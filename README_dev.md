Development helper: docker-compose + env example

This repository includes a `docker-compose.yml` to bring up local dev services: MinIO, MySQL, RabbitMQ. Use the `.env.example` as a starting point.

Quick start (Linux / macOS / Windows with Git Bash or adapted to cmd):

1. Copy the example env and (optionally) customize:

```bash
cp .env.example .env
# edit .env if you want to change credentials
```

2. Start services:

```bash
docker-compose up -d
```

This will start:
- MinIO at http://localhost:9000 (S3 API) and console at http://localhost:9001
- MySQL at localhost:3306
- RabbitMQ at amqp://localhost:5672 (management at http://localhost:15672)

3. Use the `local` Spring profile to use `src/main/resources/application-local.properties`:

Windows cmd example:

```cmd
set SPRING_PROFILES_ACTIVE=local
mvnw.cmd spring-boot:run
```

Or run the packaged jar:

```cmd
set SPRING_PROFILES_ACTIVE=local
java -jar target\nutritional-0.0.1-SNAPSHOT.jar
```

4. Verify MinIO (on startup the app should log MinIO info):

- Console: http://localhost:9001 (login using MINIO credentials from .env)
- The app logs should show lines like:
  - "MinIO configurado - Endpoint: http://localhost:9000"
  - "MinIO - Bucket: nutritional-fotos"
  - "Bucket 'nutritional-fotos' já existe" or "Bucket '...' criado com sucesso"

Troubleshooting

- If the application still uses dummy S3 (DummyS3Config), check:
  - Profile active: `local`.
  - `minio.endpoint` property present in `application-local.properties` or env var `MINIO_ENDPOINT` set.

- To run the Spring Boot app within docker-compose, uncomment the `app` service in `docker-compose.yml` and adjust the build context.

If you want, I can also:
- Add a small script to create the bucket automatically via `mc` or AWS CLI on the host.
- Add a `docker-compose` profile that includes the app container wired to the services.
