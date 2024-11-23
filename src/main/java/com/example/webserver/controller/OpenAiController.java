package com.example.webserver.controller;

import com.example.webserver.service.OpenAiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/openai")
public class OpenAiController {

    private final OpenAiService openAiService;

    public OpenAiController(OpenAiService chatGPTService) {
        this.openAiService = chatGPTService;
    }

    @GetMapping("/chat")
    public ResponseEntity<Map<String, Object>> chat(@RequestParam(name = "prompt") String prompt) {
        return openAiService.getChatResponse(prompt);
    }
}