# Grails & React

Created using the react profile, see:

http://grailsblog.objectcomputing.com/posts/2016/11/14/introducing-the-react-profile-for-grails.html

The latest react profile can be found by looking here: 

https://repo.grails.org/grails/core/org/grails/profiles/react/

## Execution

If you just want to run the whole thing then you can `./gradlew bootRun -parallel`

alternatively you can

```bash 
$ ./gradlew server:bootRun
   
 //in another terminal

$ ./gradlew client:bootRun
```

Basically you've got a standard grails app in the server folder. and a standard Create-React-App in the client one.

So you can also just do:

```bash
$ cd server
$ grails run-app
```

and in another terminal

```bash
$ cd client
$ yarn start
```

## Creation

```bash
$ grails create-app com.wyrdrune.loginTest --profile=org.grails.profiles:react:2.0.3
$ grails s2-quickstart com.wyrdrune User Role

```