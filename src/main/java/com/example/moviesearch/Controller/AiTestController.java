package com.example.moviesearch.Controller;


import com.example.moviesearch.Service.AiService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.List;

@RestController
@RequestMapping("/api/ai-test")
@CrossOrigin(origins = "*")
public class AiTestController {

    private final AiService aiService;

    public AiTestController(AiService aiService) {
        this.aiService = aiService;
    }

    @GetMapping("/recommendations")
    public List<String> getAiMovieRecommendations(@RequestParam String prompt) {
        return aiService.generateMovieTitlesFromPrompt(prompt);
    }
}