# Cloud Vendor REST API

A Spring Boot REST API application for managing cloud vendor information with full CRUD operations, JUnit 5 testing, and MySQL database integration.

## Table of Contents

- [Overview](#overview)
- [Technologies Used](#technologies-used)
- [Features](#features)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Database Configuration](#database-configuration)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Testing](#testing)
- [Project Structure](#project-structure)
- [API Documentation](#api-documentation)

## Overview

This project is a RESTful web service built with Spring Boot that allows users to manage cloud vendor information. It supports creating, reading, updating, and deleting cloud vendor records stored in a MySQL database.

## Technologies Used

- **Java 17**
- **Spring Boot 3.5.5**
- **Spring Data JPA** - For database operations
- **Spring Web** - For RESTful web services
- **MySQL** - Production database
- **H2 Database** - In-memory database for testing
- **JUnit 5** - Unit and integration testing
- **Mockito** - Mocking framework for testing
- **Maven** - Dependency management and build tool
- **Jackson** - JSON processing

## Features

- ✅ Create new cloud vendor records
- ✅ Retrieve individual vendor details by ID
- ✅ Retrieve all cloud vendors
- ✅ Update existing vendor information
- ✅ Delete vendor records
- ✅ Search vendors by name
- ✅ Custom exception handling
- ✅ Comprehensive unit and integration tests
- ✅ RESTful API design

## Prerequisites

Before you begin, ensure you have the following installed:

- Java Development Kit (JDK) 17 or higher
- Maven 3.6 or higher
- MySQL 8.0 or higher
- Your favorite IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

## Installation

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd cloudvendor
   ```

2. **Build the project:**
   ```bash
   mvn clean install
   ```

## Database Configuration

1. **Create MySQL Database:**
   ```sql
   CREATE DATABASE cloud_vendor_db;
   ```

2. **Configure Database Connection:**

   Update `src/main/resources/application.properties`:
   ```properties
   # MySQL Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/cloud_vendor_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

   # JPA Configuration
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
   spring.jpa.properties.hibernate.format_sql=true
   ```

3. **For Testing (Optional):**

   Create `src/test/resources/application.properties`:
   ```properties
   # H2 In-Memory Database for Testing
   spring.datasource.url=jdbc:h2:mem:testdb
   spring.datasource.driver-class-name=org.h2.Driver
   spring.jpa.hibernate.ddl-auto=create-drop
   spring.jpa.show-sql=true
   ```

## Running the Application

1. **Using Maven:**
   ```bash
   mvn spring-boot:run
   ```

2. **Using Java:**
   ```bash
   mvn clean package
   java -jar target/cloudvendor-0.0.1-SNAPSHOT.jar
   ```

3. **From IDE:**
    - Run the `CloudvendorApplication.java` main class

The application will start on `http://localhost:8080`

## API Endpoints

### Base URL: `http://localhost:8080/cloudvendor`

| Method | Endpoint | Description | Request Body |
|--------|----------|-------------|--------------|
| GET | `/{vendorId}` | Get vendor by ID | - |
| GET | `/` | Get all vendors | - |
| POST | `/` | Create new vendor | CloudVendor JSON |
| PUT | `/` | Update vendor | CloudVendor JSON |
| DELETE | `/{vendorId}` | Delete vendor | - |

### Request/Response Examples

#### Create Cloud Vendor (POST `/cloudvendor/`)
**Request Body:**
```json
{
  "vendorId": "1",
  "vendorName": "Amazon",
  "vendorAddress": "USA",
  "vendorPhoneNumber": "1234567890"
}
```

**Response:**
```
Cloud Vendor Created successfully!
```

#### Get Cloud Vendor (GET `/cloudvendor/1`)
**Response:**
```json
{
  "message": "Requested Vendor Details are Given here",
  "status": "OK",
  "data": {
    "vendorId": "1",
    "vendorName": "Amazon",
    "vendorAddress": "USA",
    "vendorPhoneNumber": "1234567890"
  }
}
```

#### Get All Cloud Vendors (GET `/cloudvendor/`)
**Response:**
```json
[
  {
    "vendorId": "1",
    "vendorName": "Amazon",
    "vendorAddress": "USA",
    "vendorPhoneNumber": "1234567890"
  },
  {
    "vendorId": "2",
    "vendorName": "Oracle",
    "vendorAddress": "SA",
    "vendorPhoneNumber": "0987654321"
  }
]
```

#### Update Cloud Vendor (PUT `/cloudvendor/`)
**Request Body:**
```json
{
  "vendorId": "1",
  "vendorName": "Amazon Web Services",
  "vendorAddress": "USA",
  "vendorPhoneNumber": "1234567890"
}
```

**Response:**
```
Cloud Vendor Updated successfully!
```

#### Delete Cloud Vendor (DELETE `/cloudvendor/1`)
**Response:**
```
Cloud Vendor Deleted successfully!
```

## Testing

The project includes comprehensive unit and integration tests using JUnit 5 and Mockito.

### Run All Tests:
```bash
mvn test
```

### Run Specific Test Class:
```bash
mvn test -Dtest=CloudVendorControllerTest
```

### Test Coverage:
- **Controller Tests**: Testing REST endpoints with MockMvc
- **Service Tests**: Testing business logic with mocked repositories
- **Repository Tests**: Testing data access layer

### Test Structure:
```
src/test/java/com/spring/cloudvendor/
├── controller/
│   └── CloudVendorControllerTest.java
├── service/
│   └── CloudVendorServiceTest.java
└── repository/
    └── CloudVendorRepositoryTest.java
```

## Project Structure

```
cloudvendor/
├── src/
│   ├── main/
│   │   ├── java/com/spring/cloudvendor/
│   │   │   ├── controller/
│   │   │   │   └── CloudVendorController.java
│   │   │   ├── model/
│   │   │   │   └── CloudVendor.java
│   │   │   ├── repository/
│   │   │   │   └── CloudVendorRepository.java
│   │   │   ├── service/
│   │   │   │   ├── CloudVendorService.java
│   │   │   │   └── impl/
│   │   │   │       └── CloudVendorServiceImpl.java
│   │   │   ├── exception/
│   │   │   │   ├── CloudVendorNotFoundException.java
│   │   │   │   └── CloudVendorExceptionHandler.java
│   │   │   ├── response/
│   │   │   │   └── ResponseHandler.java
│   │   │   └── CloudvendorApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       ├── java/com/spring/cloudvendor/
│       │   ├── controller/
│       │   │   └── CloudVendorControllerTest.java
│       │   └── service/
│       │       └── CloudVendorServiceTest.java
│       └── resources/
│           └── application.properties
|               application.yaml
├── pom.xml
└── README.md
```

## API Documentation

For interactive API documentation, you can add SpringDoc OpenAPI:

1. **Add dependency to `pom.xml`:**
   ```xml
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
       <version>2.2.0</version>
   </dependency>
   ```

2. **Access Swagger UI:**
   ```
   http://localhost:8080/swagger-ui/index.html
   ```

3. **API Docs JSON:**
   ```
   http://localhost:8080/v3/api-docs
   ```

## Common Issues and Solutions

### Issue: Connection to MySQL fails
**Solution:** Ensure MySQL is running and credentials in `application.properties` are correct.

### Issue: Port 8080 already in use
**Solution:** Change the port in `application.properties`:
```properties
server.port=8081
```

### Issue: Tests fail with database connection errors
**Solution:** Ensure H2 dependency is in test scope and test properties file exists.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/YourFeature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/YourFeature`)
5. Open a Pull Request


## Contact

For questions or support, please contact [muthulamuvhulawavhofhololani@gmail.com]

---
