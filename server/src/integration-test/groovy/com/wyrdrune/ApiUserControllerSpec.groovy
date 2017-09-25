/*
 * Copyright 2017 Guy Gascoigne-Piggford.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wyrdrune

import grails.gorm.transactions.Rollback
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import spock.lang.Specification
import spock.lang.Unroll

@SuppressWarnings(['MethodName', 'DuplicateNumberLiteral'])
@Integration
@Rollback
class ApiUserControllerSpec extends Specification {

  String accessToken

  RestBuilder getRestBuilder() {
    new RestBuilder()
  }

  void setup() {
    def response = restBuilder.post("http://localhost:${serverPort}/api/login") {
      accept('application/json')
      contentType('application/json')
      json {
        username = 'admin'
        password = 'password'
      }
    }
    accessToken = response.json.access_token
  }

  def 'test /api/user url is secured'() {
    when:
    def resp = restBuilder.get("http://localhost:${serverPort}/api/user") {
      accept('application/json')
      contentType('application/json')
    }

    then:
    resp.status == 401 // Unauthorized
    resp.json.status == 401
    resp.json.error == 'Unauthorized'
    resp.json.message == 'No message available'
    resp.json.path == '/api/user'
  }

  @Unroll()
  def "test a user with the role #roles gets #status accessing /api/user url"() {
    when: 'login'
    def resp = restBuilder.post("http://localhost:${serverPort}/api/login") {
      accept('application/json')
      contentType('application/json')
      json {
        username = user
        password = pass
      }
    }

    then:
    resp.status == 200 // OK
    resp.json.roles == roles

    when:
    def accessToken = resp.json.access_token
    def refresh_token = resp.json.refresh_token

    then:
    accessToken
    refresh_token

    when: 'get all users'
    resp = restBuilder.get("http://localhost:${serverPort}/api/user") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then:
    resp.status == status

    where:
    user    | pass       | roles                       | status
    'admin' | 'password' | ['ROLE_ADMIN', 'ROLE_USER'] | 200 // OK
    'user'  | 'password' | ['ROLE_USER']               | 403 // Forbidden
  }

  def "test ROLE_ADMIN is able to create a user"() {
    when:
    def resp = restBuilder.post("http://localhost:${serverPort}/api/user") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      contentType('application/json')
      json {
        username = "Captain"
        password = "Kirk"
        roles = ['ROLE_ADMIN', 'ROLE_USER']
      }
    }

    then:
    resp.status == 201

    when:
    resp = restBuilder.get("http://localhost:${serverPort}/api/user") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then:
    resp.status == 200
    resp.json == [
        [
            accountLocked  : false,
            roles          : ['ROLE_ADMIN', 'ROLE_USER'],
            accountExpired : false,
            id             : 1,
            passwordExpired: false,
            enabled        : true,
            username       : 'admin'
        ],
        [
            accountLocked  : false,
            roles          : ['ROLE_USER'],
            accountExpired : false,
            id             : 2,
            passwordExpired: false,
            enabled        : true,
            username       : 'user'
        ],
        [
            accountLocked  : false,
            roles          : ['ROLE_ADMIN', 'ROLE_USER'],
            accountExpired : false,
            id             : 3,
            passwordExpired: false,
            enabled        : true,
            username       : 'Captain'
        ]
    ]
  }

  def "test ROLE_ADMIN is able to update a user"() {
    when: 'create record'
    def resp = restBuilder.post("http://localhost:${serverPort}/api/user") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      contentType('application/json')
      json {
        username = "Mr"
        password = "Spock"
        roles = ['ROLE_ADMIN', 'ROLE_USER']
      }
    }

    then:
    resp.status == 201 // Created

    when: 'edit record'
    def id = resp.json.id

    resp = restBuilder.put("http://localhost:${serverPort}/api/user/" + id) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      contentType('application/json')
      json {
        username = "Mr"
        password = "Spock"
        roles = ['ROLE_USER']
      }
    }

    then:
    resp.status == 200 // OK

    when: 'read it'
    resp = restBuilder.get("http://localhost:${serverPort}/api/user/" + id) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then: 'verify it worked'
    resp.status == 200 // OK
    resp.json == [
        accountLocked  : false,
        roles          : ['ROLE_USER'],
        accountExpired : false,
        id             : id,
        passwordExpired: false,
        enabled        : true,
        username       : 'Mr'
    ]
  }
}
