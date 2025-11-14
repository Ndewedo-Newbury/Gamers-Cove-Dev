package GamersCoveDev.services.ai;

import GamersCoveDev.repositories.GameRepository;
import GamersCoveDev.repositories.ReviewRepository;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.model.openai.OpenAiChatModel;
import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.stereotype.Service;

@Service
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
        return chatAgent.chat(message);
    }

    public static void main(String[] args) {
        System.out.println("Please run the application using Spring Boot");
    }
}
