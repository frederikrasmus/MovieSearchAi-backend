    package com.example.moviesearch.Model;

    import lombok.Getter;
    import lombok.Setter;

    import java.util.ArrayList;
    import java.util.List;

    // Chat Completion Request til at spørge en Ai et spørgsmål
    // Returnerer svar med Response klassen.
    // Betragt det som en DTO
    @Getter
    @Setter
    public class ChatCompletionRequest {

        private String model;
        private List<Message> messages = new ArrayList<>();
        private double temperature;
        private int max_tokens;
        private double top_p;
        private double frequency_penalty;
        private double presence_penalty;

        // En klar definition af hvad en besked indeholder af parameter, rolle og indhold.
        @Getter
        @Setter
        public static class Message {
            private String role;
            private String content;

            public Message(String role, String content) {
                this.role = role;
                this.content = content;
            }
        }
    }
