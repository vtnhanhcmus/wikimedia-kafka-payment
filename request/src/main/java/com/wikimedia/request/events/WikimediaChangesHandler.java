package com.wikimedia.request.events;

import com.google.gson.Gson;
import com.launchdarkly.eventsource.EventHandler;
import com.launchdarkly.eventsource.MessageEvent;
import com.wikimedia.basedomain.BookingRequest;
import com.wikimedia.basedomain.WikiData;
import com.wikimedia.request.services.BookingRequestService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.core.publisher.Mono;

import java.util.concurrent.TimeUnit;

@NoArgsConstructor
public class WikimediaChangesHandler implements EventHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(WikimediaChangesHandler.class);

    private ReactiveKafkaProducerTemplate<String, BookingRequest> reactiveKafkaProducerTemplate;
    private String topic;

    private BookingRequestService bookingRequestService;

    public WikimediaChangesHandler(ReactiveKafkaProducerTemplate<String, BookingRequest> reactiveKafkaProducerTemplate, String topic, BookingRequestService bookingRequestService) {
        this.reactiveKafkaProducerTemplate = reactiveKafkaProducerTemplate;
        this.topic = topic;
        this.bookingRequestService = bookingRequestService;
    }

    @Override
    public void onOpen() throws Exception {

    }

    @Override
    public void onClosed() throws Exception {

    }

    @Override
    public void onMessage(String s, MessageEvent messageEvent){
        LOGGER.info(String.format("event data -> %s", messageEvent.getData()));
        WikiData wikiData = new Gson().fromJson(messageEvent.getData(), WikiData.class);

        BookingRequest bookingRequest = bookingRequestService.createBookingRequest(wikiData);
        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        reactiveKafkaProducerTemplate.send(topic, bookingRequest)
                .doOnSuccess(senderResult -> LOGGER.info("sent {} offset : {}", bookingRequest, senderResult.recordMetadata().offset()))
                .subscribe();
    }

    @Override
    public void onComment(String s) throws Exception {

    }

    @Override
    public void onError(Throwable throwable) {

    }
}
