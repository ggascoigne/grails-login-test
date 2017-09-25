package com.wyrdrune

import grails.compiler.GrailsCompileStatic
import grails.databinding.BindUsing
import grails.databinding.BindingHelper
import grails.databinding.DataBindingSource
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

//@GrailsCompileStatic
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

  static hasMany = [roles: UserRole]

  @BindUsing({User obj, source ->
    //source is DataBindingSource which is similar to a Map
    //and defines getAt operation but source.name cannot be used here.
    //In order to get name from source use getAt instead as shown below.
    List<String> roleNames = source['roles'] as List<String>
    obj.roles.each { UserRole userRole ->
      if (!roleNames.contains(userRole.role.authority)){
        userRole.delete()
      }
    }
    obj.save()
    roleNames?.collect { String roleName ->
      Role role = Role.findByAuthority(roleName)
      UserRole.findOrCreateByUserAndRole((User)obj, role)
    }
  })
  Collection<UserRole> roles

  Set<Role> getAuthorities() {
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
