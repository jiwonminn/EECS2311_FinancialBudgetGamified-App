# Financial Budget Gamified Application

A Java application that gamifies financial budgeting to make managing personal finances more engaging and rewarding.

## Project Structure

The application follows a Model-View-Controller (MVC) architecture:

- **Model**: Contains data models and database operations
- **View**: Contains UI components and screens
- **Controller**: Contains business logic and connects models with views

## Main Entry Point

The main entry point for the application is the `app.Main` class. This class initializes the application, sets up the UI look and feel, updates the database schema if needed, and launches the login screen.

## How to Run

### Using Maven

1. Make sure you have Maven installed
2. Navigate to the project root directory
3. Run the following command:

```
mvn clean package
java -jar target/FBGApp-1.0-SNAPSHOT.jar
```

### Using an IDE

1. Open the project in your IDE (Eclipse, IntelliJ IDEA, etc.)
2. Change the URL, USER and PASSWORD string in DatabaseManager class to match the database on your computer
    private static final String URL = "jdbc:postgresql://localhost:5432/database_name";
    private static final String USER = "your_username";
    private static final String PASSWORD = "your_password";
3. Run the `app.Main` class

## Features

- User authentication
- Financial transaction tracking
- Budget management
- Calendar view for financial planning
- Gamification elements (points, achievements, etc.)

## Dependencies

- Java Mail API
- JDatePicker
- MySQL Connector/J
- JUnit 5 (for testing)

## Development

The project uses Maven for dependency management and building. The module system is used to organize the codebase, with the main module being `FBGApp`.

### Module Structure

The `module-info.java` file defines the module structure, including required dependencies and exported packages.

### Package Structure

- `app`: Contains the main entry point
- `controller`: Contains business logic
- `model`: Contains data models
- `view`: Contains UI components
- `database`: Contains database operations
