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
    ```env
    PG_PORT=5432

    # DB
    PG_NAME=life_balance_db
    
    # Root user for admin/migration
    PG_ROOT_USER=postgres
    PG_ROOT_PASS=postgres_pass
    
    # App user
    PG_USER=app_user
    PG_PASS=app_pass
    
    PG_HOST=localhost
    ```

2. **Run the development environment**:
    ```bash
    ./dev.sh up -- start app
    ./dev.sh down -- exit app
    ./dev.sh hard-reset -- full app restart (all data removed)
    ./dev.sh migrate -- up all migrations
    ./dev.sh rollback -- down 1 last migration
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
