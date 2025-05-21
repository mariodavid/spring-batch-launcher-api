# Sample: app-http-kubernetes

### reset

```shell
kubectl delete \
-f http-kubernetes-role.yaml \
-f http-kubernetes-service-account.yaml \
-f http-kubernetes-role-binding.yaml \
-f http-kubernetes-service.yaml \
-f http-kubernetes-deployment.yaml \
-f postgres-service.yaml \
-f postgres-deployment.yaml \
-f greeting-job-config.yaml \
-f greeting-job-template.yaml
```

#### 0. mvn install
```shell
cd ../.. && mvn clean install && cd deployments/kubernetes
```

#### 1. starting postgres
```shell
kubectl apply \
  -f postgres-service.yaml \
  -f postgres-deployment.yaml
```

#### 2. build batch jobs
```shell
docker compose -f docker-compose-kubernetes-jobs-build.yaml build
```

#### 3. registering batch jobs
```shell
kubectl apply \
  -f greeting-job-config.yaml \
  -f greeting-job-template.yaml
```

#### 4. create HTTP kubernetes service account
```shell
kubectl apply \
  -f http-kubernetes-role.yaml \
  -f http-kubernetes-service-account.yaml \
  -f http-kubernetes-role-binding.yaml
```

#### 5. starting HTTP Kubernetes API
```shell
kubectl delete \
-f http-kubernetes-service.yaml \
-f http-kubernetes-deployment.yaml
```

```shell
kubectl apply \
  -f http-kubernetes-service.yaml \
  -f http-kubernetes-deployment.yaml
```
```shell
http localhost:30080/actuator/health
```

#### 4. starting a job via HTTP

```shell
export GREETING_NAME="user"
export TIMEOUT=$(( RANDOM % 9001 + 1000 ))

export EXTERNAL_JOB_EXECUTION_ID=$(curl -s -X POST --location "http://localhost:30080/jobs" \
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
kubectl get pod -l com.company.batch.job.external-job-execution-id=$EXTERNAL_JOB_EXECUTION_ID
```

```shell
kubectl logs -l com.company.batch.job.external-job-execution-id=$EXTERNAL_JOB_EXECUTION_ID | grep "Hello $GREETING_NAME"
```