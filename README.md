# Financial Budget Gamified Application

A Java application that gamifies financial budgeting to make managing personal finances more engaging and rewarding.

## Prerequisites

Before running the application, ensure you have the following installed:
- Java Development Kit (JDK) 18 or higher
- Maven
- PostgreSQL 14 or higher

## Project Structure

The application follows a Model-View-Controller (MVC) architecture:

- **Model**: Contains data models and database operations
- **View**: Contains UI components and screens
- **Controller**: Contains business logic and connects models with views

## Setup and Installation

### 1. Database Setup (Backend)

1. Install PostgreSQL if you haven't already:
   - Windows: Download and install from [PostgreSQL Official Website](https://www.postgresql.org/download/windows/)
   - Mac: `brew install postgresql`
   - Linux: `sudo apt-get install postgresql`

2. Start PostgreSQL Service:
   - Windows: PostgreSQL service should start automatically
   - Mac: `brew services start postgresql`
   - Linux: `sudo service postgresql start`

3. Create the Database:
   ```sql
   psql -U postgres
   CREATE DATABASE Projecttest;
   CREATE USER root WITH PASSWORD '';
   GRANT ALL PRIVILEGES ON DATABASE Projecttest TO root;
   ```

4. Verify Database Connection:
   ```sql
   psql -U root -d Projecttest
   ```

### 2. Application Setup (Frontend)

1. Clone the repository:
   ```bash
   git clone [repository-url]
   cd EECS2311_FinancialBudgetGamified-App
   ```

2. Install dependencies using Maven:
   ```bash
   mvn clean install
   ```

## Running the Application

### 1. Ensure Database is Running

Before starting the application, make sure PostgreSQL is running:
- Windows: Check Services app for "PostgreSQL"
- Mac: `brew services list`
- Linux: `sudo service postgresql status`

### 2. Start the Application

#### Using Maven
```bash
mvn clean package
java -jar target/EECS2311_FinancialBudgetGamified-App-1.0-SNAPSHOT.jar
```

#### Using an IDE
1. Open the project in your IDE (Eclipse, IntelliJ IDEA, etc.)
2. Run the `app.Main` class

## Configuration

### Database Configuration
The database connection settings can be found in `src/main/resources/database.properties`. Default settings are:
```properties
db.url=jdbc:postgresql://localhost:5432/Projecttest
db.user=root
db.password=
```

Modify these settings if you're using different database credentials.

## Features

- User authentication
- Financial transaction tracking
- Budget management
- Calendar view for financial planning
- Gamification elements (points, achievements, etc.)

## Dependencies

- Java Mail API
- JDatePicker
- PostgreSQL JDBC Driver
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

## Troubleshooting

1. Database Connection Issues:
   - Verify PostgreSQL is running
   - Check database credentials in `database.properties`
   - Ensure port 5432 is not blocked by firewall

2. Application Startup Issues:
   - Verify Java version (JDK 18+)
   - Check Maven installation
   - Review application logs for errors

## Support

For issues and support:
1. Check the troubleshooting section
2. Review project documentation
3. Submit an issue on the project repository
