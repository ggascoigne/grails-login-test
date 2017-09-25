package com.wyrdrune

import grails.gorm.transactions.Rollback
import grails.testing.mixin.integration.Integration
import org.hibernate.SessionFactory
import spock.lang.Specification

@Integration
@Rollback
class UserServiceSpec extends Specification {

  UserService userService
  SessionFactory sessionFactory

  private Long setupData() {
    new User(username: 'me1', password: 'password').save(flush: true, failOnError: true)
    new User(username: 'me2', password: 'password').save(flush: true, failOnError: true)
    def user = new User(username: 'me3', password: 'password').save(flush: true, failOnError: true)
    new User(username: 'me4', password: 'password').save(flush: true, failOnError: true)
    new User(username: 'me5', password: 'password').save(flush: true, failOnError: true)

    user.id
  }

  void "test get"() {
    setupData()

    expect:
    userService.get(1) != null
  }

  void "test list"() {
    setupData()

    when:
    List<User> userList = userService.list(max: 2, offset: 2)

    then:
    userList.size() == 2
  }

  void "test count"() {
    setupData()

    expect:
    userService.count() >= 7
  }

  void "test delete"() {
    Long userId = setupData()
    long count = userService.count()

    expect:
    count >= 7

    when:
    userService.delete(userId)
    sessionFactory.currentSession.flush()

    then:
    userService.count() == count - 1
  }

  void "test save"() {
    when:
    User user = new User(username: 'a', password: 'b')
    userService.save(user)

    then:
    user.id != null
  }
}
