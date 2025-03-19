# Spring Batch API Spike

This project is a technical spike to explore and demonstrate different strategies for triggering and orchestrating Spring Batch jobs via an HTTP-based API.

The main goal is to identify suitable approaches to asynchronously trigger batch jobs through a REST API, leveraging a centralized batch job repository to manage and monitor job executions across various deployment scenarios.

---

### Architecture Overview

The Architecture is built on **Spring Batch** and explores different approaches to exposing batch job execution via an API, allowing dynamic, ad hoc triggering of jobs. The core idea is to provide a structured way to trigger and monitor batch jobs across various execution environments.

The **Job Controller** component is the entry point for job execution requests. When a client submits a job request, it is accepted and stored in the Database, ensuring that each job execution is tracked with a unique identifier.

From there, the actual job execution depends on the chosen deployment strategy. The system either notifies an existing **Job Worker** or instantiates a new execution environment:

- In **local execution**, the job runs in the same process as the API server.
- In **remote execution**, the API server delegates the request to a separate worker instance via HTTP.
- In **Kubernetes-based execution**, the API server dynamically instantiates a new Kubernetes job, ensuring full isolation.

Regardless of the execution strategy, all batch jobs rely on a **shared database**, ensuring job parameters and execution state are consistently accessible. This approach allows for a scalable and flexible architecture, enabling batch jobs to be executed efficiently in different environments.


```mermaid
sequenceDiagram
    actor Client
    participant Job Controller
    participant Batch DB
    participant Job Worker

    Client ->> Job Controller: Create Job
    Job Controller ->> Batch DB: Store Job {externalJobExecutionId, jobName, jobParameters}
    Job Controller -->> Client: 200 OK { externalJobExecutionId }
    
    Job Controller -->> Job Worker: Notify/Instantiate Worker { externalJobExecutionId }
    Job Worker ->> Batch DB: Load Job
    Job Worker -->> Job Worker: Start Job
    
```

### Comparison to Spring Cloud Data Flow

Spring Cloud Data Flow provides a similar capability, offering a generic API for triggering batch jobs, registering applications, and dynamically deploying jobs in Kubernetes. However, it is a **broader and more complex framework** designed for orchestrating both batch and streaming workloads.

In this example, we explore a **simplified approach** that focuses specifically on **Spring Batch job execution via HTTP**. By narrowing the scope to just exposing and managing batch jobs, it avoids the overhead of a fully-fledged orchestration framework. This serves as an alternative implementation for scenarios where the primary requirement is to trigger and monitor **Spring Batch jobs** without the additional complexity of event-driven data pipelines or extensive job coordination mechanisms.

## Delegation Strategies


### 1. Local Job Execution

This is the simplest approach. Batch jobs are executed asynchronously directly within the same API server instance that receives the request. No external components or additional communication is required.

#### Sequence Diagram

```mermaid
sequenceDiagram
    actor Client
    box Batch API Service
        participant JobController
        participant LocalJobLauncherService
    end
    participant Batch DB

    Client ->> JobController: POST /jobs { jobName, parameters }
    JobController ->> LocalJobLauncherService: Launch Job
    activate LocalJobLauncherService
    LocalJobLauncherService ->> LocalJobLauncherService: Validate Job
    LocalJobLauncherService ->> Batch DB: Save Job
    LocalJobLauncherService -->> JobController: Job launched
    deactivate LocalJobLauncherService

    JobController -->> Client: 200 - OK { externalJobExecutionId }
    activate LocalJobLauncherService
    LocalJobLauncherService ->> LocalJobLauncherService: Start Job async
    LocalJobLauncherService ->> Batch DB: Update Status
    deactivate LocalJobLauncherService
```


### 2. HTTP-Gateway Delegation

In this variant, the API server acts as an API gateway and delegates job execution requests via HTTP to registered remote "Batch Worker" instances. These workers implement the same REST API but run independently, potentially on different nodes.

- Each worker runs the job locally (like in variant 1).
- The API gateway dynamically resolves the appropriate worker instance based on job names using a configurable mapping.


#### Sequence Diagram

```mermaid
sequenceDiagram
    actor Client
    box Batch API Gateway
        participant JobGatewayController
        participant HttpJobLauncherService
    end

    participant Batch DB
    
    box Batch API Service
        participant JobServiceController
        participant LocalJobLauncherService
    end

    Client ->> JobGatewayController: POST /jobs { jobName, parameters }
    JobGatewayController ->> HttpJobLauncherService: Launch Job
    activate HttpJobLauncherService
    HttpJobLauncherService ->> HttpJobLauncherService: Validate Job
    HttpJobLauncherService ->> Batch DB: Save Job
    HttpJobLauncherService -->> JobGatewayController: Job launched
    deactivate HttpJobLauncherService

    JobGatewayController -->> Client: 200 - OK { externalJobExecutionId }
    
    activate HttpJobLauncherService
    HttpJobLauncherService ->> JobServiceController: POST /jobs { externalJobExecutionId }
    JobServiceController ->> LocalJobLauncherService: Launch Job
    activate LocalJobLauncherService
    LocalJobLauncherService ->> Batch DB: Load Job { externalJobExecutionId }
    LocalJobLauncherService ->> LocalJobLauncherService: Validate Job
    LocalJobLauncherService -->> JobServiceController: Job launched
    deactivate LocalJobLauncherService

    JobServiceController -->> HttpJobLauncherService: 200 - OK
    deactivate HttpJobLauncherService
    activate LocalJobLauncherService
    LocalJobLauncherService ->> LocalJobLauncherService: Start Job async
    LocalJobLauncherService ->> Batch DB: Update Status
    deactivate LocalJobLauncherService
```


### 3. Kubernetes-based Job Execution

This implementation leverages Kubernetes to manage batch job executions dynamically.

Batch jobs are defined in advance within Kubernetes as YAML job templates, along with all required environment variables and configuration settings (e.g., secrets, data source credentials). These job templates exist as Kubernetes resources (ConfigMap). The server retrieves these predefined YAML templates via Kubernetes API and instantiates them dynamically. 

Kubernetes does not allow creation of Jobs without immediately starting them. Thus, the "hack" to configure the job-template via a Config Map.

#### Sequence Diagram

```mermaid
sequenceDiagram
    actor Client
    box Batch API K8s
        participant JobGatewayController
        participant KubernetesJobLauncherService
    end

    participant Batch DB

    participant K8s API
    
    participant Job Pod
    
    Client ->> JobGatewayController: POST /jobs { jobName, parameters }
    JobGatewayController ->> KubernetesJobLauncherService: Launch Job
    activate KubernetesJobLauncherService
    KubernetesJobLauncherService ->> KubernetesJobLauncherService: Validate Job
    KubernetesJobLauncherService ->> Batch DB: Save Job
    KubernetesJobLauncherService -->> JobGatewayController: Job launched
    deactivate KubernetesJobLauncherService

    JobGatewayController -->> Client: 200 - OK { externalJobExecutionId }
    
    activate KubernetesJobLauncherService
    KubernetesJobLauncherService ->> K8s API: Create K8s Job from Template { externalJobExecutionId }
    activate K8s API
    K8s API -->> KubernetesJobLauncherService: K8s Job launched

    deactivate KubernetesJobLauncherService
    K8s API -->> Job Pod: Creating Job Pod
    deactivate K8s API
    activate Job Pod
    Job Pod ->> Batch DB: Load Job { externalJobExecutionId }
    Job Pod ->> Job Pod: Validate Job
    Job Pod ->> Job Pod: Run Job
    Job Pod ->> Batch DB: Update Status
    deactivate Job Pod

```


---
## Module Overview

- **batch-core**  
  Contains core interfaces and shared utilities used across multiple modules.

- **batch-extapi**  
  Defines the request and response POJOs for the external HTTP API.

- **batch-extservice**  
  Provides Spring MVC REST controllers to delegate job execution requests to different execution strategies and handle HTTP interactions.

- **batch-persistence**  
  Handles the persistence of the `AcceptedJob` entity. This module is used by different applications for both writing and reading job execution data.

- **Launcher Strategies**  
  Contains different job execution strategies:
    - **launcher-http**: Forwards job execution requests to another HTTP-Worker services (Job API Gateway).
    - **launcher-kubernetes**: Dynamically starts Kubernetes jobs for batch execution.
    - **launcher-local**: Executes the batch job locally within the application.

- **Job Modules (sample/jobs)**  
  Contain specific Spring Batch job implementations:
    - **job-bill**: Example batch job that writes billing data into the database.
    - **job-greeting**: Simple batch job that prints a greeting message to the console.

- **Sample Apps (sample/apps)**  
  Spring Boot applications demonstrating different execution strategies:
    - **job-app-http-greeting-local**: HTTP server that executes the Greeting Job locally.
    - **job-app-http-kubernetes**: HTTP API server that forwards job execution to Kubernetes.
    - **job-app-http-proxy**: API Gateway that forwards job execution requests to registered HTTP worker services.
    - **job-app-shell-greeting-local**: Spring Boot application that executes the Greeting Job as an `ApplicationRunner`, used as a worker job in the Kubernetes example.