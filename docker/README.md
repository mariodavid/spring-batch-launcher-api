## Docker

### reset

```shell
docker compose \
  -f docker-compose-infra.yaml \
  -f docker-compose-docker-jobs-build.yaml \
  -f docker-compose-http-docker.yaml \
  down
```

### Start Postgres

```shell
docker compose -f docker-compose-infra.yaml up --build -d
```

### Sample: job-app-http-docker

#### 1. starting postgres
```shell
docker compose -f docker-compose-infra.yaml up --build -d
```

#### 2. build batch jobs
```shell
docker compose -f docker-compose-docker-jobs-build.yaml build
```

#### 3. starting HTTP docker API
```shell
docker compose -f docker-compose-http-docker.yaml up --build -d
```

```shell
curl -X POST --location "http://localhost:8080/jobs" \
    -H "Content-Type: application/json" \
    -d '{
          "jobName": "greetingJob",
          "jobParameters": {
            "greetingName": "Mario",
            "timeout": 5011
          }
        }'
```