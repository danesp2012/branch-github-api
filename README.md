
# Spring Boot REST API For GitHub Users

This is a Spring Boot REST API that offers a simple interface to retrieve a limited set of information about a GitHub
user and their repositories

## How to Run

1. Clone from repository to local
2. Ensure that you have Java 21 or higher installed.
3. Navigate to the project root directory.
4. Run the application from the command line using `./gradlew bootRun`.
   (Or import the gradle based project into your compatible IDE)
5. Access swagger documentation and test endpoints at http://localhost:8080/swagger-ui/index.html
6. To shut down the app POST to /actuator/shutdown `curl -X POST http://localhost:8080/actuator/shutdown`

## Caveats and Considerations
This app uses a domain based file infrastructure and layers should be built within the domains eg...

```
github
├── GithubApplication.java
├── User
...   └──Controller
        └── UserController.java
      └──Service
        └── User.java
        └── UserService.java

```
Response objects should be added to the controller layer, 
and a new layer should be created if persistence is necessary

The cache implementation is the out-of-the-box Spring Boot ConcurrentMap implementation
and replacement implementations should be considered for production

Spring Boot Actuator has been added to the project but only the shutdown endpoint is exposed and enabled

Consider some form of auth in the future especially to lockdown actuator endpoints

Lombok annotations are used consistently to create builders for DTOs and constructors for classes. 