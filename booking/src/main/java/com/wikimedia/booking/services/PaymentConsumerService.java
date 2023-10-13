package com.wikimedia.booking.services;

import com.wikimedia.basedomain.PaymentRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;

@Service
public class PaymentConsumerService implements CommandLineRunner {

    @Autowired
    private ReactiveKafkaConsumerTemplate<String, PaymentRequest> paymentKafkaConsumerTemplate;

    @Value("${topic.payment}")
    private String topicPayment;

    @Override
    public void run(String... args) throws Exception {

    }
}
