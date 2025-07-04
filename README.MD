# Healthcare Management System

The **Healthcare Management System (HMS)** is a full‑stack application for managing appointments, prescriptions and user records for patients, doctors, administrators and owners. It aims to streamline operations while keeping data secure and compliant.

## Directory overview

```
backend/  - Spring Boot API server
frontend/ - React + Vite client
```

See [`backend/project-overview`](backend/project-overview) and [`backend/classes-methods`](backend/classes-methods) for detailed specifications.

## Backend

* **Tech stack**: Spring Boot, Spring Security (JWT), JPA/Hibernate and MySQL.
* **Key modules**
  * **Entities**: `User`, `Patient`, `Doctor`, `Appointment`, `Prescription`, `Notification`, `AuditLog`, etc.
  * **Repositories & Services** implement business logic around these entities.
  * **Controllers** expose REST endpoints under `/api`.
  * **Security** uses JWT tokens via `JwtAuthenticationFilter` and role-based rules.
* **Setup**
  1. Edit `backend/src/main/resources/application.properties` with your MySQL credentials and JWT secret.
  2. Start the API:
     ```bash
     cd backend
     ./mvnw spring-boot:run
     ```

## Frontend

* **Tech stack**: React with Vite and Tailwind CSS.
* **Context providers**: `AuthContext` manages login state and JWT storage; `ThemeContext` toggles light/dark mode.
* **Role‑based features**
  * **Patients** can book appointments, view prescriptions and manage their profile.
  * **Doctors** manage schedules, prescriptions and patient messaging.
  * **Admins** handle user and appointment administration.
  * **Owners** oversee system configuration and analytics.
* **Setup**
  1. Install dependencies:
     ```bash
     cd frontend
     npm install
     ```
  2. Run the development server:
     ```bash
     npm run dev
     ```
     The Vite dev server proxies API requests starting with `/api` to `http://localhost:8080` (see `vite.config.js`).

## Interaction between front‑end and back‑end

All HTTP calls from the React client hit the REST API under `/api`, which is served by the Spring Boot backend. During development, Vite proxies these requests so the browser can talk to both parts seamlessly.

## Building for production

1. **Backend**: package the Spring Boot app
   ```bash
   cd backend
   ./mvnw package
   java -jar target/*.jar
   ```
2. **Frontend**: build static assets
   ```bash
   cd frontend
   npm run build
   ```
   Serve the resulting `dist/` directory with your preferred web server, or configure Spring Boot to serve these files.

---
For deeper functional descriptions see the [project overview](backend/project-overview) and [class design](backend/classes-methods) documents.
