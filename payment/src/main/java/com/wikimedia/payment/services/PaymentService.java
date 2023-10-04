package com.wikimedia.payment.services;

import com.wikimedia.basedomain.PaymentRequest;
import com.wikimedia.payment.entities.Payment;
import com.wikimedia.payment.repositories.PaymentRepository;
import com.wikimedia.payment.utils.RandomUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Service
@Slf4j
public class PaymentService implements CommandLineRunner {

    @Autowired
    private ReactiveKafkaConsumerTemplate<String, PaymentRequest> paymentKafkaConsumerTemplate;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, PaymentRequest> paymentKafkaProducerTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${topic.payment}")
    private String topicPayment;

    public Flux<PaymentRequest> consumerBookingRequest(){
        return paymentKafkaConsumerTemplate
                .receiveAutoAck()
                .delayElements(Duration.ofSeconds(5L)) // BACKPRESSURE
                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                )
                .map(ConsumerRecord::value)
                .doOnNext(paymentRequest -> {
                    log.info("successfully consumed {}",  paymentRequest);
                    String status = RandomUtils.generateRandomNumber();
                    Payment.PaymentBuilder paymentBuilder = Payment.builder();
                    paymentBuilder.bookingId(paymentRequest.getBookingId());
                    paymentBuilder.status(status);
                    Payment payment = paymentBuilder.build();
                    paymentRepository.save(payment);
                    paymentRequest.setStatus(status);
                })
                .doOnNext(paymentRequest -> {
                    paymentKafkaProducerTemplate.send(topicPayment, paymentRequest)
                            .doOnSuccess(senderResult -> log.info("sent {} offset : {}", paymentRequest, senderResult.recordMetadata().offset()))
                            .subscribe();
                })

                .doOnError(throwable -> log.error("something bad happened while consuming : {}", throwable.getMessage()));
    }

    @Override
    public void run(String... args){
        consumerBookingRequest().subscribe();
    }

}
