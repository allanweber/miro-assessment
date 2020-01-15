# Widgets

## Run the app

* _mvn spring-boot:run_    

**To Test the app**

* _mvn test_  

**The application has swagger on http://localhost:8080/**

**There is a postman collection called Miro.postman_collection.json with some requests**

## Stack
* Java 11
* Webflux
* Spring Boot
* JUnit5
* swagger + swagger-ui
* Unit and Integrated tests
* Actuator
* Lombok
* Map Struct
* PMD

## Structure
### Api
Controllers and their interfaces (to document swagger)

### Domain
Business rules related to widgets management

### Infrastructure
Concrete implementations, for example InMemoryRepository  
All Spring configurations, handlers and etc.  
All logic related to api limits


