package com.wyrdrune

import grails.testing.mixin.integration.Integration
import grails.gorm.transactions.Rollback
import spock.lang.Specification
import org.hibernate.SessionFactory

@Integration
@Rollback
class RoleServiceSpec extends Specification {

  RoleService roleService
  SessionFactory sessionFactory

  private Long setupData() {
    def adminRole = new Role(authority: 'ROLE_ADMIN').save()
    new Role(authority: 'ROLE_USER_1').save()
    new Role(authority: 'ROLE_USER_2').save()
    new Role(authority: 'ROLE_USER_3').save()
    new Role(authority: 'ROLE_USER_4').save()
    adminRole.id
  }

  void "test get"() {
    setupData()

    expect:
    roleService.get(1) != null
  }

  void "test list"() {
    setupData()

    when:
    List<Role> roleList = roleService.list(max: 2, offset: 2)

    then:
    roleList.size() == 2
    assert roleList.get(0).authority == 'ROLE_USER_2'
    assert roleList.get(1).authority == 'ROLE_USER_3'
  }

  void "test count"() {
    setupData()

    expect:
    roleService.count() == 5
  }

  void "test delete"() {
    Long roleId = setupData()

    expect:
    roleService.count() == 5

    when:
    roleService.delete(roleId)
    sessionFactory.currentSession.flush()

    then:
    roleService.count() == 4
  }

  void "test save"() {
    when:
    Role role = new Role(authority: 'foo')
    roleService.save(role)

    then:
    role.id != null
  }
}
