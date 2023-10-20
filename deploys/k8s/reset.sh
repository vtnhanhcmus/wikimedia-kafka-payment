# kubectl scale deployment wiki-zoo-deployment --replicas=0
# kubectl scale deployment wiki-kafka-deployment --replicas=0
# kubectl scale deployment wiki-kafka-schema-registry-deployment --replicas=0
# kubectl scale deployment wiki-request-deployment --replicas=0
# kubectl scale deployment wiki-booking-deployment --replicas=0
kubectl scale deployment --all --replicas=0
kubectl delete pods --all
kubectl delete services --all