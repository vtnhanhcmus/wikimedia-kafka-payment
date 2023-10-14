package com.wikimedia.payment.config;

import com.wikimedia.basedomain.PaymentRequest;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class PaymentConsumerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String hostKafka;
    @Bean
    public ReceiverOptions<String, PaymentRequest> paymentReceiverOptions(@Value(value = "${topic.payment}") String topic,
                                                                        KafkaProperties kafkaProperties) {
        Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, hostKafka);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, "payment-group");
        config.put(JsonDeserializer.USE_TYPE_INFO_HEADERS, false);
        config.put(JsonDeserializer.TRUSTED_PACKAGES,"*");
        config.put(JsonDeserializer.VALUE_DEFAULT_TYPE,"com.wikimedia.basedomain.PaymentRequest");
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        ReceiverOptions<String, PaymentRequest> basicReceiverOptions = ReceiverOptions.create(config);
        return basicReceiverOptions.subscription(Collections.singletonList(topic));
    }

    @Bean
    public ReactiveKafkaConsumerTemplate<String, PaymentRequest> paymentKafkaConsumerTemplate(ReceiverOptions<String, PaymentRequest> paymentReceiverOptions) {
        return new ReactiveKafkaConsumerTemplate<>(paymentReceiverOptions);
    }

}
