# CRUD App

This project is a simple CRUD (Create, Read, Update, Delete) application built with Java and Spring Boot. It demonstrates basic backend operations and can be connected to a frontend for managing student data.

## Features
- RESTful API for CRUD operations
- Spring Boot framework for rapid development
- Maven for dependency management
- Minimal CSS for basic UI styling

## Project Structure
- `src/main/java/com/example/crud_app/CrudAppApplication.java`: Main entry point for the Spring Boot application.
- `controller/`: Handles HTTP requests and maps them to service methods.
- `service/`: Contains business logic for managing students.
- `repository/`: Interfaces with the database using Spring Data JPA.
- `model/`: Defines the Student entity.
- `resources/templates/`: HTML templates for frontend pages.
- `resources/static/style.css`: Minimal CSS for UI styling.

## Deeper Explanation

This project is designed for learning Spring Boot and understanding how a CRUD application works. Here is a breakdown of the main components and why they are structured this way:

### 1. CrudAppApplication.java
- **@SpringBootApplication**: This annotation marks the main class of a Spring Boot application. It enables auto-configuration, component scanning, and allows you to run the app as a standalone Java application.
- **Purpose**: Serves as the entry point. When you run this file, Spring Boot starts up and initializes all components.

### 2. model/Student.java
- **@Entity**: Tells Spring Data JPA that this class should be mapped to a database table.
- **@Id** and **@GeneratedValue**: Marks the primary key and configures it to auto-increment.
- **Purpose**: Represents the data structure for a student. Each instance corresponds to a row in the database.

### 3. repository/StudentRepository.java
- **@Repository**: Indicates that this interface is a Spring Data repository.
- **Extends JpaRepository**: Inherits CRUD methods for interacting with the database.
- **Purpose**: Handles all database operations for Student entities.

### 4. service/StudentService.java
- **@Service**: Marks this class as a service component in Spring.
- **Purpose**: Contains business logic. Calls repository methods and processes data before returning it to controllers.

### 5. controller/StudentController.java
- **@Controller**: Marks this class as a web controller.
- **@RequestMapping**: Maps HTTP requests to handler methods.
- **@GetMapping, @PostMapping, etc.**: Specify the HTTP method for each endpoint.
- **Purpose**: Handles incoming web requests, interacts with the service layer, and returns views or data.

### 6. resources/templates/*.html
- **Thymeleaf Templates**: Used for rendering dynamic HTML pages.
- **Purpose**: Provides the frontend for users to interact with the app (view, add, edit, delete students).

### 7. resources/static/style.css
- **CSS File**: Adds basic styling to the HTML pages.
- **Purpose**: Improves the user interface and experience.

### Why This Structure?
- **Separation of Concerns**: Each layer (controller, service, repository, model) has a specific responsibility, making the code easier to maintain and understand.
- **Spring Boot Best Practices**: Uses annotations and conventions recommended by Spring Boot for rapid development and scalability.
- **Learning Focus**: The project is kept simple and well-annotated to help beginners understand each part.


## Getting Started
1. **Clone the repository**
2. **Build the project** using Maven:
   ```bash
   mvn clean install
   ```
3. **Run the application**:
   ```bash
   mvn spring-boot:run
   ```
4. Access the web interface or API endpoints.

## Technologies Used
- Java
- Spring Boot
- Maven

## Notes
- Each file is annotated and structured according to Spring Boot best practices.
- The application is designed for learning and internship purposes, with detailed code explanations available in the source files.
