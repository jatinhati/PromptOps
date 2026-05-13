# Kubernetes Pods

In Kubernetes, a Pod is the smallest and simplest unit in the K8s object model that you create or deploy.
- **Concept**: A Pod represents a single instance of a running process in your cluster. It can contain one or more containers, such as Docker containers.
- **Shared Context**: Containers within the same Pod share the same network namespace, IP address, and storage volumes.
- **Lifecycle**: Pods are considered ephemeral, disposable entities. If a Pod fails, Kubernetes can automatically create a new replica of that Pod to maintain the desired state.
- **kubectl**: To get a list of all running pods, use the command: `kubectl get pods`