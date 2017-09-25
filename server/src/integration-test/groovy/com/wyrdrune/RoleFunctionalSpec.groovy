package com.wyrdrune

import geb.spock.GebSpec
import grails.plugins.rest.client.RestBuilder
import grails.testing.mixin.integration.Integration
import grails.transaction.Rollback

import static org.springframework.http.HttpStatus.CREATED
import static org.springframework.http.HttpStatus.NO_CONTENT
import static org.springframework.http.HttpStatus.OK
import static org.springframework.http.HttpStatus.UNPROCESSABLE_ENTITY

@Integration
@Rollback
class RoleFunctionalSpec extends GebSpec {

  String accessToken

  RestBuilder getRestBuilder() {
    new RestBuilder()
  }

  String getResourcePath() {
    "${baseUrl}/api/role"
  }

  // need to differentiate the json since the authority value is unique
  Closure getValidJson(int val) {
    {->
      [authority: "new_role_${val}"]
    }
  }

  Closure getInvalidJson() {
    {->
      [authority: []]
    }
  }

  void setup() {
    def response = new RestBuilder().post("http://localhost:${serverPort}/api/login") {
      accept('application/json')
      contentType('application/json')
      json {
        username = 'admin'
        password = 'password'
      }
    }
    accessToken = response.json.access_token
  }

  void "Test the index action"() {
    when: "The index action is requested"
    def response = restBuilder.get(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then: "The response is correct"
    response.status == OK.value()
    response.json == [[authority: 'ROLE_ADMIN', id: 1], [authority: 'ROLE_USER', id: 2]]
  }

  void "Test the save action correctly persists an instance"() {
    when: "The save action is executed with no content"
    def response = restBuilder.post(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then: "The response is correct"
    response.status == UNPROCESSABLE_ENTITY.value()

    when: "The save action is executed with invalid data"
    response = restBuilder.post(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json invalidJson
    }
    then: "The response is correct"
    response.status == UNPROCESSABLE_ENTITY.value()


    when: "The save action is executed with valid data"
    response = restBuilder.post(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json getValidJson(1)
    }

    then: "The response is correct"
    response.status == CREATED.value()
    response.json.id
    Role.count() == 3
  }

  void "Test the update action correctly updates an instance"() {
    when: "The save action is executed with valid data"
    def response = restBuilder.post(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json getValidJson(2)
    }

    then: "The response is correct"
    response.status == CREATED.value()
    response.json.id

    when: "The update action is called with invalid data"
    def id = response.json.id
    response = restBuilder.put("$resourcePath/$id") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json invalidJson
    }

    then: "The response is correct"
    response.status == UNPROCESSABLE_ENTITY.value()

    when: "The update action is called with valid data"
    response = restBuilder.put("$resourcePath/$id") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json getValidJson(2)
    }

    then: "The response is correct"
    response.status == OK.value()
    response.json

  }

  void "Test the show action correctly renders an instance"() {
    when: "The save action is executed with valid data"
    def response = restBuilder.post(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json getValidJson(3)
    }

    then: "The response is correct"
    response.status == CREATED.value()
    response.json.id

    when: "When the show action is called to retrieve a resource"
    def id = response.json.id
    response = restBuilder.get("$resourcePath/$id") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then: "The response is correct"
    response.status == OK.value()
    response.json.id == id
  }

  void "Test the delete action correctly deletes an instance"() {
    when: "The save action is executed with valid data"
    def response = restBuilder.post(resourcePath) {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
      json getValidJson(4)
    }

    then: "The response is correct"
    response.status == CREATED.value()
    response.json.id

    when: "When the delete action is executed on an unknown instance"
    def id = response.json.id
    response = restBuilder.delete("$resourcePath/99999") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then: "The response is correct"
    response.status == NO_CONTENT.value()

    when: "When the delete action is executed on an existing instance"
    response = restBuilder.delete("$resourcePath/$id") {
      accept('application/json')
      header('Authorization', "Bearer ${accessToken}")
    }

    then: "The response is correct"
    response.status == NO_CONTENT.value()
    !Role.get(id)
  }
}