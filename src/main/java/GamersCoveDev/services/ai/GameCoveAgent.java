package GamersCoveDev.services.ai;

import GamersCoveDev.repositories.GameRepository;
import GamersCoveDev.repositories.ReviewRepository;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

@Service
@Slf4j
public class GameCoveAgent {

    private final AiAssistant chatAgent;
    private final MessageWindowChatMemory chatMemory;

    public GameCoveAgent(GameRepository gameRepository, ReviewRepository reviewRepository) {
        // Initialize with empty chat memory
        this.chatMemory = MessageWindowChatMemory.withMaxMessages(20);
        
        // 🔹 Load .env from project root
        Dotenv dotenv = Dotenv.configure()
                .directory(".")        // look in working directory
                .filename(".env")      // load .env file
                .ignoreIfMalformed()
                .ignoreIfMissing()
                .load();

        String apiKey = dotenv.get("OPENAI_API_KEY");

        if (apiKey == null || apiKey.isBlank()) {
            throw new RuntimeException("❌ OPENAI_API_KEY missing in .env!");
        }

        // Debug without exposing whole key
        System.out.println("Loaded OPENAI_API_KEY = " + apiKey.substring(0, 12) + "...");

        var chatModel = OpenAiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gpt-4o-mini")
                .temperature(0.7)
                .build();

        // Initialize tools with repositories
        ReviewTool reviewTool = new ReviewTool(reviewRepository, gameRepository);
        RecommendationTool recommendationTool = new RecommendationTool(gameRepository);
        RandomGameTool randomGameTool = new RandomGameTool(gameRepository);
        
        this.chatAgent = AiServices.builder(AiAssistant.class)
                .chatLanguageModel(chatModel)
                .chatMemory(chatMemory)
                .tools(reviewTool, recommendationTool, randomGameTool)
                .build();
    }

    public String chat(String message) {
        try {
            String response = chatAgent.chat(message);
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, Object> responseMap = new HashMap<>();
            
            // Add the reply
            responseMap.put("reply", response);
            
            // Check if we should include game cards
            if (message.toLowerCase().contains("show me") || 
                message.toLowerCase().contains("recommend") || 
                message.toLowerCase().contains("game") ||
                message.toLowerCase().contains("hollow") ||
                message.toLowerCase().contains("knight") ||
                message.toLowerCase().contains("review")) {
                
                // Add sample game data (replace with actual game data from your database)
                List<Map<String, Object>> recommendations = new ArrayList<>();
                
                // Add sample game 1
                Map<String, Object> game1 = new HashMap<>();
                game1.put("title", "Hollow Knight");
                game1.put("coverImageUrl", "https://images.igdb.com/igdb/image/upload/t_cover_big/co1r0e.jpg");
                game1.put("genres", Arrays.asList("Metroidvania", "Action", "Adventure"));
                recommendations.add(game1);
                
                // Add sample game 2
                Map<String, Object> game2 = new HashMap<>();
                game2.put("title", "Celeste");
                game2.put("coverImageUrl", "https://images.igdb.com/igdb/image/upload/t_cover_big/co1r7j.jpg");
                game2.put("genres", Arrays.asList("Platformer", "Indie", "Adventure"));
                recommendations.add(game2);
                
                responseMap.put("recommendations", recommendations);
            }
            
            return objectMapper.writeValueAsString(responseMap);
            
        } catch (Exception e) {
            log.error("Error in chat: {}", e.getMessage(), e);
            return "{\"reply\":\"Sorry, I encountered an error: " + e.getMessage().replace("\"", "'") + "\"}";
        }
    }

    public static void main(String[] args) {
        System.out.println("Please run the application using Spring Boot");
    }
}
