package GamersCoveDev.services.ai;
import GamersCoveDev.mockdata.mockgames;
import org.apache.commons.text.similarity.LevenshteinDistance;
import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.domains.entities.ReviewEntity;
import GamersCoveDev.repositories.GameRepository;
import GamersCoveDev.repositories.ReviewRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ReviewTool implements AgentTool {
    private final ReviewRepository reviewRepo ;
    private final GameRepository gamesRepo ;

  public  ReviewTool(ReviewRepository reviewRepo, GameRepository gamesRepo) {
      this.reviewRepo = reviewRepo;
      this.gamesRepo = gamesRepo;
  }




    @Tool("Fetches the top 3 highest-rated reviews for the given game title. Uses fuzzy matching if no exact match is found.")
    public String reviewTool(@UserMessage String gameTitle) {
        System.out.println("[TOOL CALLED] ReviewTool for: " + gameTitle);

        GameEntity game = gamesRepo.findByTitleIgnoreCase(gameTitle).orElse(null);

        if (game == null) {
            LevenshteinDistance ld = new LevenshteinDistance();
            String normalizedTitle = gameTitle.toLowerCase();

            List<GameEntity> closestGames = gamesRepo.findAll().stream()
                    .sorted(Comparator.comparingInt(
                            g -> ld.apply(g.getTitle().toLowerCase(), normalizedTitle)))
                    .limit(1)
                    .toList();

            if (closestGames.isEmpty()) {
                return jsonError("No matching games found for title: " + gameTitle);
            }

            // ✅ Re-fetch using ID to ensure managed entity
            Long matchedId = closestGames.get(0).getId();
            game = gamesRepo.findById(matchedId).orElse(null);
            game = gamesRepo.findByTitleIgnoreCase(gameTitle).orElse(null);
            if (game == null) {
                game = mockgames.GAMES.stream()
                        .filter(g -> g.getTitle().equalsIgnoreCase(gameTitle))
                        .findFirst()
                        .orElse(null);
            }
            System.out.println("🔍 Using closest match: " + game.getTitle() + " (id=" + matchedId + ")");
        }

        if (game == null) {
            return jsonError("Unable to retrieve matching game from database.");
        }

        List<ReviewEntity> reviews = reviewRepo.findTop3ByGameIdOrderByRatingDesc(game.getId());

        if (reviews.isEmpty()) {
            return jsonError("No reviews found for game: " + game.getTitle());
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(reviews);
        } catch (Exception e) {
            return jsonError("Error serializing reviews: " + e.getMessage());
        }
    }



private String jsonError(String message) {
    return String.format("{\"error\": \"%s\"}", message.replace("\"", "'"));
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
