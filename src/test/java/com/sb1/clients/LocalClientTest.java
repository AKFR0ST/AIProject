package com.sb1.clients;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class LocalClientTest {

    private MockWebServer mockWebServer;
    private LocalClient localClient;

    @BeforeEach
    void setUp() throws IOException {

        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();

        localClient = new LocalClient(baseUrl, "test-token");
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    void localTextToTextRequest_shouldReturnContentFromResponse() {

        String responseJson = """
                {
                  "output": [
                    {"content": "ignore"},
                    {"content": "expected answer"}
                  ]
                }
                """;

        mockWebServer.enqueue(
                new MockResponse()
                        .setBody(responseJson)
                        .addHeader("Content-Type", "application/json")
        );

        String result = localClient.localTextToTextRequest("system prompt", "user text");

        assertEquals("expected answer", result);
    }
}