# GUAP Project
## How to build and deploy

Deploy PostgreSQL in k8s:
```shell script
kubectl apply -f postgres-storage.yaml
kubectl apply -f postgres-deployment.yaml
kubectl apply -f postgres-service.yaml
```

Restore backup:
```shell script
psql -h mitrakoff.com -p 31432 -U mitrakov varlam < backup.sql
```

Deploy Redis in k8s:
```shell script
kubectl apply -f redis-deployment.yaml
kubectl apply -f redis-service.yaml
```

Populate Redis storage (with backup or manually):
```
set Tommy "..."
```

Build FVDS-commons dependency:
```shell script
# navigate to a library project and run:
mvn install
```

Change DB passwords:
- in `resources/META-INF/persistence.xml`
- in `resources/hibernate.properties`

Bump up Varlam version, if necessary:
- `servlets/VarlamVersion.scala`

Build a WAR:
```shell script
mvn package
```

Build Docker image:
```shell script
docker build -t mitrakov/guap .
```

Log in to Docker hub and push image:
```shell script
docker push mitrakov/guap
```

Deploy Guap in k8s (or restart the pod, if deploy already exists):
```shell script
kubectl apply -f guap-deployment.yaml       # to deploy
# or:
kubectl delete pod guap-85bfcdd44f-9s7r9    # to restart
```

Done!
