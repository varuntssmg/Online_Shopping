# Online Store -- Spring Boot Application

A simple online shopping application built with **Java, Spring Boot,
Spring Data JPA, and a browser-based frontend**. The project
demonstrates how a Spring Boot application connects web pages,
controllers, services, repositories, and a database to support product
browsing and checkout.

## Features

-   Browser-based online store homepage.
-   Product data access using Spring Data JPA repositories.
-   Checkout flow that accepts customer and credit-card details.
-   Credit-card validation through a dedicated validation service.
-   Rejection of invalid-format and demo stolen-card numbers.
-   Global exception handling using Spring MVC `@ControllerAdvice`.
-   Configurable maximum purchase quantity.
-   H2 in-memory database for local development.
-   Spring Boot Actuator endpoints for application monitoring.

## Technology Stack

  Technology              Usage
  ----------------------- ------------------------------------------
  Java 21                 Application runtime
  Spring Boot 2.5.0       Application framework
  Spring MVC              Web request handling
  Spring Data JPA         Persistence layer
  Hibernate               ORM implementation
  H2 Database             In-memory development database
  Maven                   Build and dependency management
  HTML, CSS, JavaScript   Browser-based frontend
  Spring Boot Actuator    Health and application metrics endpoints

> **Compatibility note:** This project uses Spring Boot 2.5 and the
> `javax.*` Servlet API. It is not a Spring Boot 3 project without
> dependency and import upgrades.

## Prerequisites

Install the following before running the application:

-   JDK 21 (or a compatible JDK configured for the project)
-   Maven 3.6+ (or use the Maven Wrapper if it is included)
-   IntelliJ IDEA, Eclipse, or another Java IDE
-   A modern browser

Verify your Java and Maven installations:

``` bash
java -version
mvn -version
```

## Getting Started

### 1. Clone the repository

``` bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd <PROJECT_DIRECTORY>
```

Replace the placeholders with your repository URL and folder name.

### 2. Open the project

Open the project folder in IntelliJ IDEA (or your preferred IDE), then
allow Maven to import dependencies.

### 3. Configure the application

The main configuration file is:

``` text
src/main/resources/application.properties
```

The project uses an H2 in-memory database. The relevant settings
include:

``` properties
spring.h2.console.enabled=true
spring.h2.console.path=/h2

spring.datasource.url=jdbc:h2:mem:in-memory-db;DB_CLOSE_DELAY=-1
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=admin
spring.datasource.password=

spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always

products.service.max-number-of-items=10
```

The `products.service.max-number-of-items` property controls the maximum
total quantity allowed in one purchase. Change it to suit your local
test scenario.

**Important:** H2 is configured as an in-memory database. Data is
temporary and is recreated when the application starts because
`spring.jpa.hibernate.ddl-auto=create-drop` is enabled.

### 4. Run the application

From the project root, run:

``` bash
mvn spring-boot:run
```

If the Maven Wrapper is included, you can use:

**Windows**

``` bat
mvnw.cmd spring-boot:run
```

**macOS / Linux**

``` bash
./mvnw spring-boot:run
```

Alternatively, run the main class:

``` text
online.store.Application
```

### 5. Open the application

After the application starts, open:

-   Store homepage: http://localhost:8080/
-   Checkout page: http://localhost:8080/checkout/checkout.html
-   H2 database console: http://localhost:8080/h2
-   Actuator health: http://localhost:8080/actuator/health

For the H2 console, use the JDBC URL, username, and password configured
in `application.properties`.

## Checkout Validation

The checkout flow uses `CreditCardValidationService` to validate the
submitted card number before orders are saved.

The following are **project demo test values only**; they are not real
payment-card credentials:

  Test value           Expected behavior
  -------------------- ------------------------------------------
  `1111222233334444`   Accepted by the project's demo validator
  `9999888877776666`   Rejected as a demo stolen-card number
  `abcd`               Rejected as an invalid card format

The checkout controller also checks required customer and payment
fields. Do not enter real payment-card details into this demonstration
application.

## Purchase Quantity Limit

The maximum total quantity is configured externally:

``` properties
products.service.max-number-of-items=10
```

The `OrdersService` reads this setting using Spring's `@Value` injection
and checks the sum of item quantities before calling the repository save
operation.

For a configured limit of 10:

-   A checkout containing 10 total items is within the limit.
-   A checkout containing 11 total items is rejected.
-   The limit applies to the sum of quantities, not just the number of
    distinct product rows.

The service has a fallback value of 25 if the property is not provided.

## Exception Handling

The project demonstrates two levels of exception handling:

-   **Checkout-specific handling:** `CheckoutController` handles
    `CreditCardValidationException` and returns a user-friendly HTTP 400
    response.
-   **Global handling:** `GlobalControllerExceptionHandler`, annotated
    with `@ControllerAdvice`, handles uncaught exceptions and returns an
    HTTP 500 response containing the exception message.

The browser checkout JavaScript can display the backend response body to
help the user understand why checkout failed.

## Project Structure

The source and resource folders follow the standard Maven layout. The
main package is `online.store`.

``` text
src/
└── main/
    ├── java/
    │   └── online/
    │       └── store/
    │           ├── Application.java
    │           ├── controllers/
    │           ├── services/
    │           ├── repositories/
    │           ├── model/
    │           └── exceptions/
    └── resources/
        ├── application.properties
        └── public/
            ├── index.html
            └── checkout/
                ├── checkout.html
                ├── checkout.js
                ├── form-validation.css
                └── form-validation.js
```

The exact classes in each package may vary as the project evolves. The
key responsibilities are:

  Layer                Responsibility
  -------------------- -------------------------------------------------------
  Controllers          Receive browser requests and return responses
  Services             Apply business rules, checkout, and validation
  Repositories         Read and write application data using Spring Data JPA
  Model                Represent application entities and request data
  Exceptions           Define application-specific exception types
  `resources/public`   Serve static HTML, CSS, and JavaScript files

## Build and Test

Compile and package the application:

``` bash
mvn clean package
```

Run tests:

``` bash
mvn test
```

If the project includes the Maven Wrapper, use `mvnw.cmd` on Windows or
`./mvnw` on macOS/Linux.

## Troubleshooting

### Port 8080 is already in use

Stop the other process using port 8080, or configure a different port:

``` properties
server.port=8081
```

Then open the application using `http://localhost:8081/`.

### Checkout page returns 404

Confirm the files are located under:

``` text
src/main/resources/public/checkout/
```

The expected page URL is:

``` text
http://localhost:8080/checkout/checkout.html
```

### Purchase quantity validation does not use the expected limit

1.  Check `products.service.max-number-of-items` in
    `application.properties`.
2.  Save the file.
3.  Restart the Spring Boot application.
4.  Add the quantities of all cart items and compare the total with the
    configured limit.

### H2 console cannot connect

Use the exact JDBC URL and credentials from `application.properties`:

``` text
JDBC URL: jdbc:h2:mem:in-memory-db
User Name: admin
Password: (leave blank)
```

## Notes and Limitations

-   This is a learning/demo project, not a production-ready e-commerce
    or payment system.
-   The card validation values are hard-coded demo scenarios, not a real
    payment gateway integration.
-   H2 in-memory data does not persist across application restarts.
-   The global exception handler exposes exception messages in HTTP
    responses for demonstration. Production applications should return
    controlled error responses and avoid exposing internal details.
-   Actuator exposure is configured broadly for local development.
    Review and secure these endpoints before deploying the application.

## Learning Outcomes

This project is useful for understanding:

-   Spring Boot application startup and component scanning.
-   Dependency injection and constructor-based service dependencies.
-   Spring MVC controllers and exception handling.
-   Layered application design.
-   Spring Data JPA repositories and Hibernate.
-   H2 database configuration.
-   Externalized configuration with `application.properties`.
-   Checkout validation and business-rule enforcement.
-   Serving static frontend resources from Spring Boot.

------------------------------------------------------------------------

## License

Add the license that applies to this repository before distributing or
reusing the project.
