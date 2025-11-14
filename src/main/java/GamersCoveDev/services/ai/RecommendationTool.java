package GamersCoveDev.services.ai;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.repositories.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RecommendationTool implements AgentTool {
    private final GameRepository gamesRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public RecommendationTool(GameRepository gamesRepo) {
        this.gamesRepo = gamesRepo;
    }


    @Tool({
        "Recommends games similar to the specified game by title.",
        "The AI should provide the main game title and up to 3 similar game titles as parameters.",
        "Example: recommendGames(\"Hollow Knight\", \"Ori and the Blind Forest\", \"Dead Cells\", \"Blasphemous\")"
    })
    public String recommendGames(
            @UserMessage String gameTitle,
            String similar1,
            String similar2,
            String similar3) {

        System.out.println("[TOOL CALLED] RecommendationTool for: " + gameTitle);

        // Try to find the main game first - try exact match first, then case-insensitive
        var mainGame = gamesRepo.findByTitle(gameTitle)
                .or(() -> gamesRepo.findByTitleIgnoreCase(gameTitle))
                .orElse(null);
                
        if (mainGame == null) {
            System.out.println("Main game not found: " + gameTitle);
            // Instead of failing, return a helpful response with some general recommendations
            return createFallbackResponse(gameTitle);
        }

        // Collect all similar games
        List<GameEntity> recommendedGames = new ArrayList<>();

        // Add similar games if they exist
        for (String title : new String[]{similar1, similar2, similar3}) {
            if (title != null && !title.trim().isEmpty()) {
                gamesRepo.findByTitleIgnoreCase(title.trim())
                        .ifPresent(recommendedGames::add);
            }
        }

        // If no similar games found, return an empty array for recommendations
        if (recommendedGames.isEmpty()) {
            System.out.println("No similar games found for: " + gameTitle);
            return createSuccessResponse(gameTitle, new ArrayList<>());
        }

        try {
            return createSuccessResponse(gameTitle, recommendedGames);
        } catch (Exception e) {
            System.err.println("Error creating recommendation response: " + e.getMessage());
            return createErrorResponse("Error generating recommendations");
        }
    }

    private String createSuccessResponse(String gameTitle, List<GameEntity> recommendedGames) {
        try {
            Map<String, Object> response = new HashMap<>();

            // Set the main reply
            if (recommendedGames.isEmpty()) {
                response.put("reply", "I couldn't find any similar games to " + gameTitle + ".");
            } else {
                response.put("reply", "Here are some games similar to " + gameTitle + ":");
            }

            // Set the main game (if found)
            response.put("game", null); // Main game is already in the reply

            // Convert recommended games to the expected format
            List<Map<String, Object>> recommendations = recommendedGames.stream()
                    .map(game -> {
                        Map<String, Object> gameMap = new HashMap<>();
                        gameMap.put("id", game.getId());
                        gameMap.put("title", game.getTitle());
                        gameMap.put("coverImageUrl", game.getCoverImageUrl());
                        gameMap.put("genres", game.getGenres() != null ? game.getGenres() : new String[]{});
                        gameMap.put("rating", "N/A"); // Default rating if not available
                        return gameMap;
                    })
                    .collect(Collectors.toList());

            response.put("recommendations", recommendations);
            response.put("reviews", new ArrayList<>());
            response.put("quiz", createEmptyQuizObject());

            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            throw new RuntimeException("Error creating success response", e);
        }
    }

    private Map<String, Object> createEmptyQuizObject() {
        Map<String, Object> quiz = new HashMap<>();
        quiz.put("active", false);
        quiz.put("hintNumber", null);
        quiz.put("hint", null);
        quiz.put("remainingAttempts", null);
        return quiz;
    }

    private String createErrorResponse(String error) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("reply", "I couldn't generate recommendations right now. " + error);
            response.put("game", null);
            response.put("reviews", new ArrayList<>());
            response.put("recommendations", new ArrayList<>());
            response.put("quiz", createEmptyQuizObject());
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            return "{\"reply\":\"Error generating recommendations\"}";
        }
    }

    private String createFallbackResponse(String gameTitle) {
        try {
            Map<String, Object> response = new HashMap<>();
            response.put("reply", String.format("I couldn't find information about \"%s\", but here are some popular games you might like:", gameTitle));
            
            // Create a list of popular games as fallback
            List<Map<String, Object>> recommendations = new ArrayList<>();
            
            // Add some popular games as fallback
            String[] popularGames = {
                "The Legend of Zelda: Breath of the Wild",
                "The Witcher 3: Wild Hunt",
                "Red Dead Redemption 2",
                "God of War (2018)",
                "Elden Ring"
            };
            
            for (String title : popularGames) {
                Map<String, Object> gameMap = new HashMap<>();
                gameMap.put("title", title);
                gameMap.put("id", "");
                gameMap.put("coverImageUrl", "");
                gameMap.put("genres", new String[]{ "Action", "Adventure" });
                gameMap.put("rating", "N/A");
                recommendations.add(gameMap);
            }
            
            response.put("recommendations", recommendations);
            response.put("quiz", createEmptyQuizObject());
            
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
        } catch (Exception e) {
            return "{\"reply\":\"Here are some great games you might enjoy.\"}";
        }
    }

    @Override
    public String getName() {
        return "Game Recommendation Tool";
    }

    @Override
    public String getDescription() {
        return "Finds and returns information about games similar to a given game title.";
    }
}
