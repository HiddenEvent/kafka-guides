package me.ricky.guides.kafkaguides.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.extern.slf4j.Slf4j;
import me.ricky.guides.kafkaguides.domain.LibraryEvent;
import me.ricky.guides.kafkaguides.producer.LibraryEventsProducer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

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
    public LibraryEvent postLibraryEvent(@RequestBody LibraryEvent libraryEvent) throws JsonProcessingException, ExecutionException, InterruptedException, TimeoutException {
        log.info("libraryEvent: {}", libraryEvent);
//        libraryEventsProducer.sendLibraryEvent(libraryEvent);
        libraryEventsProducer.sendLibraryEvent_block_wait(libraryEvent);
//        libraryEventsProducer.sendLibraryEvent_producer_record(libraryEvent);
        log.info("After sendLibraryEvent 호출");
        return libraryEvent;
    }
}
