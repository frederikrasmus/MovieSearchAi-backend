package com.example.moviesearch.Service;
import com.example.moviesearch.Model.MovieDtoWithAiRecommendation;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.moviesearch.Model.GenreDto;
import com.example.moviesearch.Model.MovieDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient; // Korrekt import for WebClient
import reactor.core.publisher.Mono;




import java.util.List;
import java.util.stream.Collectors;

@Service
public class MovieService {

    private final WebClient webClient;
    private final AiService aiService;

    @Value("${tmdb.api.key}")
    private String tmdbApiKey;



    private static final String TMDB_IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500";

    // WebClient konfigureret TMDB base url.
    // Efterfølgende kan man specificere med eks. "movie/popular
    public MovieService(AiService aiService) {
        this.webClient = WebClient.builder()
                .baseUrl("https://api.themoviedb.org/3")
                .build();
        this.aiService = aiService;
    }

    // Metode som henter detaljer på film med filmens id
    public MovieDto fetchMovieDetails(Integer movieId) {
        // Bygger URL til TMDB endpoint med api nøgle og på dansk.
        String url = "/movie/" + movieId + "?api_key=" + tmdbApiKey + "&language=da-DK";

        // Her sender vi et get kald og konverterer en TMDB film objekt.
        Mono<TmdbMovieApiResponse.TmdbMovie> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TmdbMovieApiResponse.TmdbMovie.class); // <-- Direkte til TmdbMovie

        // Igen vi blokerer og venter på svaret.
        TmdbMovieApiResponse.TmdbMovie tmdbMovie = responseMono.block();

        // Hvis vi modtager svar, mapper vi til MovieDTO
        if (tmdbMovie != null) {
            // Metoden mapToMovieDto, hjælper med at konverterer til en film fra TMDB til MovieDto.
            return mapToMovieDto(tmdbMovie);
        }
        return null; // Returner null, hvis filmen ikke kan findes
    }

    public List<MovieDto> fetchPopularMovies() {

        // Endpoint for populære film
        String url = "/movie/popular?api_key=" + tmdbApiKey + "&language?=da-DK";

        // TMDB's svar har en "results" liste, så vi skal lave en wrapper DTO
        // Fordi at vi får flere film i en liste
        Mono<TmdbMovieApiResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve() // Anmod og hent svar
                .bodyToMono(TmdbMovieApiResponse.class); // Konverter svar til dto

        // Endnu en block()
        TmdbMovieApiResponse response = responseMono.block();

        if(response != null && response.getResults() !=null) {
            // Map TMDB egen dto til moviedto som sendes til frontend
            return response.getResults().stream()
                    .map(this::mapToMovieDto) // Kalder helper-metode til at mappe TMDB movie til vores MovieDto
                    .collect(Collectors.toList()); // TMDB film bliver transformeret til et MovieDto objekt.
        }
        return List.of(); // Returner tom liste ved fejl
    }

    // Metode til at søge efter specifik film
    // Applikationen bruger denne når den søger efter film med ai-prompt
    public MovieDto searchMovies(String query) {
        String url = "/search/movie?api_key=" + tmdbApiKey + "&language=da-DK&query=" + query;

        Mono<TmdbMovieApiResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TmdbMovieApiResponse.class);

        TmdbMovieApiResponse response = responseMono.block();

        if (response != null && response.getResults() != null && !response.getResults().isEmpty()) {
            return mapToMovieDto(response.getResults().get(0)); // Tag første (bedste) match
        }
        return null; // Returner null, hvis ingen film findes
    }

    // Finder film med ai prompts, bruger SearchMovies
    public List<MovieDto> findMoviesByAiPrompt(String userPrompt) {
        List<String> aiMovieTitles = aiService.generateMovieTitlesFromPrompt(userPrompt);

        return aiMovieTitles.stream()
                .map(this::searchMovies) // Brug den nye searchMovies metode for hver titel
                .filter(movieDto -> movieDto != null) // Filtrer film, der ikke kunne findes på TMDB
                .collect(Collectors.toList());
    }

    // Søger og finder flere film, maks 20 TMDB er i stand til at returnere
    public List<MovieDto> searchMoviesMulti(String query) {
        String url = "/search/movie?api_key=" + tmdbApiKey + "&language=da-DK&query=" + query;

        Mono<TmdbMovieApiResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TmdbMovieApiResponse.class);

        TmdbMovieApiResponse response = responseMono.block();

        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .map(this::mapToMovieDto) // Mapper alle fundne TMDB movies
                    .collect(Collectors.toList());
        }
        return List.of(); // Returner tom liste, hvis intet findes
    }

    // Henter genre
    public List<GenreDto> fetchGenres() {
        String url = "/genre/movie/list?api_key=" + tmdbApiKey + "&language=da-DK";

        Mono<TmdbGenreApiResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TmdbGenreApiResponse.class);

        TmdbGenreApiResponse response = responseMono.block();

        if (response != null && response.getGenres() != null) {
            return response.getGenres(); // TMDB's Genre objekt matcher typisk vores DTO ret godt
        }
        return List.of();
    }

    // Henter film ved genre, bruges i dropdown.
    public List<MovieDto> fetchMoviesByGenre(Integer genreId) {
        // TMDB Discover endpoint for at filtrere efter genre
        // URL til Discover, som også kan tage api_key, language, og with_genres
        String url = UriComponentsBuilder.fromPath("/discover/movie")
                .queryParam("api_key", tmdbApiKey)
                .queryParam("language", "da-DK")
                .queryParam("with_genres", genreId) // Her specificerer vi genre id
                .build()
                .toUriString();

        Mono<TmdbMovieApiResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TmdbMovieApiResponse.class);

        TmdbMovieApiResponse response = responseMono.block();

        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .map(this::mapToMovieDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    // Hent film detaljer og ai anbefaling
    public MovieDtoWithAiRecommendation fetchMovieDetailsWithAiRecommendation(Integer movieId) {
        MovieDto movie = fetchMovieDetails(movieId); // Genbruger eksisterende metode til at hente filmen

        if (movie != null) {
            String aiRecommendation = aiService.generateMovieSummary(movie.getTitle(), movie.getOverview());
            return new MovieDtoWithAiRecommendation(movie, aiRecommendation); // Returner en ny DTO
        }
        return null;
    }

    // Hent aktuelle film
    public List<MovieDto> fetchNowPlayingMovies() {
        String url = "/movie/now_playing?api_key=" + tmdbApiKey + "&language=da-DK";

        Mono<TmdbMovieApiResponse> responseMono = webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(TmdbMovieApiResponse.class);

        TmdbMovieApiResponse response = responseMono.block();

        if (response != null && response.getResults() != null) {
            return response.getResults().stream()
                    .map(this::mapToMovieDto)
                    .collect(Collectors.toList());
        }
        return List.of();
    }

    // Helper-metode til at mappe TMDB's movie format til MovieDto
    private MovieDto mapToMovieDto(TmdbMovieApiResponse.TmdbMovie tmdbMovie) {
        List<GenreDto> mappedGenres;

        if (tmdbMovie.getGenres() != null && !tmdbMovie.getGenres().isEmpty()) {
            // Hvis TMDB svar allerede indeholder fulde genre objekter eks. fra /movie/{id} kaldet
            mappedGenres = tmdbMovie.getGenres(); // Brug dem direkte
        } else if (tmdbMovie.getGenre_ids() != null && !tmdbMovie.getGenre_ids().isEmpty()) {
            // Hvis TMDB kun returnerer genre-IDer eks. fra /popular eller /search lister
            mappedGenres = tmdbMovie.getGenre_ids().stream()
                    .map(id -> new GenreDto(id, "N/A")) // Opret GenreDto med ID og "N/A" som navn
                    .collect(Collectors.toList());
        } else {
            // Ingen genre-info tilgængelig
            mappedGenres = List.of();
        }

        return new MovieDto(
                tmdbMovie.getId(),
                tmdbMovie.getTitle(),
                tmdbMovie.getRelease_date(),
                tmdbMovie.getVote_average(),
                tmdbMovie.getPoster_path(),
                tmdbMovie.getOverview(),
                mappedGenres // Brug den nye mappede genreliste
        );
    }

    // --- INTERNE DTO'ER til at håndtere TMDB's API-svar ---
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TmdbMovieApiResponse {
        private Integer page;
        private List<TmdbMovie> results;
        private Integer total_pages;
        private Integer total_results;

        @Data
        @NoArgsConstructor
        @AllArgsConstructor
        public static class TmdbMovie {
            private Integer id;
            private String title;
            private String overview;
            private String release_date;
            private String poster_path;
            private Double vote_average;
            // Original: private List<Integer> genre_ids;

            // Tilføj dette felt for at fange genrer som objekter for enkeltfilm:
            private List<GenreDto> genres; // <--- NYT FELT TIL FULDE GENRE-OBJEKTER!

            // Beholder genre_ids også for at håndtere lister, hvor det kun er IDs
            private List<Integer> genre_ids;
            // Tilføj andre felter, hvis nødvendigt for andre kald
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TmdbGenreApiResponse {
        private List<GenreDto> genres; // Eller en intern Genre klasse, som så mappes til GenreDto
    }
}
