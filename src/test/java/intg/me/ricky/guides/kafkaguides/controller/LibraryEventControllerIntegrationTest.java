package me.ricky.guides.kafkaguides.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import me.ricky.guides.kafkaguides.domain.LibraryEvent;
import me.ricky.guides.kafkaguides.util.TestUtil;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.serialization.IntegerDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.TestPropertySource;

import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    ObjectMapper objectMapper;

    private Consumer<Integer, String> consumer;

    @BeforeEach
    void setUp() {
        Map<String, Object> consumerProps = KafkaTestUtils.consumerProps("group1", "true", embeddedKafkaBroker);
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        // consumer 생성 (테스트)
        consumer = new DefaultKafkaConsumerFactory<>(consumerProps, new IntegerDeserializer(), new StringDeserializer()).createConsumer();
        // EmbeddedKafkaBroker 에 테스트 consumer 등록
        embeddedKafkaBroker.consumeFromAllEmbeddedTopics(consumer);
    }

    @AfterEach
    void tearDown() {
        // consumer 종료
        consumer.close();
    }

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

        // 비동기 방식 검증 처리 => blocking 하여 Kafka message consumer Records를 가져온다.
        ConsumerRecords<Integer, String> consumerRecords = KafkaTestUtils.getRecords(consumer);
        assertThat(consumerRecords.count()).isEqualTo(1);

        consumerRecords.forEach(record -> {
            LibraryEvent libraryEventActual = TestUtil.parseLibraryEventRecord(objectMapper, record.value());
            System.out.println("libraryEventActual: " + libraryEventActual);
            assertThat(libraryEventActual).isEqualTo(libraryEvent);
        });

    }
}