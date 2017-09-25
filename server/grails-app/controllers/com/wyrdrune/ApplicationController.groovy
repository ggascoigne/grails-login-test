package com.wyrdrune

import grails.core.GrailsApplication
import grails.plugins.GrailsPluginManager
import grails.plugins.PluginManagerAware

import grails.plugin.springsecurity.annotation.Secured

@Secured('ROLE_USER')
class ApplicationController implements PluginManagerAware {

  GrailsApplication grailsApplication
  GrailsPluginManager pluginManager

  def index() {
    [grailsApplication: grailsApplication, pluginManager: pluginManager]
  }
}
