# wikimedia-kafka-payment
build docker image for every module
./mvnw spring-boot:build-image -DskipTests -Dspring-boot.build.skip=true

docker build -t custom-jenkins-docker .

docker build -t vtnhanhcmus/wiki-request:latest .
docker build -t vtnhanhcmus/wiki-booking:latest .
docker build -t vtnhanhcmus/wiki-payment:latest .


kubectl proxy
http://localhost:8001/api/v1/namespaces/kubernetes-dashboard/services/https:kubernetes-dashboard:/proxy/.
https://github.com/kubernetes/dashboard/blob/master/docs/user/access-control/creating-sample-user.md
