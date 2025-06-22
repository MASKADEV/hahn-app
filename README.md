### Product Management System - Full Stack Application

## Overview

This is a full-stack CRUD application built with:
- **Backend**: Java Spring Boot (Rest)
- **Frontend**: React.js
- **Database**: PostgreSQL

The application demonstrates clean code practices, proper architecture, and version control best practices.

## Features

### Backend Features
- RESTful API with Spring Boot
- JPA/Hibernate for database interaction
- Product and User entities with full CRUD operations
- Input validation and graceful error handling
- JWT authentication with access/refresh tokens
- Role-based authorization (USER, ADMIN)
- Proper DDD (Domain-Driven Design) architecture

### Frontend Features
- Responsive UI with React
- React Router for navigation
- Axios for API communication
- Forms for adding/editing products
- List view with delete/edit options
- Authentication flow (login/registration)
- Context API for state management

## Branches

This repository has two main branches:

1. **`backend`**: Contains the Spring Boot application
   - Java 11+
   - Spring Boot 2.7+
   - Spring Security
   - JWT authentication
   - PostgreSQL

2. **`frontend`**: Contains the React.js application
   - React 18+
   - React Router 6
   - Axios for HTTP requests
   - Context API for state management
   - Bootstrap 5 for styling

### Prerequisites

- Java 17 JDK
- Node.js 22
- PostgreSQL 16
- Maven (for backend)
- npm/yarn (for frontend)
