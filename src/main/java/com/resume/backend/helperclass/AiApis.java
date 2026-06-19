package com.resume.backend.helperclass;

import com.resume.backend.exceptions.AiNotRespondingException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
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
        //String hello = callAiService("Hello");
       // log.info("Ai called {}", hello);
    }
    @CircuitBreaker(name = "aiCircuitBreaker", fallbackMethod = "fallbackCallAiService")
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
    public String fallbackCallAiService(String text, Throwable ex) {
        log.error("AI service unavailable, circuit breaker triggered: {}", ex.getMessage());
        //return "AI service is currently unavailable. Please try again later for fallback.";
        throw new AiNotRespondingException("Ai service is down try again later from fallback");
    }
}
