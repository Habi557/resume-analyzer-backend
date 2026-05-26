# Resume AI Backend

Spring Boot backend for a resume analysis and screening platform. The service lets users upload resumes, parses resume content from PDF/DOCX files, stores resume data, runs AI-based matching against job descriptions, exposes dashboard/search APIs, and supports authentication, email templates, Redis caching, and Docker-based deployment.

## Features

- User registration, login, JWT refresh token, logout, and Google OAuth2 support
- Resume upload and download
- PDF and DOCX resume parsing
- AI-powered resume screening with Ollama through Spring AI
- Background resume analysis jobs with polling support
- Dashboard and analyzed-resume listing APIs
- Resume search and skill suggestions
- Chatbot-style query endpoint for analyzed resumes
- Email templates for registration and candidate status updates
- MySQL persistence with Flyway database migrations
- Redis integration for caching/session support
- Local file storage and AWS S3 storage implementations
- Dockerfile and Docker Compose setup for backend, MySQL, Redis, Ollama, and frontend image

## Tech Stack

- Java 21
- Spring Boot 3.4.2
- Spring Web, Spring Security, Spring Data JPA, Spring Mail, Spring Cache, Spring AOP
- Spring AI with Ollama
- MySQL 8
- Flyway
- Redis
- Apache PDFBox, Apache POI, Apache Tika
- AWS SDK for S3 and SES
- Maven
- Docker and Docker Compose

## Project Structure

```text
src/main/java/com/resume/backend
├── configurations       # Security, JWT, OAuth2, Redis, AWS SES, async config
├── controller           # REST API controllers
├── db                   # Startup data initialization
├── dtos                 # Request/response DTOs
├── entity               # JPA entities
├── exceptions           # Custom exceptions
├── globalexceptions     # Global exception handlers
├── helperclass          # Mapping, parsing, response, specification helpers
├── projection           # Repository projections
├── repository           # Spring Data repositories
├── serviceImplementation# Service implementations
└── services             # Service interfaces

src/main/resources
├── db/migration         # Flyway SQL migrations
├── prompts              # AI prompt templates
├── templates            # Thymeleaf email templates
└── application*.properties
```

## Prerequisites

- Java 21
- Maven 3.9+ or the included Maven wrapper
- MySQL 8
- Redis
- Ollama, if running AI analysis locally
- Docker and Docker Compose, if using containers

## Environment Variables

Create a `.env` file for Docker usage, or export these values in your shell for local usage.

```env
SPRING_PROFILES_ACTIVE=dev
MYSQL_URL=jdbc:mysql://localhost:3306/AIresumeDb
MYSQL_USERNAME=root
MYSQL_PASSWORD=your_mysql_password

OLLAMA_MODEL=qwen2.5:3b
OLLAMA_BASE_URL=http://localhost:11434

EMAIL_USERNAME=your_email@gmail.com
MAIL_USERNAME=your_email@gmail.com
pass=your_email_app_password

OAUTH_CLIENT_ID=your_google_client_id
OAUTH_CLIENT_SECRET=your_google_client_secret

UPLOAD_DIR=./uploads
```

For the Docker profile, use service names in container URLs, for example:

```env
SPRING_PROFILES_ACTIVE=docker
MYSQL_URL=jdbc:mysql://mysql:3306/resume_ai
OLLAMA_BASE_URL=http://ai-model-ollama:11434
UPLOAD_DIR=/app/uploads
```

## Run Locally

1. Start MySQL, Redis, and Ollama.
2. Create a MySQL database:

```sql
CREATE DATABASE AIresumeDb;
```

3. Pull the Ollama model:

```bash
ollama pull qwen2.5:3b
```

4. Export the required environment variables.
5. Start the application:

```bash
./mvnw spring-boot:run
```

The API runs at:

```text
http://localhost:8080
```

## Run With Docker Compose

Build and start the full stack:

```bash
docker compose up --build
```

The backend will be available at:

```text
http://localhost:8080
```

Docker Compose includes:

- `resume-ai-backend` on port `8080`
- `mysql` on host port `3307`
- `redis` on port `6379`
- `ai-model-ollama` on port `11434`
- `angular-frontend` on port `80`

## Build

```bash
./mvnw clean package
```

Build without tests:

```bash
./mvnw clean package -DskipTests
```

Build the Docker image:

```bash
docker build -t airesumeimage .
```

## Tests

```bash
./mvnw test
```

## Key API Endpoints

### Auth

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/auth/register` | Register a user |
| `POST` | `/auth/login` | Login and receive access token |
| `POST` | `/auth/refreshToken` | Refresh access token using refresh cookie |
| `POST` | `/auth/logout` | Logout user |
| `GET` | `/auth/health` | Health check |

### Resume

| Method | Endpoint | Description |
| --- | --- | --- |
| `POST` | `/ai/upload` | Upload a resume file |
| `POST` | `/ai/screen-resume` | Start resume screening against a job description |
| `GET` | `/analyze/status/{jobId}` | Poll analysis job status |
| `GET` | `/ai/getAllAnalysiedResumes` | List analyzed resumes |
| `GET` | `/ai/gellAllDashboardDetails` | Get dashboard details |
| `GET` | `/ai/downloadResume/{resumeId}` | Download a resume file |
| `GET` | `/ai/allResumes` | List uploaded resumes |
| `DELETE` | `/resume/delete/{resumeId}` | Delete a resume |

### Search, User, Chatbot, Email

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `/search/suggestions?query=java` | Get resume/skill suggestions |
| `GET` | `/search/analysedresumes?skillName=java` | Search analyzed resumes by skill |
| `GET` | `/user/getUserAnalyisedDetails?username=value` | Get analyzed resumes for a user |
| `GET` | `/chatbot/query?userQuery=value` | Query analyzed resumes |
| `GET` | `/email/sendEmail/{id}` | Send status email |
| `POST` | `/editResumeDetails/edit` | Edit parsed resume details |

## Example Requests

Register:

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "email": "admin@example.com",
    "password": "password"
  }'
```

Upload resume:

```bash
curl -X POST "http://localhost:8080/ai/upload?username=admin" \
  -H "Authorization: Bearer <access-token>" \
  -F "file=@/path/to/resume.pdf"
```

Start screening:

```bash
curl -X POST "http://localhost:8080/ai/screen-resume?scanAllresumesIsChecked=true" \
  -H "Authorization: Bearer <access-token>" \
  -H "Content-Type: application/json" \
  -d '{"jobDescription":"Java Spring Boot developer with MySQL and AWS experience"}'
```

Poll screening status:

```bash
curl http://localhost:8080/analyze/status/<job-id> \
  -H "Authorization: Bearer <access-token>"
```

## Database Migrations

Flyway migrations are stored in:

```text
src/main/resources/db/migration
```

The application uses:

```properties
spring.jpa.hibernate.ddl-auto=none
spring.flyway.enabled=true
```

So schema changes should be added as new Flyway migration files.

## Notes

- The application currently uses profile-specific configuration files: `dev`, `docker`, and `prod`.
- Several endpoint names contain existing spelling from the codebase, such as `getAllAnalysiedResumes`, `gellAllDashboardDetails`, and `getUserAnalyisedDetails`.
- Keep real credentials out of Git. Use environment variables or a local `.env` file.
