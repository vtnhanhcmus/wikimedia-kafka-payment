package com.wikimedia.payment.config;

import com.wikimedia.basedomain.PaymentRequest;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import reactor.kafka.sender.SenderOptions;

import java.util.Map;

@Configuration
public class PaymentProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String hostKafka;
    @Bean
    public ReactiveKafkaProducerTemplate<String, PaymentRequest> paymentKafkaProducerTemplate(KafkaProperties kafkaProperties){
        Map<String, Object> props = kafkaProperties.buildProducerProperties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, hostKafka);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new ReactiveKafkaProducerTemplate<>(SenderOptions.create(props));
    }

}
