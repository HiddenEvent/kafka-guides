package me.ricky.guides.kafkaguides.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import me.ricky.guides.kafkaguides.domain.LibraryEvent;
import me.ricky.guides.kafkaguides.domain.LibraryEventType;
import me.ricky.guides.kafkaguides.producer.LibraryEventsProducer;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

@RestController
@Slf4j
public class LibraryEventController {
    public static final String BASE_URL = "/v1/libraryevent";
    private final LibraryEventsProducer libraryEventsProducer;

    public LibraryEventController(LibraryEventsProducer libraryEventsProducer) {
        this.libraryEventsProducer = libraryEventsProducer;
    }

    @PostMapping(BASE_URL)
    public LibraryEvent postLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent: {}", libraryEvent);
//        libraryEventsProducer.sendLibraryEvent(libraryEvent);
//        libraryEventsProducer.sendLibraryEvent_block_wait(libraryEvent);
        libraryEventsProducer.sendLibraryEvent_producer_record(libraryEvent);
        log.info("After sendLibraryEvent 호출");
        return libraryEvent;
    }
    @PutMapping(BASE_URL)
    public LibraryEvent updateLibraryEvent(
            @RequestBody @Valid LibraryEvent libraryEvent
    ) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException, BadRequestException {
        log.info("updateLibraryEvent: {}", libraryEvent);
        libraryEvent.validate();

        libraryEventsProducer.sendLibraryEvent_producer_record(libraryEvent);
        log.info("After updateLibraryEvent 완료");
        return libraryEvent;
    }


}
