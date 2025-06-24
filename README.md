### Product Management System - Full Stack Application

A full-stack product management system built with React.js (frontend) and Spring Boot (backend).

## Repository Structure

This repository has two main branches:

1. **`backend`**: Contains the Spring Boot application
2. **`frontend`**: Contains the React.js application

## Prerequisites

Before you begin, ensure you have the following installed:

- **Java 17 JDK** (for backend)
- **Node.js 18+** (for frontend)
- **PostgreSQL 16** (database)
- **Maven** (for backend build)
- **npm** or **yarn** (for frontend dependencies)

## Setup Instructions

### Backend Setup (Spring Boot)

1. **Clone the backend branch**:
   ```bash
   git clone -b backend https://github.com/MASKADEV/hahn-app.git
   ```

2. **Navigate to backend directory**:
   ```bash
   cd backend
   ```

3. **Configure database**:
   - Create a PostgreSQL database named `product_management`
   - Update the database configuration in `src/main/resources/application.yml`:
     ```
      spring:
        datasource:
          url: jdbc:postgresql://localhost:5432/product_management
          username: your_username
          password: your_password
     ```

4. **Build and run the application**:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```
   The backend will start at `http://localhost:8888`

### Frontend Setup (React)

1. **Clone the frontend branch**:
   ```bash
   git clone -b frontend https://github.com/MASKADEV/hahn-app.git
   ```

2. **Navigate to frontend directory**:
   ```bash
   cd frontend
   ```

3. **Install dependencies**:
   ```bash
   npm install
   # or
   yarn install
   ```

4. **Configure environment**:
   Create a `.env` file in the root of the frontend directory with:
   ```
   VITE_API_BASE_URL=http://localhost:8888/api
   ```

5. **Start the development server**:
   ```bash
   npm start
   # or
   yarn start
   ```
   The frontend will open at `http://localhost:5173`

## API Endpoints

The backend provides the following REST API endpoints:

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get a single product
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update a product
- `POST /api/auth/Register` - Register User
- `DELETE /api/auth/login` - Login User
