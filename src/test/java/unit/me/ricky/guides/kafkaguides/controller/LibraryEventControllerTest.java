package me.ricky.guides.kafkaguides.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import me.ricky.guides.kafkaguides.domain.LibraryEvent;
import me.ricky.guides.kafkaguides.producer.LibraryEventsProducer;
import me.ricky.guides.kafkaguides.util.TestUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LibraryEventController.class)
class LibraryEventControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    LibraryEventsProducer libraryEventsProducer;

    @Test
    void postLibraryEvent() throws Exception {
        //given
        String eventJson = objectMapper.writeValueAsString(TestUtil.libraryEventRecord());
        doNothing().when(libraryEventsProducer).sendLibraryEvent_producer_record(isA(LibraryEvent.class));

        //when
        ResultActions perform = mockMvc.perform(post("/v1/libraryevent")
                .content(eventJson)
                .contentType(MediaType.APPLICATION_JSON));

        //then
        perform.andExpect(status().isOk());
    }
}