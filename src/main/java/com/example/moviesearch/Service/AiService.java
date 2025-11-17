package com.example.moviesearch.Service;

import com.example.moviesearch.Model.ChatCompletionRequest;
import com.example.moviesearch.Model.ChatCompletionResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AiService {

    private final WebClient webClient;

    // Application Properties
    @Value("${openai.api.key}")
    private String openaiApiKey;

    @Value("${openai.url}")
    private String openaiUrl;

    @Value("${openai.model}")
    private String openaiModel;

    @Value("${openai.temperature}")
    private double openaiTemperature;

    @Value("${openai.max_tokens}")
    private int openaiMaxTokens;

    public AiService() {
        this.webClient = WebClient.builder().build();
    }

    // Metode som returnerer filmtitler på baggrund af en prompt til openAi.
    public List<String> generateMovieTitlesFromPrompt(String userPrompt) {
        String systemMessage = "Du er en filmanbefaler. " +
                "Brugeren ønsker at se en film baseret på følgende beskrivelse: '" + userPrompt + "'. " +
                "Foreslå 3-5 filmtitler, der matcher godt. " +
                "Returner kun titlerne, adskilt af et komma, uden yderligere tekst. " +
                "Eksempel: 'The Matrix, Inception, Blade Runner 2049'";

        // Requesten bliver injected med parametre. Som er defineret i app properties.
        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(openaiModel);
        requestDto.setTemperature(openaiTemperature);
        requestDto.setMax_tokens(openaiMaxTokens);
        // ChatCompletionRequest indlejret klasse Message injectes med systemMessage og userPrompt.
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", systemMessage));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

        // Mono er asynkron, som ikke blokerer andet kode.
        // Et løfte om en respons i fremtiden.
        Mono<ChatCompletionResponse> responseMono = webClient.post()
                .uri(openaiUrl)
                .header("Authorization", "Bearer " + openaiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto)) // Sender DTO som JSON
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class);

        // Vent på svar, vi kører ikke asynkron.
        // Fokus på skalerbarhed. I fremtiden kan applikationen blive asynkron.
        // Jeg kunne også have brugt RestTemplate, postForObject().
        // Men valgte webClient som i dette tilfælde er lidt modsigende en Mono, som er asynkron.
        // I tilfælde af asynkron, kunne vi risikere at controlleren, returnerede for tidligt
        // Vi ville skulle blockere i controlleren alligevel så.
        ChatCompletionResponse response = responseMono.block();

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            String aiResponseContent = response.getChoices().get(0).getMessage().getContent();
            // Pars den kommaseparerede streng til en liste af titler
            return Arrays.stream(aiResponseContent.split(","))
                    .map(String::trim) // Trim whitespace med ->
                    .filter(s -> !s.isEmpty()) // Fjern tomme strenge
                    .collect(Collectors.toList());
        }
        // Ved fejl returner tom liste
        return List.of();
    }

    // Metode som genererer en kort beskrivelse med fakta omkring en film
    public String generateMovieSummary(String movieTitle, String movieOverview) {
        String systemMessage = "Du er en filmanbefaler. " +
                "Lav en kort, fængende tekst med en fun fact eller teknisk detalje for filmnørder på dansk af filmen. " +
                "Fokuser på dens kerneelementer, men gør det kort (max 50 ord).";
        String userPrompt = "Filmtitel: " + movieTitle + "\nBeskrivelse: " + movieOverview;

        // Samme koncept som anden metode.
        ChatCompletionRequest requestDto = new ChatCompletionRequest();
        requestDto.setModel(openaiModel);
        requestDto.setTemperature(0.3); // Lavere temperatur, for mere faktabaseret svar.
        requestDto.setMax_tokens(100); // Lidt flere tokens, men stadig kort
        requestDto.getMessages().add(new ChatCompletionRequest.Message("system", systemMessage));
        requestDto.getMessages().add(new ChatCompletionRequest.Message("user", userPrompt));

        Mono<ChatCompletionResponse> responseMono = webClient.post()
                .uri(openaiUrl)
                .header("Authorization", "Bearer " + openaiApiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(requestDto))
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class);

        ChatCompletionResponse response = responseMono.block();

        if (response != null && response.getChoices() != null && !response.getChoices().isEmpty()) {
            return response.getChoices().get(0).getMessage().getContent();
        }
        return "Kunne ikke generere en AI-anbefaling for denne film."; // Fejlbesked
    }
}