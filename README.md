# Standardization Service

A microservice that acts as the standardization layer in the feed processing pipeline, transforming incoming feed data
from various providers into a consistent internal format that can be used throughout the rest of the system.

## Project Structure

The Standardization Service consists of three main modules:

### 1. Commons Module

Contains shared utilities, common dependencies, and helper classes used across the service. This currently 
contains only the encryption utilities

### 2. Auth Module

Provides OAuth2 token generation for **machine to machine** communication. This module generates an OAuth2 access token that 
can be used to authenticate requests to the Standardization Service.

### 3. Standardization Application

The main application module containing the entry point and controllers. This module:

- Processes and standardizes data from different providers (Alpha and Beta)
- Exposes REST endpoints for data ingestion
- Transforms provider specific formats into a standardized format

## Prerequisites

- Java 21
- Maven 3.6+ or Maven Wrapper (included)
- Spring Boot 3.5.4

## Setup and Installation

### Clone the Repository

```bash
git clone <repository-url>
cd standardization-service
```

### Build the Project

Build the entire project with all modules:

```bash
# Using Maven Wrapper
  ./mvnw clean install

# Or using Maven
  mvn clean install
```

if you encounter any issues building the entire project, try building the modules individually in the below order:
commons, auth-module, standardization-application.

```bash
# Using Maven Wrapper
  ./mvnw clean install -pl commons,auth-module,standardization-application
  
# Or using Maven
  mvn clean install -pl commons,auth-module,standardization-application
 ```

## Running Tests

### Running All Tests

```bash
# Using Maven Wrapper
  ./mvnw clean test

# Or using Maven
  mvn clean test
```

## Running the Application

### Start the Application

```bash
# Using Maven Wrapper
  ./mvnw spring-boot:run -pl standardization-application

# Or using Maven
mvn spring-boot:run -pl standardization-application
```

The application will be running on port 8083 by default.

## API Endpoints

### Authentication

- **POST /oauth/token**: Generate OAuth2 access token for machine-to-machine communication
    - Consumes: `application/json`
    - client_id: ARwdsvCGmeerDq9xeQg9MtGAnky4eHhL
    - client_secret: f1fLjcTMkh-WsRP0efBNpQGQB3191B-ihmmYTyOJnstESYGuE1CZuGN9OOFUvup
      
full request body for requesting OAuth2 access token
```json
{
  "grant_type": "client_credentials",
  "client_id": "ARwdsvCGmeerDq9xeQg9MtGAnky4eHhL",
  "client_secret": "f1fLjcTMkh-WsRP0efBNpQGQB3191B-ihmmYTyOJnstESYGuE1CZuGN9OOFUvup",
  "audience": "https://standardization-service.com"
}
```

### Provider Alpha Integration

- **POST /provider-alpha/feed**: Ingest data from Provider Alpha
    - Consumes: `application/json`
    - Request body: AlphaMsg
    - Response: 200 OK on success

### Provider Beta Integration

- **POST /provider-beta/feed**: Ingest data from Provider Beta
    - Consumes: `application/json`
    - Request body: BetaMsg
    - Response: 200 OK on success

## Troubleshooting

If you encounter any issues:

1. Ensure Java 21 is installed and set as the active JDK
2. Verify all dependencies are resolved correctly
3. Check application logs for detailed error messages