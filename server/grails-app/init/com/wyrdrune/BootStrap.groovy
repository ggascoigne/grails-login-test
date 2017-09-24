package com.wyrdrune

import grails.util.Environment

class BootStrap {

  def init = {servletContext ->
    environments {
      production {
        generateDefaultData(servletContext)
      }

      development {
        generateDefaultData(servletContext)
      }

      test {
        // start the tests with a clean slate
      }
    }
  }

  def generateDefaultData(servletContext) {
    def adminRole = new Role(authority: 'ROLE_ADMIN').save()
    def userRole = new Role(authority: 'ROLE_USER').save()

    def testUser = new User(username: 'me', password: 'password').save()

    UserRole.create testUser, adminRole

    UserRole.withSession {
      it.flush()
      it.clear()
    }

    assert User.count() == 1
    assert Role.count() == 2
    assert UserRole.count() == 1
  }

  def destroy = {
  }
}
