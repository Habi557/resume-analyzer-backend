package com.resume.backend.helperclass;

import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
@Component
@Slf4j
public class AiApis {
    @Autowired
    ChatClient chatClient;
    @PostConstruct
    public  void AiAPisMethodCalled(){
        log.info("AiApis setup method called {}");
        //String hello = callAiService("Hello");
       // log.info("Ai called {}", hello);
    }
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
