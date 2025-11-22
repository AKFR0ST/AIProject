package com.sb1.interfaces;

import com.sb1.services.GigaChatAPIService;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.sb1.enums.NNServices.GIGA_CHAT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SendRequestTest {
    @Mock
    GigaChatAPIService gigaChatAPIService;
    @InjectMocks
    SendRequestImpl sendRequest;


    @Test
    void test_send_text_to_text_request() {
        String testText = RandomStringUtils.randomAlphabetic(10);
        when(gigaChatAPIService.textToTextRequest(any(), any())).thenReturn(testText);

        assertEquals(testText, sendRequest.sendTextToTextRequest(testText, testText, GIGA_CHAT));
    }
}