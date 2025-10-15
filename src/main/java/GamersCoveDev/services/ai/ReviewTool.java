package GamersCoveDev.services;
import org.apache.commons.text.similarity.LevenshteinDistance;
import GamersCoveDev.domain.entities.GameEntity;
import GamersCoveDev.domain.entities.ReviewEntity;
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




    @Tool("Returns A json format of the reviews Relevant to the user's query")
    public String reviewTool(@UserMessage String gameTitle) {
        var game = gamesRepo.findByTitleIgnoreCase(gameTitle).orElse(null);

        if (game == null) {
            LevenshteinDistance ld = new LevenshteinDistance();
            String normalizedUserTitle = gameTitle.toLowerCase();

            List<GameEntity> allGames = gamesRepo.findAll();
            // Sort all games by similarity (smallest distance = closest match)
            List<GameEntity>  closestGames = allGames.stream()
                    .sorted(Comparator.comparingInt(g -> ld.apply(g.getTitle().toLowerCase(), normalizedUserTitle)))
                    .limit(5) // get top 5 similar titles
                    .toList();


        if (closestGames.isEmpty()) {
            return "{\\\"message\\\": \\\"No matching games found.\\\"}";// no match found
        }

            game = closestGames.get(0);
        }

        List<ReviewEntity> review = reviewRepo.findTop3ByGameIdOrderByRatingDesc(game.getId());

        try{
            return new ObjectMapper().writeValueAsString(review);
        } catch (Exception e) {
           return "[]";
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
