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

import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import org.springframework.util.LinkedMultiValueMap
import org.springframework.util.MultiValueMap
import spock.lang.Specification

@SuppressWarnings(['MethodName', 'DuplicateNumberLiteral'])
@Integration
@Rollback
class ApiLoginControllerSpec extends Specification {

  def "test an invalid login to /api/login"() {
    when: 'login with the admin/wrong password'
    def resp = login('admin', 'incorrect')

    then:
    resp.status == 401 // Unauthorized
    !resp.json

    when: 'login with the admin/wrong password'
    resp = login('incorrect', 'password')

    then:
    resp.status == 401 // Unauthorized
    !resp.json
  }

  def "test that admin can login to /api/login"() {
    when: 'login with the admin'
    def resp = login('admin', 'password')

    then:
    resp.status == 200 // OK
    resp.json.roles.find {it == 'ROLE_ADMIN'}
    resp.json.roles.find {it == 'ROLE_USER'}

    when:
    def accessToken = resp.json.access_token
    def refresh_token = resp.json.refresh_token

    then:
    accessToken
    refresh_token
    resp.json.username == 'admin'
    resp.json.token_type == 'Bearer'
    resp.json.expires_in == 3600

    when: 'use refresh token to get new access token'

    RestBuilder rest = new RestBuilder()
    MultiValueMap<String, String> form = new LinkedMultiValueMap<String, String>()
    form.add("grant_type", "refresh_token")
    form.add("refresh_token", (String) refresh_token)
    resp = rest.post("http://localhost:${serverPort}/oauth/access_token") {
      accept("application/json")
      contentType("application/x-www-form-urlencoded")
      body(form)
    }

    then:
    resp.status == 200 // OK
    resp.json.roles.find {it == 'ROLE_ADMIN'}
    resp.json.roles.find {it == 'ROLE_USER'}

    when:
    accessToken = resp.json.access_token
    refresh_token = resp.json.refresh_token

    then:
    accessToken
    refresh_token
    resp.json.username == 'admin'
    resp.json.token_type == 'Bearer'
    resp.json.expires_in == 3600
  }

  def login(u, p) {
    new RestBuilder().post("http://localhost:${serverPort}/api/login") {
      accept('application/json')
      contentType('application/json')
      json {
        username = u
        password = p
      }
    }
  }
}
