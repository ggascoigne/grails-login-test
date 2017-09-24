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

import grails.plugin.json.view.test.JsonViewTest
import grails.test.hibernate.HibernateSpec

class UserViewSpec extends HibernateSpec implements JsonViewTest {
  void "Test render of User GSON view"() {
    when: "A gson view is rendered for a User instance"
    def role = new Role(authority: 'ROLE_USER_1').save()
    def user = new User(username: 'me', password: 'password').save()
    UserRole.create user, role
    UserRole.withSession {
      it.flush()
      it.clear()
    }

    def result = render(template: "/user/user", model: [user: user])

    then: "The json is correct"
    result.json == [
        id             : 1,
        username       : 'me',
        passwordExpired: false,
        accountLocked  : false,
        accountExpired : false,
        enabled        : true,
        roles          : ['ROLE_USER_1']
    ]
  }

  void "Test render of User GSON view with 2 roles"() {
    when: "A gson view is rendered for a User instance"
    def role1 = new Role(authority: 'ROLE_USER_1').save()
    def role2 = new Role(authority: 'ROLE_USER_2').save()
    def user = new User(username: 'me', password: 'password').save()
    UserRole.create user, role1
    UserRole.create user, role2
    UserRole.withSession {
      it.flush()
      it.clear()
    }

    def result = render(template: "/user/user", model: [user: user])

    then: "The json is correct"
    result.json == [
        id             : 2,
        username       : 'me',
        passwordExpired: false,
        accountLocked  : false,
        accountExpired : false,
        enabled        : true,
        roles          : ['ROLE_USER_1', 'ROLE_USER_2']
    ]
  }
}