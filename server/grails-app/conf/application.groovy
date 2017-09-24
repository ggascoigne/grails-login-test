grails.plugin.springsecurity.logout.postOnly = false

// Added by the Spring Security Core plugin:
grails.plugin.springsecurity.userLookup.userDomainClassName = 'com.wyrdrune.User'
grails.plugin.springsecurity.userLookup.authorityJoinClassName = 'com.wyrdrune.UserRole'
grails.plugin.springsecurity.authority.className = 'com.wyrdrune.Role'

grails.plugin.springsecurity.controllerAnnotations.staticRules = [
    [pattern: '/', access: ['permitAll']],
    [pattern: '/error', access: ['permitAll']],
    [pattern: '/index', access: ['permitAll']],
    [pattern: '/index.gsp', access: ['permitAll']],
    [pattern: '/shutdown', access: ['permitAll']],
    [pattern: '/assets/**', access: ['permitAll']],
    [pattern: '/**/js/**', access: ['permitAll']],
    [pattern: '/**/css/**', access: ['permitAll']],
    [pattern: '/**/images/**', access: ['permitAll']],
    [pattern: '/**/favicon.ico', access: ['permitAll']],
    [pattern: '/api/login', access: ['permitAll']],
    [pattern: '/api/logout', access: ['isFullyAuthenticated()']],
    [pattern: '/api/product', access: ['isFullyAuthenticated()']],
    [pattern: '/**', access: ['isFullyAuthenticated()']]

]

grails.plugin.springsecurity.filterChain.chainMap = [
    [pattern: '/api/**', filters: 'JOINED_FILTERS,-anonymousAuthenticationFilter,-exceptionTranslationFilter,-authenticationProcessingFilter,-securityContextPersistenceFilter,-rememberMeAuthenticationFilter'],
    [pattern: '/assets/**', filters: 'none'],
    [pattern: '/**/js/**', filters: 'none'],
    [pattern: '/**/css/**', filters: 'none'],
    [pattern: '/**/images/**', filters: 'none'],
    [pattern: '/**/favicon.ico', filters: 'none'],
    [pattern: '/**', filters: 'JOINED_FILTERS,-restTokenValidationFilter,-restExceptionTranslationFilter, -rememberMeAuthenticationFilter']
]

