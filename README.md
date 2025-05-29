# Life Balance Backend

The application is inspired by the principles outlined in *The 7 Habits of Highly Effective People*. It is designed to help you organize your life, goals, and initiatives — aligning your actions with your core values and long-term vision. It supports both personal growth and organizational clarity by rooting every project and decision in imagination, integrity, and proactive intent.

---

## 🚀 Quick Start

### Prerequisites

- Java 21+
- Docker + Docker Compose
- Bash
- Gradle (wrapper is included)

---

## 📦 Setup Instructions

1. **Create a `.env` file** in the project root:
    ```bash
    cp .env.example .env  
    ```

2. **Run the development environment**:
    ```bash
    ./dev.sh up         # start app
    ./dev.sh down       # exit app
    ./dev.sh hard-reset # full app restart (all data removed)
    ./dev.sh migrate    # up all migrations
    ./dev.sh rollback   # down 1 last migration
   ./dev.sh test        # unit + integration tests and coverage
    ```

   This script will:
    - Load environment variables from `.env`
    - Start PostgreSQL via Docker Compose
    - Run the Spring Boot app with Gradle

---

## 📄 Creating a Liquibase Migration

   To create a new migration file:
   
   ```bash
   ./create_migration.sh your-change-name
   ```