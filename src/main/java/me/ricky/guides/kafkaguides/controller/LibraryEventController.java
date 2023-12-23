package me.ricky.guides.kafkaguides.controller;

import lombok.extern.slf4j.Slf4j;
import me.ricky.guides.kafkaguides.domain.LibraryEvent;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class LibraryEventController {
    @PostMapping("/v1/libraryevent")
    public LibraryEvent postLibraryEvent(@RequestBody LibraryEvent libraryEvent) {
        log.info("libraryEvent: {}", libraryEvent);
        return libraryEvent;
    }
}
