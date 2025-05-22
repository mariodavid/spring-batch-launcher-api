## Sample: app-http-docker

### reset

```shell
docker compose \
  -f docker-compose-infra.yaml \
  -f docker-compose-docker-jobs-build.yaml \
  -f docker-compose-http-docker.yaml \
  down
```

#### 0. mvn install
```shell
cd ../.. && mvn clean install && cd deployments/docker
```

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

#### 4. starting a job via HTTP

```shell
export GREETING_NAME="user"
export TIMEOUT=$(( RANDOM % 9001 + 1000 ))

curl -s -X POST --location "http://localhost:8080/jobs" \
    -H "Content-Type: application/json" \
    -d "{
          \"jobName\": \"greetingJob\",
          \"jobParameters\": {
            \"greetingName\": \"$GREETING_NAME\",
            \"timeout\": $TIMEOUT
          }
        }" | jq
```

```shell
export GREETING_NAME="user"
export TIMEOUT=$(( RANDOM % 9001 + 1000 ))

export EXTERNAL_JOB_EXECUTION_ID=$(curl -s -X POST --location "http://localhost:8080/jobs" \
    -H "Content-Type: application/json" \
    -d "{
          \"jobName\": \"greetingJob\",
          \"jobParameters\": {
            \"greetingName\": \"$GREETING_NAME\",
            \"timeout\": $TIMEOUT
          }
        }" | jq -r '.externalJobExecutionId')

echo "EXTERNAL_JOB_EXECUTION_ID: $EXTERNAL_JOB_EXECUTION_ID"
```

```shell
docker ps -a --filter "label=com.company.batch.job.external-job-execution-id=$EXTERNAL_JOB_EXECUTION_ID"
```

```shell
docker logs greetingjob-$EXTERNAL_JOB_EXECUTION_ID
```