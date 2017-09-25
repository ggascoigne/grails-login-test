package com.wyrdrune

class BootStrap {

  def init = {servletContext ->
    def authorities = ['ROLE_ADMIN', 'ROLE_USER']
    authorities.each {String authority ->
      if (!Role.findByAuthority(authority)) {
        new Role(authority: authority).save()
      }
    }

    if (!User.findByUsername('admin')) {
      def u = new User(username: 'admin', password: 'password', roles: authorities)
      u.save()
    }

    if (!User.findByUsername('user')) {
      def u = new User(username: 'user', password: 'password', roles: ['ROLE_USER'])
      u.save()
    }

    UserRole.withSession {
      it.flush()
      it.clear()
    }

    assert User.count() == 2
    assert Role.count() == 2
    assert UserRole.count() == 3
  }

  def destroy = {
  }
}
