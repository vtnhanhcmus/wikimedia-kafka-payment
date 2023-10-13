package com.wikimedia.booking.services;

import com.wikimedia.basedomain.BookingRequest;
import com.wikimedia.basedomain.PaymentRequest;
import com.wikimedia.booking.entity.Booking;
import com.wikimedia.booking.repositories.BookingRepository;
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
import java.time.LocalDateTime;

@Service
@Slf4j
public class BookingService implements CommandLineRunner {


    @Autowired
    private ReactiveKafkaConsumerTemplate<String, BookingRequest> bookingKafkaConsumerTemplate;

    @Autowired
    private ReactiveKafkaProducerTemplate<String, PaymentRequest> paymentKafkaProducerTemplate;

    @Autowired
    private BookingRepository bookingRepository;

    @Value("${topic.payment}")
    private String topicPayment;

    public Flux<BookingRequest> consumerBookingRequest(){
        return bookingKafkaConsumerTemplate
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

                    PaymentRequest.PaymentRequestBuilder paymentRequestBuilder = PaymentRequest.builder();
                    paymentRequestBuilder.bookingId(bookingRequest.getId());

                    PaymentRequest paymentRequest = paymentRequestBuilder.build();
                    log.info("send msg to payment service {}", paymentRequest);

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
