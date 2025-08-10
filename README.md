
# Inventory Backend

## Overview

This is a Spring Boot project that can be set up locally by following the steps below. The project includes an example configuration file (`application.properties.example`) that should be updated with your own valid configuration values.

## Prerequisites

Before you begin, make sure you have the following software installed on your system:

- **Java JDK 17** (or compatible version) - [Download](https://adoptopenjdk.net/)
- **Maven** - [Download and Installation Instructions](https://maven.apache.org/install.html)
- **Git** - [Download](https://git-scm.com/downloads)
- **Database** (e.g., MySQL, PostgreSQL, etc.) - Ensure you have a database installed and running.

## Getting Started

### 1. Clone the Repository

GetFirst(), clone this repository to your local machine:

```bash
git clone https://github.com/kmtGryffindor20/JdbcInventory.git
```

Navigate to the project directory:

```bash
cd JdbcInventory
```

### 2. Set Up Configuration

The project comes with a sample properties file named `application.properties.example`. You need to create an `application.properties` file with your specific configuration values.

1. Copy `application.properties.example` to a new file named `application.properties`:

   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```

2. Open the `application.properties` file and update the following properties with your own configuration:

   ```properties
   # Database Configuration
   spring.datasource.url=jdbc:mysql://localhost:3306/your_database_name
   spring.datasource.username=your_database_username
   spring.datasource.password=your_database_password

   spring.sql.init.mode=always
   
   
   ```

   Make sure to set the correct values for your database URL, username, password, and any other necessary configuration.

### 3. Build the Project

Use Maven to build the project. Run the following command in the root directory of the project:

```bash
./mvnw clean install
```

This will download all required dependencies and build the project.

### 4. Run the Application

Once the build is successful, you can run the Spring Boot application using:

```bash
./mvnw spring-boot:run
```

Alternatively, you can run the application using the JAR file:

```bash
java -jar target/backend-[Some SNAPSHOT].jar
```

### 5. Access the Application

The application will start on the default port `8080`. You can access it by navigating to:

```
http://localhost:8080
```

## Running Tests

To run the tests for the project, use the following command:

```bash
./mvnw test
```

## Directory Structure

```
backend/
 └── src/
     ├── main/
     │   ├── java/               # Application source code
     │   └── resources/
     │       ├── application.properties.example  # Example configuration file
     │       └── application.properties           # Your actual configuration file (not in repository)
     └── test/                 # Test source code
```

## Troubleshooting

- **Port Already in Use**: If you encounter a port conflict, change the `server.port` property in `application.properties`.
- **Database Connection Issues**: Ensure that your database is running and that the URL, username, and password in `application.properties` are correct.

## Contributing

If you want to contribute to this project, feel free to create a pull request or raise an issue on GitHub.
