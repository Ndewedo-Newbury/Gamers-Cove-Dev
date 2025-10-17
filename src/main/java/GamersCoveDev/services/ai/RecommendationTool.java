package GamersCoveDev.services.ai;
import GamersCoveDev.domains.entities.GameEntity;
import GamersCoveDev.repositories.GameRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.service.UserMessage;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationTool implements AgentTool {
    private final GameRepository gamesRepo;
    private final ObjectMapper mapper = new ObjectMapper();

    public RecommendationTool(GameRepository gamesRepo) {
        this.gamesRepo = gamesRepo;
    }


    @Tool("Fetches and returns detailed info for up to 4 games (the main game and three similar ones).")
    public String recommendGames(@UserMessage String gameTitle, String similar1
            , String similar2, String similar3) {
        var game = gamesRepo.findByTitleIgnoreCase(gameTitle).orElse(null);
        List<String> titles = List.of(gameTitle, similar2, similar3);
        List<GameEntity> closestGames = new ArrayList<>();
        for (String title : titles) {
            if (title == null || title.isEmpty()) continue;
            gamesRepo.findByTitleIgnoreCase(title).ifPresent(closestGames::add);
        }
        if (closestGames.isEmpty()) {
            return "{\\\"message\\\": \\\"No matching games found.\\\"}";
        }

        try {
            return mapper.writeValueAsString(closestGames);
        } catch (Exception e) {
            return "[]";
        }



    }
    @Override
    public String getName () {
        return "Recommendation Tool";
    }

    @Override
    public String getDescription () {
        return "Fetches database details for an initial game and three related games suggested by the AI.";
    }
}
