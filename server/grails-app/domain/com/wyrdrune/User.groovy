package com.wyrdrune

import grails.compiler.GrailsCompileStatic
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@GrailsCompileStatic
@EqualsAndHashCode(includes = 'username')
@ToString(includes = 'username', includeNames = true, includePackage = false)
class User implements Serializable {

  private static final long serialVersionUID = 1

  String username
  String password
  boolean enabled = true
  boolean accountExpired
  boolean accountLocked
  boolean passwordExpired

  Set<Role> getAuthorities() {
//    Set<Role> authorities = (UserRole.findAllByUser(this) as List<UserRole>).collect { Role.get(it.role.id) } as Set<Role>
    Set<Role> authorities = (UserRole.findAllByUser(this) as List<UserRole>)*.role as Set<Role>
    authorities
  }

  static constraints = {
    password nullable: false, blank: false, password: true
    username nullable: false, blank: false, unique: true
  }

  static mapping = {
    password column: '`password`'
  }
}
