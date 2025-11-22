package com.sb1.services;

import com.sb1.clients.GigaChatClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GigaChatAPIServiceTest {

    @Mock
    private GigaChatClient gigaChatClient;

    @InjectMocks
    private GigaChatAPIService gigaChatAPIService;

    @Test
    void test_text_to_text_request() {
        String testText = RandomStringUtils.randomAlphabetic(10);
        when(gigaChatClient.gigaChatTextToTextRequest(any(), any())).thenReturn(testText);

        assertEquals(testText, gigaChatAPIService.textToTextRequest(testText, testText));
    }
}