package com.sb1.clients;

import com.sb1.dto.HhVacancyDetailDto;
import com.sb1.dto.HhVacancyDto;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HhClientTest {

    private MockWebServer mockWebServer;
    private HhClient hhClient;

    @BeforeEach
    void setUp() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        WebClient webClient = WebClient.builder()
                .baseUrl(mockWebServer.url("/").toString())
                .build();

        hhClient = new HhClient();

        ReflectionTestUtils.setField(hhClient, "webClient", webClient);
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void searchAllVacancies_shouldReturnAllPages() {

        String page1 = """
                {
                  "items": [
                    {"id": "1", "name": "Java dev"},
                    {"id": "2", "name": "Backend dev"}
                  ]
                }
                """;

        String page2 = """
                {
                  "items": [
                    {"id": "3", "name": "Senior Java"}
                  ]
                }
                """;

        String emptyPage = """
                {
                  "items": []
                }
                """;

        mockWebServer.enqueue(new MockResponse().setBody(page1).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody(page2).addHeader("Content-Type", "application/json"));
        mockWebServer.enqueue(new MockResponse().setBody(emptyPage).addHeader("Content-Type", "application/json"));

        List<HhVacancyDto> result = hhClient.searchAllVacancies(
                "Java",
                96,
                "between1And3",
                "publication_time",
                10,
                "2024-01-01"
        );

        assertEquals(3, result.size());
    }

    @Test
    void getVacancyById_shouldReturnVacancy() {

        String responseJson = """
                {
                  "id": "123",
                  "name": "Java Developer"
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(responseJson)
                        .addHeader("Content-Type", "application/json")
        );

        HhVacancyDetailDto result = hhClient.getVacancyById("123");

        assertNotNull(result);
        assertEquals("123", result.getId());
    }

}