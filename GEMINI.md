# Gemini Project Guidance: Iot-Project

This document provides a high-level overview of the `Iot-Project` to guide Gemini CLI in understanding its architecture, conventions, and key components.

## 1. Core Technologies

- **Framework:** Spring Boot
- **Language:** Java
- **Build Tool:** Maven (`pom.xml`)
- **Databases:**
  - **Relational (JPA):** Used for core user and permission management (`User`, `Role`, `Permission`). Assumed to be configured in `application.yaml`.
  - **NoSQL (MongoDB):** Used for IoT-specific data like sensor readings and device logs (`Classroom`, `ControlLog`, `SensorReading`).
- **Authentication:** Spring Security with JSON Web Tokens (JWT).
- **Real-time Communication:** MQTT for IoT device integration (`MqttConfig`, `MqttMessageHandlerService`).

## 2. Project Structure

The project follows a standard Spring Boot layered architecture:

- `src/main/java/com/example/Iot_Project/`: Root package.
  - `configuration/`: Spring Security, MQTT, and application configurations.
  - `controller/`: REST API endpoints. Handles HTTP requests.
  - `service/`: Contains business logic.
  - `repository/`: Data access layer.
    - `jpa/`: Repositories for relational data (e.g., `UserRepository`).
    - `mongo/`: Repositories for MongoDB documents (e.g., `ClassroomRepository`).
  - `entity/`: JPA entity classes for the relational database.
  - `document/`: Document classes for MongoDB.
  - `dto/`: Data Transfer Objects for API requests and responses.
  - `mapper/`: MapStruct or manual mappers for converting between DTOs and entities/documents.
  - `enums/`: Enumerations for status, types, modes, etc.
  - `exception/`: Global exception handling.

## 3. Key Workflows & Conventions

- **API Design:** RESTful services using DTOs for request/response payloads. Responses are wrapped in a common `ApiResponse` structure.
- **Entity vs. Document:**
  - Use JPA `entity` classes for user/auth-related data.
  - Use MongoDB `document` classes for time-series or unstructured IoT data.
- **Business Logic:** Encapsulated within `@Service` classes.
- **Authentication:** Handled by `AuthenticationService` and enforced by `SecurityConfig`.
- **Testing:** Use JUnit for unit and integration tests. Test files are located in `src/test/java`.
- **IoT Integration:** MQTT messages are received and processed by `MqttMessageHandlerService`. Automatic control logic resides in `service/auto/`.

## 4. How to Run & Test

- **Run Application:** Execute the main class `IotProjectApplication.java` or use the Maven command `./mvnw spring-boot:run`.
- **Run Tests:** Use the Maven command `./mvnw test`. See `TEST_GUIDE.md` for more details.
