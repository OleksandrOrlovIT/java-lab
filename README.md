# Spring_Core_GYM

**Spring_Core_GYM** is a comprehensive, microservice-based application designed to manage gym operations, streamline user interactions, and track memberships, trainers, and trainee activities. It leverages the Spring Boot ecosystem for robust and scalable services.

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Features](#features)
- [Technology Stack](#technology-stack)
  - [Core Services (Spring_Core_GYM Module)](#core-services-spring_core_gym-module)
  - [Trainer Workload Service (Gym_Trainer_Workload Module)](#trainer-workload-service-gym_trainer_workload-module)
  - [Integration Testing (Gym_Integrational_Testing Module)](#integration-testing-gym_integrational_testing-module)
- [Prerequisites](#prerequisites)

## Overview

This project provides a backend system for a modern gym. It handles essential functionalities such as user registration, login, profile management for trainees and trainers, membership plan tracking, and workout session logging. The system is designed with a microservice architecture to ensure scalability, maintainability, and independent deployment of its components.

## Architecture

The Spring_Core_GYM application is composed of distinct microservices communicating (potentially asynchronously) to handle different business domains:

1.  **Core Gym Service (`Spring_Core_GYM` module):**
    * Manages core entities like users (trainees, trainers), memberships, and training types.
    * Handles user authentication and authorization.
    * Provides primary API endpoints for front-end interaction.
    * Integrates with PostgreSQL for persistent storage.

2.  **Trainer Workload Service (`Gym_Trainer_Workload` module):**
    * Manages and tracks the workload of trainers, including training sessions and schedules.
    * Likely consumes messages (e.g., via AMQP) from the Core Gym Service regarding new training assignments.
    * Uses MongoDB for storing trainer workload data.

3.  **Integration Testing (`Gym_Integrational_Testing` module):**
    * A dedicated module for running integration and end-to-end tests across the microservices, ensuring they interact correctly.

## Features

* **User Management:** Secure registration and login for trainees and trainers.
* **Profile Management:** CRUD operations for trainee and trainer profiles.
* **Membership Tracking:** Management of gym membership plans and user subscriptions.
* **Trainer Management:** Onboarding and management of trainer details and specializations.
* **Training Session Logging:** Recording and tracking of training sessions.
* **Role-Based Security:** Secure access to functionalities based on user roles (e.g., trainee, trainer, admin) using JWT.
* **API Documentation:** OpenAPI (Swagger) for clear and interactive API documentation.
* **Monitoring & Metrics:** Health checks and performance metrics via Spring Boot Actuator, with Prometheus integration.
* **Database Schema Migration:** Managed database schema changes using Liquibase (for PostgreSQL).
* **Containerization Support:** Docker integration for consistent development and deployment environments.
* **Asynchronous Communication:** Utilizes message queues (e.g., RabbitMQ via Spring AMQP) for inter-service communication.
* **Comprehensive Testing:**
    * Unit tests for individual components.
    * Integration tests using H2 (for JPA) and Testcontainers (for MongoDB and other services).
    * Behavior-Driven Development (BDD) with Cucumber.

## Technology Stack

This project utilizes a modern Java and Spring-based stack:

**Common Across Modules:**

* **Language:** Java 17
* **Build Tool:** Apache Maven
* **Core Framework:** Spring Boot (versions differ slightly per module, generally 3.3.x - 3.4.x)
* **Testing:**
    * JUnit 5 (via `spring-boot-starter-test`)
    * Cucumber (7.20.1) for BDD
    * Apache HttpClient (4.5.5) for API testing
* **Utilities:**
    * Lombok (1.18.36) for boilerplate code reduction
    * MapStruct (1.6.3) for DTO mapping

---

### Core Services (`Spring_Core_GYM` module)

* **Spring Boot Version:** 3.3.4
* **Web:** Spring MVC (`spring-boot-starter-web`)
* **Data Persistence:**
    * Spring Data JPA (`spring-boot-starter-data-jpa`)
    * **Database:** PostgreSQL (`postgresql` driver 42.7.4)
    * **Schema Migration:** Liquibase (`liquibase-core`)
    * **Test Database:** H2 (`h2` 2.3.232)
* **Security:**
    * Spring Security (`spring-boot-starter-security`)
    * JSON Web Tokens (JWT) with `jjwt-api`, `jjwt-impl`, `jjwt-jackson` (0.12.3)
* **API Documentation:** SpringDoc OpenAPI (`springdoc-openapi-starter-webmvc-ui` 2.6.0)
* **Monitoring & Metrics:**
    * Spring Boot Actuator (`spring-boot-starter-actuator`)
    * Micrometer Core (`micrometer-core`)
    * Prometheus Registry (`micrometer-registry-prometheus`)
* **Messaging:** Spring AMQP (`spring-boot-starter-amqp`) for asynchronous communication
* **Containerization:** Spring Boot Docker Compose (`spring-boot-docker-compose`)
* **Validation:** Spring Boot Validation (`spring-boot-starter-validation`)
* **Logging:** Log4j2 (`log4j-api`, `log4j-core`)
* **Utilities:** Jackson JSR310, Guava (32.0.0-jre)
* **Code Coverage:** JaCoCo (`jacoco-maven-plugin` 0.8.12)

---

### Trainer Workload Service (`Gym_Trainer_Workload` module)

* **Spring Boot Version:** 3.3.5
* **Web:** Spring MVC (`spring-boot-starter-web`)
* **Data Persistence:**
    * Spring Data MongoDB (`spring-boot-starter-data-mongodb`)
* **Messaging:** Spring AMQP (`spring-boot-starter-amqp`)
* **Validation:** Spring Boot Validation (`spring-boot-starter-validation`)
* **Testing:**
    * Testcontainers (`testcontainers` and `mongodb` artifacts, 1.19.0) for MongoDB integration testing.
* **Code Coverage:** JaCoCo (`jacoco-maven-plugin` 0.8.12)

---

### Integration Testing (`Gym_Integrational_Testing` module)

* **Spring Boot Version (for test context):** 3.4.0
* **Primary Focus:** End-to-end and integration testing.
* **Testing Frameworks:**
    * Cucumber (`cucumber-java`, `cucumber-spring`, `cucumber-junit`, `cucumber-junit-platform-engine`)
    * Testcontainers (`testcontainers`, `junit-jupiter` artifacts, 1.19.0)
    * Spring Boot Test utilities, including `spring-boot-starter-web` for test scope (e.g., `TestRestTemplate`).
* **Build Plugins:** `maven-failsafe-plugin` for running Cucumber integration tests.

## Prerequisites

Before you begin, ensure you have the following installed:
* JDK 17 or later
* Apache Maven 3.6.x or later
* Docker and Docker Compose (for running databases and message brokers, and for containerized deployment)
* Access to a PostgreSQL instance (or use Docker)
* Access to a MongoDB instance (or use Docker)
* Access to a RabbitMQ instance (or other AMQP broker, or use Docker)
