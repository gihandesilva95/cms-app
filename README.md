## Customer Management System

A full-stack customer management application built with **Spring Boot** (Java 8), **React.js**, and **MariaDB**. This project supports CRUD operations, bulk Excel upload, and integrates a REST API backend with a modern React frontend.


## Technologies

- **Backend:** Java 8, Spring Boot, Maven  
- **Frontend:** React.js, Axios, Material-UI  
- **Database:** MariaDB  
- **Testing:** JUnit  


## 🚀 Setup Instructions

### 1. Clone the repository
    `git clone <repository_url>`
    cd cms-app

### 2. Backend Setup (Java 8, Spring Boot, MariaDB)
    `cd cms`

    # Copy .env file
        `cp .env.example .env`

    # Set your database connection & credentials in application.properties:

        `spring.datasource.url=jdbc:mariadb://localhost:3306/your_db_name`
        `spring.datasource.username=your_db_username`
        `spring.datasource.password=your_db_password`

    # Run
        `mvn clean install`
        `mvn spring-boot:run`


### 3. Frontend Setup (React JS)

    `cd cms-frontend`

    # Install dependencies
        `npm install`

    # Run the dev server
        `npm start`

    #The project will be available at `  http://localhost:3000/ ` by default.
