package com.wikimedia.booking.services;

import com.wikimedia.basedomain.BookingRequest;
import com.wikimedia.booking.entity.Booking;
import com.wikimedia.booking.repositories.BookingRepository;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class WikimediaService implements CommandLineRunner {

    private Logger log = LoggerFactory.getLogger(WikimediaService.class);

    @Autowired
    private ReactiveKafkaConsumerTemplate<String, BookingRequest> reactiveKafkaConsumerTemplate;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, BookingRequest> reactiveKafkaProducerTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${topic.payment}")
    private String topicPayment;

    public Flux<BookingRequest> consumerBookingRequest(){
        return reactiveKafkaConsumerTemplate
                .receiveAutoAck()
                .delayElements(Duration.ofSeconds(5L)) // BACKPRESSURE
                .doOnNext(consumerRecord -> log.info("received key={}, value={} from topic={}, offset={}",
                        consumerRecord.key(),
                        consumerRecord.value(),
                        consumerRecord.topic(),
                        consumerRecord.offset())
                )
                .map(ConsumerRecord::value)
                .doOnNext(bookingRequest -> {
                    log.info("successfully consumed {}",  bookingRequest);
                    Booking.BookingBuilder bookingBuilder = Booking.builder();
                    bookingBuilder.createDate(LocalDateTime.now());
                    bookingBuilder.userId(bookingRequest.getUserId());
                    bookingBuilder.wikiId(bookingRequest.getWikiId());
                    bookingBuilder.status("REQUEST");
                    Booking booking = bookingBuilder.build();
                    bookingRepository.save(booking);
                    bookingRequest.setId(booking.getId());

                })
                .doOnNext(bookingRequest -> {
                    reactiveKafkaProducerTemplate.send(topicPayment, bookingRequest);
                })
                .doOnError(throwable -> log.error("something bad happened while consuming : {}", throwable.getMessage()));
    }

    @Override
    public void run(String... args){
        consumerBookingRequest().subscribe();
    }
}
