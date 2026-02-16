# Docker Setup Guide - Email Marketing API

## Prerequisites

- Docker Desktop installed and running
- Docker Compose (included with Docker Desktop)

## Quick Start

### Option 1: Using Docker Compose (Recommended)

This will start both PostgreSQL and the backend API:

```bash
cd emailmarketapi
docker-compose up -d
```

To view logs:
```bash
docker-compose logs -f backend
```

To stop:
```bash
docker-compose down
```

### Option 2: Build and Run Manually

#### 1. Build the Docker Image

```bash
cd emailmarketapi
docker build -t emailmarket-api:latest .
```

#### 2. Run PostgreSQL Container

```bash
docker run -d \
  --name emailmarket-postgres \
  -e POSTGRES_DB=simplyfound \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=Kalimbwejoel \
  -p 5432:5432 \
  postgres:15-alpine
```

#### 3. Run the Backend Container

```bash
docker run -d \
  --name emailmarket-api \
  -p 8585:8585 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/simplyfound \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=Kalimbwejoel \
  -e APP_BASE_URL=http://localhost:8585/api/music/view \
  -v emailmarket-uploads:/app/uploads \
  emailmarket-api:latest
```

## Environment Variables

You can override environment variables when running the container:

```bash
docker run -d \
  --name emailmarket-api \
  -p 8585:8585 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/simplyfound \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=your_password \
  -e SPRING_MAIL_HOST=your-smtp-host \
  -e SPRING_MAIL_USERNAME=your-email@example.com \
  -e SPRING_MAIL_PASSWORD=your-password \
  -e APP_BASE_URL=https://your-domain.com/api/music/view \
  emailmarket-api:latest
```

## Volume Management

### View Uploads Volume

```bash
docker volume inspect emailmarket-uploads
```

### Backup Uploads

```bash
docker run --rm \
  -v emailmarket-uploads:/data \
  -v $(pwd):/backup \
  alpine tar czf /backup/uploads-backup.tar.gz -C /data .
```

### Restore Uploads

```bash
docker run --rm \
  -v emailmarket-uploads:/data \
  -v $(pwd):/backup \
  alpine tar xzf /backup/uploads-backup.tar.gz -C /data
```

## Health Checks

Check if the API is running:

```bash
curl http://localhost:8585/api/subscribers/count
```

## Troubleshooting

### View Container Logs

```bash
docker logs emailmarket-api
docker logs emailmarket-api -f  # Follow logs
```

### Access Container Shell

```bash
docker exec -it emailmarket-api /bin/bash
```

### Rebuild After Code Changes

```bash
docker-compose build --no-cache
docker-compose up -d
```

### Clean Up

```bash
# Stop and remove containers
docker-compose down

# Remove volumes (WARNING: deletes data)
docker-compose down -v

# Remove images
docker rmi emailmarket-api:latest
```

## Production Deployment

For production, consider:

1. **Use environment-specific configuration files**
2. **Set up proper secrets management**
3. **Configure reverse proxy (nginx)**
4. **Set up SSL/TLS certificates**
5. **Configure backup strategies**
6. **Set up monitoring and logging**

### Example Production Docker Run

```bash
docker run -d \
  --name emailmarket-api \
  --restart unless-stopped \
  -p 8585:8585 \
  -e SPRING_PROFILES_ACTIVE=prod \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://db-host:5432/simplyfound \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=${DB_PASSWORD} \
  -e SPRING_MAIL_HOST=${MAIL_HOST} \
  -e SPRING_MAIL_USERNAME=${MAIL_USERNAME} \
  -e SPRING_MAIL_PASSWORD=${MAIL_PASSWORD} \
  -e APP_BASE_URL=https://api.yourdomain.com/api/music/view \
  -v /path/to/uploads:/app/uploads \
  emailmarket-api:latest
```

