package com.example.moviesearch.Model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

// MovieDTO, men også et objekt. Får data fra API og ""oversætter" det med en DTO.
@Data
@NoArgsConstructor // Genererer en konstruktør uden argumenter
@AllArgsConstructor // Genererer en konstruktør med alle argumenter
public class MovieDto {
    private Integer id;
    private String title;
    private String releaseDate;
    private Double voteAverage;
    private String posterPath;
    private String overview;
    private List<GenreDto> genres;

}
