package com.example.moviesearch.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Klasse, som kan bruges til at displaye en film med en aiRecommendation.
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDtoWithAiRecommendation {
    private MovieDto movie;
    private String aiRecommendation;
}