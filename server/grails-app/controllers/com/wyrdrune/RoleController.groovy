package com.wyrdrune

import grails.validation.ValidationException
import static org.springframework.http.HttpStatus.*

class RoleController {

  RoleService roleService

  static responseFormats = ['json', 'xml']
  static allowedMethods = [save: "POST", update: "PUT", delete: "DELETE"]

  def index(Integer max) {
    params.max = Math.min(max ?: 10, 100)
    respond roleService.list(params), model: [roleCount: roleService.count()]
  }

  def show(Long id) {
    respond roleService.get(id)
  }

  def save(Role role) {
    if (role == null) {
      render status: NOT_FOUND
      return
    }

    try {
      roleService.save(role)
    } catch (ValidationException e) {
      respond role.errors, view: 'create'
      return
    }

    respond role, [status: CREATED, view: "show"]
  }

  def update(Role role) {
    if (role == null) {
      render status: NOT_FOUND
      return
    }

    try {
      roleService.save(role)
    } catch (ValidationException e) {
      respond role.errors, view: 'edit'
      return
    }

    respond role, [status: OK, view: "show"]
  }

  def delete(Long id) {
    if (id == null) {
      render status: NOT_FOUND
      return
    }

    roleService.delete(id)

    render status: NO_CONTENT
  }
}
