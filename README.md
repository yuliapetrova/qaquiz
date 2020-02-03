# Triangle Service testing framework

The given framework has been developed for automated testing of the Triangle Service https://qa-quiz.natera.com/.

## Running the tests

You can run all tests with Maven by using the command: mvn clean test.

## Built With

* [Maven](https://maven.apache.org/) - Dependency Management
* [Rest Assured](https://github.com/rest-assured/rest-assured/wiki/Usage/) - Testing and validating REST services framework
* [JUnit5](https://junit.org/junit5/docs/current/user-guide/) - Unit testing framework


## Framework structure

```
─src/main/java
    │       
    ├───com.natera.qaquiz
    │   ├───config
    │   ├───helpers
    │   ├───models
```

**Config** 

Config package contains classes to support reading of application.yaml file with basic settings. 

**Helpers**   

Helpers package contains utility classes to work with Triangle Service method requests.

**Models**

Models package contains builder classes for REST API requests/responses.

**Tests**

```
─src/main/test
    │       
    ├───com.natera.qaquiz
    │   ├───servicetests
    │   ├───BaseTest
    │   ├───FrameworkTests
```  

Servicetests package contains tests for the Triangle Service.

FrameworkTests class is simple check of framework utility classes.

