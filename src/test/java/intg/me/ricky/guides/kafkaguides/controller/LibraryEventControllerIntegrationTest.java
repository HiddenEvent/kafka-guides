package me.ricky.guides.kafkaguides.controller;

import lombok.extern.slf4j.Slf4j;
import me.ricky.guides.kafkaguides.domain.LibraryEvent;
import me.ricky.guides.kafkaguides.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@EmbeddedKafka(topics = {"library-events"})
@TestPropertySource(properties = {
        "spring.kafka.producer.bootstrap-servers=${spring.embedded.kafka.brokers}",
        "spring.kafka.admin.properties.bootstrap.servers=${spring.embedded.kafka.brokers}"})
@Slf4j
class LibraryEventControllerIntegrationTest {
    @Autowired
    TestRestTemplate restTemplate;

    @Test
    void postLibraryEvent() {
        //given
        LibraryEvent libraryEvent = TestUtil.libraryEventRecord();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<LibraryEvent> request = new HttpEntity<>(libraryEvent, headers);

        //when
        ResponseEntity<LibraryEvent> response = restTemplate.postForEntity(LibraryEventController.BASE_URL, request, LibraryEvent.class);

        //then
        log.info("response 데이터 확인: {}", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }
}