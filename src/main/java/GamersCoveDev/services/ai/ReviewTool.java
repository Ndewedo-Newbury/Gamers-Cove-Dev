package GamersCoveDev.services.ai;

import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.mockdata.mockgames;
import GamersCoveDev.mockdata.mockreview;
import GamersCoveDev.repositories.GameRepository;
import GamersCoveDev.repositories.ReviewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;
import org.apache.commons.text.similarity.LevenshteinDistance;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ReviewTool implements AgentTool {
    private final ReviewRepository reviewRepo ;
    private final GameRepository gamesRepo ;

  public  ReviewTool(ReviewRepository reviewRepo, GameRepository gamesRepo) {
      this.reviewRepo = reviewRepo;
      this.gamesRepo = gamesRepo;
  }




    @Tool("""
        Fetches the top 3 highest-rated reviews for a specific game title.
        Use ONLY when the user explicitly asks for reviews about a specific game.
        DO NOT use for quizzes, random games, or when the user hasn't specified a game title.
        Example valid uses:
        - 'Show me reviews for Hollow Knight'""")
    public String reviewTool(@UserMessage String gameTitle) {
        System.out.println("\n=== [REVIEW TOOL CALLED] ===");
        System.out.println("Requested Game: " + gameTitle);
        
        try {
            // Input validation
            if (gameTitle == null || gameTitle.isBlank() || gameTitle.equalsIgnoreCase("random game")) {
                return jsonError("Please specify which game's reviews you'd like to see. For example: 'Show me reviews for Hollow Knight'");
            }
            
            // Prevent use for quiz-related queries
            String lowerTitle = gameTitle.toLowerCase();
            if (lowerTitle.matches(".*(quiz|play|random|hint|guess).*")) {
                return jsonError("I can only show reviews for specific games. If you want to play a quiz, just say 'play a quiz'.");
            }

            // 1. Try to find the game in the database first
            GameEntity game = findGameInDatabase(gameTitle);
            
            // 2. If not found, try mock data
            if (game == null) {
                System.out.println("🔍 Game not found in database, checking mock data...");
                game = findGameInMockData(gameTitle);
                if (game == null) {
                    return jsonError("Game not found: " + gameTitle);
                }
            }
            
            // 3. Get reviews for the game
            List<ReviewEntity> reviews = findReviewsForGame(game);
            if (reviews.isEmpty()) {
                return jsonError("No reviews found for " + game.getTitle());
            }
            
            // 4. Format and return the response
            return formatReviewResponse(game, reviews);
            
        } catch (Exception e) {
            System.err.println("❌ Error in reviewTool: " + e.getMessage());
            e.printStackTrace();
            return jsonError("An error occurred while processing your request: " + e.getMessage());
        }
    }
    
    private GameEntity findGameInDatabase(String gameTitle) {
        try {
            // First try exact match
            GameEntity game = gamesRepo.findByTitleIgnoreCase(gameTitle).orElse(null);
            if (game != null) {
                System.out.println("✅ Found exact match in database: " + game.getTitle());
                return game;
            }
            
            // Then try partial match
            List<GameEntity> matches = gamesRepo.findByTitleContainingIgnoreCase(gameTitle);
            if (!matches.isEmpty()) {
                System.out.println("✅ Found partial match in database: " + matches.get(0).getTitle());
                return matches.get(0);
            }
            
            // Finally try fuzzy matching
            return findGameByFuzzyMatch(gameTitle);
            
        } catch (Exception e) {
            System.err.println("❌ Error querying database: " + e.getMessage());
            return null;
        }
    }
    
    private GameEntity findGameInMockData(String gameTitle) {
        return mockgames.GAMES.stream()
                .filter(g -> g.getTitle().equalsIgnoreCase(gameTitle))
                .findFirst()
                .orElse(null);
    }
    
    private GameEntity findGameByFuzzyMatch(String gameTitle) {
        System.out.println("🔍 Trying fuzzy matching for: " + gameTitle);
        LevenshteinDistance ld = new LevenshteinDistance();
        String normalizedTitle = gameTitle.toLowerCase();

        try {
            List<GameEntity> allGames = gamesRepo.findAll();
            if (!allGames.isEmpty()) {
                return allGames.stream()
                        .min(Comparator.comparingInt(
                                g -> ld.apply(g.getTitle().toLowerCase(), normalizedTitle)))
                        .orElse(null);
            }
        } catch (Exception e) {
            System.err.println("❌ Error in fuzzy matching: " + e.getMessage());
        }
        return null;
    }
    
    private List<ReviewEntity> findReviewsForGame(GameEntity game) {
        try {
            System.out.println("🔍 Looking for reviews for game: " + game.getTitle() + " (ID: " + game.getId() + ")");
            
            // First try database
            List<ReviewEntity> reviews = reviewRepo.findTop3ByGameIdOrderByRatingDesc(game.getId());
            System.out.println("🔍 Database query returned " + reviews.size() + " reviews");
            
            if (!reviews.isEmpty()) {
                System.out.println("✅ Found " + reviews.size() + " reviews in database");
                return reviews;
            }
            
            // Fall back to mock data
            System.out.println("🔍 Checking mock reviews for game ID: " + game.getId());
            System.out.println("🔍 Total mock reviews available: " + mockreview.REVIEWS.size());
            
            List<ReviewEntity> mockReviews = mockreview.REVIEWS.stream()
                    .peek(r -> System.out.println("  - Mock Review - Game ID: " + r.getGameId() + ", Rating: " + r.getRating() + ", Content: " + 
                        (r.getContent() != null ? r.getContent().substring(0, Math.min(30, r.getContent().length())) + "..." : "null")))
                    .filter(r -> {
                        boolean match = r.getGameId().equals(game.getId());
                        System.out.println("  - Checking if " + r.getGameId() + " matches " + game.getId() + ": " + match);
                        return match;
                    })
                    .limit(3)
                    .collect(Collectors.toList());
                    
            if (!mockReviews.isEmpty()) {
                System.out.println("✅ Found " + mockReviews.size() + " reviews in mock data");
            } else {
                System.out.println("❌ No matching reviews found in mock data");
            }
            
            return mockReviews;
            
        } catch (Exception e) {
            System.err.println("❌ Error finding reviews: " + e.getMessage());
            return Collections.emptyList();
        }
    }
    
    private String formatReviewResponse(GameEntity game, List<ReviewEntity> reviews) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> response = new HashMap<>();
            
            // Build reviews array with all required fields
            List<Map<String, Object>> formattedReviews = reviews.stream()
                    .map(r -> {
                        Map<String, Object> reviewMap = new HashMap<>();
                        reviewMap.put("id", r.getId());
                        reviewMap.put("userId", r.getUserId());
                        reviewMap.put("username", "User" + r.getUserId()); // Default username
                        reviewMap.put("gameId", r.getGameId());
                        reviewMap.put("gameTitle", game.getTitle()); // Add game title
                        reviewMap.put("rating", r.getRating());
                        reviewMap.put("content", r.getContent());
                        reviewMap.put("date", r.getCreatedAt() != null ? 
                            r.getCreatedAt().toString() : null);
                        return reviewMap;
                    })
                    .collect(Collectors.toList());
            
            // Build game info with all required fields
            Map<String, Object> gameInfo = new HashMap<>();
            gameInfo.put("id", game.getId());
            gameInfo.put("externalApiId", game.getExternalApiId());
            gameInfo.put("title", game.getTitle());
            gameInfo.put("description", game.getDescription());
            gameInfo.put("coverImageUrl", game.getCoverImageUrl());
            gameInfo.put("releaseDate", game.getReleaseDate() != null ? 
                game.getReleaseDate().toString() : null);
            gameInfo.put("platforms", game.getPlatforms());
            gameInfo.put("genres", game.getGenres());
            
            // Build final response with just the card content
            response.put("reply", " "); // Single space to maintain structure
            response.put("reviews", formattedReviews);
            
            // Only include game info if we have valid data
            if (game != null) {
                response.put("game", gameInfo);
            } else {
                response.put("game", null);
            }
            
            response.put("recommendations", Collections.emptyList());
            
            // Add quiz object with default values
            Map<String, Object> quiz = new HashMap<>();
            quiz.put("active", false);
            quiz.put("hintNumber", null);
            quiz.put("hint", null);
            quiz.put("remainingAttempts", null);
            response.put("quiz", quiz);
            
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response);
            
        } catch (Exception e) {
            System.err.println("❌ Error formatting response: " + e.getMessage());
            return jsonError("Error formatting reviews: " + e.getMessage());
        }
    }



    private String jsonError(String message) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writeValueAsString(Collections.singletonMap("error", message));
        } catch (Exception e) {
            // Fallback in case JSON serialization fails
            return "{\"error\":\"" + message.replace("\"", "\\\"") + "\"}";
        }
    }




    @Override
    public String getName() {
        return "Review Tool";
    }

    @Override
    public String getDescription() {
        return "Fetches top 3 reviews for the given game title. Uses fuzzy matching if no exact match is found.";
    }
}
