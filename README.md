# wikimedia-kafka-payment
build docker image for every module
./mvnw spring-boot:build-image -DskipTests -Dspring-boot.build.skip=true

docker build -t vtnhanhcmus/wiki-request:latest .
docker build -t vtnhanhcmus/wiki-booking:latest .
docker build -t vtnhanhcmus/wiki-payment:latest .