package GamersCoveDev.services.ai;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.mockdata.mockgames;
import GamersCoveDev.repositories.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

@Service
public class RandomGameTool implements AgentTool {

    private final GameRepository gamesRepo;
    private final ObjectMapper mapper = new ObjectMapper();
    private final Random random = new Random();

    public RandomGameTool(GameRepository gamesRepo) {
        this.gamesRepo = gamesRepo;
    }

    @Tool({
        "Selects a random game from the database to start a quiz.",
        "Use this when the user wants to play a QUIZ or says things like:",
        "- 'quiz me'",
        "- 'let's play a quiz'",
        "- 'guess the game'",
        "- 'random game challenge'",
        "- 'start a quiz'"
    })
    public String randomGame() {
        System.out.println("🎮 [QUIZ MODE] RandomGameTool.randomGame() - Starting new quiz");

        try {
            // First try to get games from the database
            System.out.println("🔍 Attempting to fetch games from database...");
            List<GameEntity> games = gamesRepo.findAll();
            System.out.println("✅ Fetched " + (games != null ? games.size() : 0) + " games from database");
            
            // If no games in database, use mock data
            if (games == null || games.isEmpty()) {
                System.out.println("ℹ️ No games in database, using mock data");
                try {
                    games = new ArrayList<>(mockgames.GAMES);
                    System.out.println("✅ Loaded " + games.size() + " mock games");
                } catch (Exception e) {
                    System.err.println("❌ Error loading mock games: " + e.getMessage());
                    e.printStackTrace();
                    return createQuizErrorResponse("Error loading game data: " + e.getMessage());
                }
            }

            // Ensure we have games to pick from
            if (games == null || games.isEmpty()) {
                System.err.println("❌ No games available for quiz (games list is null or empty)");
                return createQuizErrorResponse("No games available for quiz");
            }

            System.out.println("🎲 Selecting random game from " + games.size() + " available games");
            GameEntity picked = games.get(random.nextInt(games.size()));
            System.out.println("🎯 Selected game: " + (picked != null ? picked.getTitle() : "null"));
            
            // Ensure name is set (should match title)
            if (picked != null) {
                if (picked.getName() == null && picked.getTitle() != null) {
                    System.out.println("ℹ️ Setting name to match title: " + picked.getTitle());
                    picked.setName(picked.getTitle());
                }
                System.out.println("📝 Game details - Name: " + picked.getName() + 
                                 ", Title: " + picked.getTitle() + 
                                 ", ID: " + picked.getId());
            } else {
                return createQuizErrorResponse("Failed to select a game for the quiz");
            }

            // Create the quiz response with the first hint
            return createQuizResponse("🎮 Let's play! I'm thinking of a game.\n\n" +
                                   "Type 'hint' for a clue or 'give up' to see the answer.\n" +
                                   "You have 5 attempts. Good luck!", 
                                   picked);
        } catch (Exception e) {
            System.err.println("❌ Unexpected error in randomGame: " + e.getMessage());
            e.printStackTrace();
            return createQuizErrorResponse("An unexpected error occurred: " + e.getMessage());
        }
    }

    private String getHint(GameEntity game, int hintNumber) {
        String[] hints = new String[5];
        
        // Hint 1: First letters of each word in the title
        String[] words = game.getTitle().split("\\s+");
        StringBuilder firstLetters = new StringBuilder();
        for (String word : words) {
            if (!word.isEmpty()) {
                firstLetters.append(word.charAt(0)).append(".");
            }
        }
        hints[0] = String.format("The game's title is abbreviated as: %s", firstLetters.toString());
        
        // Hint 2: Genre information
        String[] genres = game.getGenres();
        String genreHint = genres.length > 0 ? 
            String.join(" or ", genres) + " game" : 
            "a game of an unspecified genre";
        hints[1] = String.format("It's %s.", genreHint);
        
        // Hint 3: Release year
        if (game.getReleaseDate() != null) {
            int year = game.getReleaseDate().getYear();
            hints[2] = String.format("It was released in the year %d.", year);
        } else {
            hints[2] = "The release date is not specified.";
        }
        
        // Hint 4: Available platforms
        String[] platforms = game.getPlatforms();
        if (platforms.length > 0) {
            hints[3] = String.format("You can play it on: %s", String.join(", ", platforms));
        } else {
            hints[3] = "Platform information is not available for this game.";
        }
        
        // Hint 5: Description snippet
        String description = game.getDescription();
        if (description != null && !description.isEmpty()) {
            int snippetLength = Math.min(150, description.length());
            String snippet = description.substring(0, snippetLength);
            if (description.length() > snippetLength) {
                snippet += "...";
            }
            hints[4] = "Description: " + snippet;
        } else {
            hints[4] = "No description available for this game.";
        }
        
        // Return the appropriate hint based on hintNumber (1-5)
        int hintIndex = Math.min(hintNumber - 1, hints.length - 1);
        return hints[hintIndex];
    }
    
    private String createQuizResponse(String message, GameEntity game) {
        try {
            // Create a map to represent the response structure
            Map<String, Object> response = new HashMap<>();
            
            // Generate the initial hint
            String firstHint = getHint(game, 1);
            String reply = String.format("🎮 Let's play! I'm thinking of a game.\n\n" +
                "💡 Hint #1: %s\n\n" +
                "You have 5 attempts. Type your guess!", firstHint);
                
            response.put("reply", reply);
            
            // Add game details (hidden from user, used internally)
            Map<String, Object> gameMap = new HashMap<>();
            gameMap.put("id", game.getId());
            gameMap.put("title", game.getTitle());
            gameMap.put("description", game.getDescription());
            gameMap.put("coverImageUrl", game.getCoverImageUrl());
            gameMap.put("releaseDate", game.getReleaseDate() != null ? game.getReleaseDate().toString() : null);
            gameMap.put("platforms", game.getPlatforms());
            gameMap.put("genres", game.getGenres());
            response.put("game", gameMap);
            
            // Add quiz state
            Map<String, Object> quizMap = new HashMap<>();
            quizMap.put("active", true);
            quizMap.put("hintNumber", 1);
            quizMap.put("hint", firstHint);
            quizMap.put("remainingAttempts", 5);
            quizMap.putAll(gameMap); // Include all game details in quiz state
            response.put("quiz", quizMap);
            
            // Empty arrays for other fields
            response.put("reviews", new ArrayList<>());
            response.put("recommendations", new ArrayList<>());
            
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            System.err.println("❌ Error creating quiz response: " + e.getMessage());
            return createQuizErrorResponse("Error creating quiz: " + e.getMessage());
        }
    }
    
    private String createQuizErrorResponse(String message) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("reply", "It seems there was an issue starting the quiz. " + message);
            response.put("game", null);
            response.put("reviews", new ArrayList<>());
            response.put("recommendations", new ArrayList<>());
            
            Map<String, Object> quizMap = new HashMap<>();
            quizMap.put("active", false);
            quizMap.put("hintNumber", null);
            quizMap.put("hint", null);
            quizMap.put("remainingAttempts", null);
            response.put("quiz", quizMap);
            
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            return "{\"reply\":\"A critical error occurred while creating the error response.\",\"game\":null,\"reviews\":[],\"recommendations\":[],\"quiz\":{\"active\":false,\"hintNumber\":null,\"hint\":null,\"remainingAttempts\":null}}";
        }
    }

    @Override
    public String getName() {
        return "Random Game Tool";
    }

    @Override
    public String getDescription() {
        return "Selects a random game from the database and returns it as JSON, useful for quizzes or surprise recommendations.";
    }
}
