package com.resume.backend.helperclass;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
@Component
public class AiApis {
    @Autowired
    ChatClient chatClient;
    public String callAiService(String text) throws RestClientException {
        String content = chatClient.prompt()
                .user(text)
                .call()
                .chatResponse()
                .getResult()
                .getOutput()
                .getContent();
        return content;

    }
}
