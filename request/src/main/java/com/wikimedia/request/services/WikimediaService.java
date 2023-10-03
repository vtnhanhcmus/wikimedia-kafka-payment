package com.wikimedia.request.services;

import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.EventSource;
import com.wikimedia.basedomain.BookingRequest;
import com.wikimedia.basedomain.WikiData;
import com.wikimedia.request.events.WikimediaChangesHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.concurrent.TimeUnit;

@Service
public class WikimediaService implements CommandLineRunner {

    @Value("${spring.kafka.topic.name}")
    private String topic;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, BookingRequest> reactiveKafkaProducerTemplate;

    @Autowired
    private BookingRequestService bookingRequestService;


    public void sendMessage() throws InterruptedException {
        EventHandler eventHandler = new WikimediaChangesHandler(reactiveKafkaProducerTemplate, topic, bookingRequestService);
        String url = "https://stream.wikimedia.org/v2/stream/recentchange";
        EventSource.Builder builder = new EventSource.Builder(eventHandler, URI.create(url));
        EventSource eventSource = builder.build();
        eventSource.start();

        TimeUnit.MINUTES.sleep(10);
    }

    @Override
    public void run(String... args) throws Exception {
        sendMessage();
    }
}
