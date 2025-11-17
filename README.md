# 🎬 MovieSearch AI - Backend

Denne Spring Boot applikation fungerer som backend-tjeneste for MovieSearch AI frontend. Den aggregerer data fra The Movie Database (TMDB) API'en og genererer AI-drevne filmanbefalinger og opsummeringer via OpenAI API'en.


## ✨ Funktioner

*   **TMDB-integration:** Henter populære film, aktuelle film, film efter genre, søgeresultater og detaljer for enkelte film fra TMDB API'en.
*   **OpenAI-integration:**
    *   Genererer filmanbefalinger baseret på brugerbeskrivelser ved hjælp af OpenAI's Chat Completion API.
    *   Genererer kortfattede, fængende film opsummeringer med en "fun fact" til filmdetaljesiden.
*   **Data Mapping:** Mapper rå TMDB API-svar til egne, rene `MovieDto` og `GenreDto` for at opretholde separation of concerns.
*   **RESTful API:** Eksponerer RESTful endpoints, der kan forbruges af frontend-applikationer.
*   **CORS-understøttelse:** Konfigureret til at tillade forespørgsler fra enhver frontend-domæne (`*`).


## 🚀 Kom godt i gang

Følg disse trin for at få backend-applikationen til at køre lokalt på din maskine.

### Forudsætninger

*   **Java 17 (eller nyere):** SDK skal være installeret.
*   **Maven:** Byggestyringsværktøj, typisk inkluderet med Java IDE'er.
*   **En TMDB API-nøgle:**
    1.  Opret en konto på [TMDB](https://www.themoviedb.org/signup).
    2.  Anmod om en API-nøgle (typisk under din profil -> indstillinger -> API).
*   **En OpenAI API-nøgle:**
    1.  Opret en konto på [OpenAI](https://platform.openai.com/).
    2.  Generer en ny hemmelig API-nøgle (under API keys i din profil).


### Installation og Kørsel

1.  **Klon repository'et:**
    ```bash
    git clone https://github.com/frederikrasmus/MovieSearchAi-backend.git
    ```
2.  **Naviger til projektmappen:**
    ```bash
    cd MovieSearchAi-backend
    ```
3.  **Konfigurer API-nøgler:**
    *   Opret filen `src/main/resources/application.properties` (hvis den ikke allerede findes).
    *   Indsæt dine API-nøgler og konfigurationsindstillinger i denne fil:
        ```properties
        # TMDB API Konfiguration
        tmdb.api.key=DIN_TMDB_API_NØGLE_HER

        # OpenAI API Konfiguration
        openai.api.key=DIN_OPENAI_API_NØGLE_HER
        openai.url=https://api.openai.com/v1/chat/completions
        openai.model=gpt-3.5-turbo # Eller en anden passende model
        openai.temperature=0.7
        openai.max_tokens=100
        ```
    *   **Erstat `DIN_TMDB_API_NØGLE_HER` og `DIN_OPENAI_API_NØGLE_HER` med dine faktiske nøgler.**

4.  **Byg og kør applikationen:**
    *   **Fra terminalen:**
        ```bash
        ./mvnw spring-boot:run
        ```
        (På Windows kan det være `.\mvnw.cmd spring-boot:run`)
    *   **Fra din IDE (f.eks. IntelliJ IDEA):**
        *   Åbn projektet.
        *   Find `MovieSearchApplication.java` og kør `main`-metoden.

5.  **Adgang:**
    *   Applikationen vil starte på `http://localhost:8080`.
    *   Frontend-applikationen forventer at finde backend på `http://localhost:8080/api/movies`.


## 💻 Teknologier

*   **Java 17:** Programmeringssprog.
*   **Spring Boot 3:** Framework for hurtig udvikling af webapplikationer og RESTful API'er.
*   **Maven:** Byggestyringsværktøj.
*   **Spring WebFlux (WebClient):** Ikke-blokerende HTTP-klient til eksterne API-kald (TMDB, OpenAI).
*   **Lombok:** Giver boilerplate-kode som getters, setters, constructors automatisk.
*   **The Movie Database (TMDB) API:** Kilde til filmdata.
*   **OpenAI API:** Leverer AI-drevne tekstgenereringsfunktioner.


## 📂 Projektstruktur (Backend)

MovieSearchAi-backend/

├── src/

│   ├── main/

│   │   ├── java/com/example/moviesearch/

│   │   │   ├── MovieSearchApplication.java # Hoved Spring Boot applikation

│   │   │   ├── Controller/               # REST API-endpoints

│   │   │   │   ├── AiTestController.java

│   │   │   │   └── MovieController.java

│   │   │   ├── Model/                    # Data Transfer Objects (DTOs)

│   │   │   │   ├── ChatCompletionRequest.java

│   │   │   │   ├── ChatCompletionResponse.java

│   │   │   │   ├── GenreDto.java

│   │   │   │   ├── MovieDto.java

│   │   │   │   └── MovieDtoWithAiRecommendation.java

│   │   │   └── Service/                  # Forretningslogik og API-kald

│   │   │       ├── AiService.java

│   │   │       └── MovieService.java

│   │   └── resources/

│   │       └── application.properties    # Applikationskonfiguration (inkl. API-nøgler)

│   └── test/                             # Test-relaterede filer

└── pom.xml                               # Maven konfiguration (afhængigheder etc.)

## 🤝 Bidrag

Forespørgsler og forslag er velkomne.


## 📝 Licens

MIT License

Copyright (c) [2025] [Frederik Rasmus Wendelboe Hansen]

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.


## ✉️ Kontakt
@frederikrasmus](https://github.com/frederikrasmus)
frederikrasmus@hotmail.dk
