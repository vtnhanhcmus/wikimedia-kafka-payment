kubectl apply -f 1_config-map.yml
kubectl apply -f 2_wiki-postgres.yaml
kubectl apply -f 3_wiki_zoo.yaml 
kubectl apply -f 4_wiki-kafka.yml
# kubectl apply -f 5_wiki_kafka-schema-registry.yaml 
kubectl apply -f 6_request-deployment.yml
kubectl apply -f 7_wiki_booking.yaml