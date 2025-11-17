package com.example.moviesearch.Controller; // Bemærk, din pakke hedder Controller, ikke controller

import com.example.moviesearch.Model.GenreDto;
import com.example.moviesearch.Model.MovieDto;
import com.example.moviesearch.Model.MovieDtoWithAiRecommendation;
import com.example.moviesearch.Service.MovieService; // Bemærk, din pakke hedder Service, ikke service
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@CrossOrigin(origins = "*")
public class MovieController {

    private final MovieService movieService;

    // MovieController skal kun kende MovieService
    public MovieController(MovieService movieService) {
        this.movieService = movieService;
    }

    // Metode returnerer 20 populære film
    // Try catch, ved fejl
    @GetMapping("/popular")
    public ResponseEntity<List<MovieDto>> getPopularMovies() {
        try {
            List<MovieDto> movies = movieService.fetchPopularMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("Fejl ved hentning af populære film: " + e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // En post metode, som modtager prompt i browser og returnerer film.
    @PostMapping("/ai-recommend")
    public ResponseEntity<List<MovieDto>> getAiRecommendations(@RequestBody String prompt) {
        try {
            List<MovieDto> movies = movieService.findMoviesByAiPrompt(prompt);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("Fejl ved AI-anbefaling: " + e.getMessage());
            e.printStackTrace(); // Godt til fejlfinding
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // En Get metode, som henter flere film på baggrund af en søgning.
    @GetMapping("/search")
    public ResponseEntity<List<MovieDto>> searchMovies(@RequestParam String query) {
        try {
            List<MovieDto> movies = movieService.searchMoviesMulti(query);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("Fejl ved almindelig søgning: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // Get Metode, som henter genre til drop-down
    @GetMapping("/genres")
    public ResponseEntity<List<GenreDto>> getGenres() {
        try {
            List<GenreDto> genres = movieService.fetchGenres();
            return ResponseEntity.ok(genres);
        } catch (Exception e) {
            System.err.println("Fejl ved hentning af genrer: " + e.getMessage());
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // Get metode, som henter film på baggrund af valg genre i drop-down
    @GetMapping("/by-genre")
    public ResponseEntity<List<MovieDto>> getMoviesByGenre(@RequestParam Integer genreId) {
        try {
            List<MovieDto> movies = movieService.fetchMoviesByGenre(genreId);
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("Fejl ved hentning af film efter genre: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // Get Metode, som henter film oplysninger på baggrund af filmens id
    @GetMapping("/{id}")
    public ResponseEntity<MovieDto> getMovieDetails(@PathVariable Integer id) {
        try {
            MovieDto movie = movieService.fetchMovieDetails(id);
            if (movie != null) {
                return ResponseEntity.ok(movie);
            }
            return ResponseEntity.notFound().build(); // Hvis filmen ikke findes
        } catch (Exception e) {
            System.err.println("Fejl ved hentning af filmdetaljer for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build(); // Generisk fejl
        }
    }

    // Get Metode, som henter film detaljer og ai beskrivelse på baggrund af id.
    @GetMapping("/details-with-ai/{id}")
    public ResponseEntity<MovieDtoWithAiRecommendation> getMovieDetailsWithAi(@PathVariable Integer id) {
        try {
            MovieDtoWithAiRecommendation movieDetails = movieService.fetchMovieDetailsWithAiRecommendation(id);
            if (movieDetails != null) {
                return ResponseEntity.ok(movieDetails);
            }
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            System.err.println("Fejl ved hentning af filmdetaljer med AI for ID " + id + ": " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).build();
        }
    }

    // Get metode, som henter film som er i biffen.
    @GetMapping("/now-playing") // Nyt endpoint for aktuelle film
    public ResponseEntity<List<MovieDto>> getNowPlayingMovies() {
        try {
            List<MovieDto> movies = movieService.fetchNowPlayingMovies();
            return ResponseEntity.ok(movies);
        } catch (Exception e) {
            System.err.println("Fejl ved hentning af aktuelle film: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(500).body(List.of());
        }
    }

    // Fjern GET /search endpoint herfra for nu, for at simplificere og fokusere på AI.
    // Hvis du vil have en dedikeret søgefunktion ud over AI, kan du implementere den senere.
}