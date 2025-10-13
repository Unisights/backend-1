## 🐳 PostgreSQL Database with Docker

To run a PostgreSQL 16 database instance for this project, use the following Docker command. This will set up a container named `unisights-pg`, expose port `5432`, and set the database name and password.

### Prerequisites

* **[Docker](https://docs.docker.com/get-docker/)** must be installed and running on your system.

### Command

Run this command in your terminal to start the database container:

```bash
docker run --name unisights-pg -e POSTGRES_PASSWORD=pass -e POSTGRES_DB=unisights -p 5432:5432 -d postgres:16 
```

```bash

docker run -p 9100:9000 -p 9101:9001  --name minio -e MINIO_ROOT_USER=minioadmin -e MINIO_ROOT_PASSWORD=minioadmin -v "${PWD}\.minio:/data" -d quay.io/minio/minio server /data --console-address ":9001"


```

### Then open:

MinIO Console → http://localhost:9101

Access key(login): minioadmin

Secret key(pwd): minioadmin

create bucket unisights.

### for day 12
docker compose -f 'docker-compose.staging.yml' --env-file .env.staging up -d backend