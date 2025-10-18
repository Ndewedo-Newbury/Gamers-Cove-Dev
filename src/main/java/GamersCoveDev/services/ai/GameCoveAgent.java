package GamersCoveDev.services.ai;

import GamersCoveDev.repositories.MockGameRepository;
import GamersCoveDev.repositories.MockReviewRepository;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

import java.util.Scanner;
import java.util.spi.ToolProvider;

public class GameCoveAgent {
    public MockReviewRepository reviewRepo;
    private MockGameRepository gameRepo;
    private final AiAgent chatAgent;
       public GameCoveAgent(ReviewTool reviewTool,RecommendationTool recommendationTool) {

           var model = OpenAiChatModel.builder()
                   .apiKey(System.getenv("OPENAI_API_KEY"))
                   .modelName("gpt-4o-mini")
                   .temperature(0.7)
                   .build();

           this.chatAgent =AiServices.builder(AiAgent.class)
                   .chatLanguageModel(model)
                   .tools(reviewTool,recommendationTool)
                   .build();
       }
       public String chat(String message) {
           return chatAgent.chat(message);
       }

       public static void main(String[] args) {
           MockReviewRepository reviewRepo = new MockReviewRepository();
           MockGameRepository gamesRepo = new MockGameRepository();
           // 🔐 Make sure your key is set: echo %OPENAI_API_KEY% (Windows) or echo $OPENAI_API_KEY (Mac/Linux)
           if (System.getenv("OPENAI_API_KEY") == null) {
               System.err.println("❌ OPENAI_API_KEY not set!");
               return;
           }

           // 🧩 Create mock tools (replace with real beans or fakes if needed)
           ReviewTool reviewTool = new ReviewTool(  reviewRepo, gamesRepo); // you can pass mocks here for isolated testing
           RecommendationTool recommendationTool = new RecommendationTool(gamesRepo);

           // 🧠 Create the chat agent
           GameCoveAgent agent = new GameCoveAgent(reviewTool, recommendationTool);

           // 💬 Interactive console test
           Scanner scanner = new Scanner(System.in);
           System.out.println("🎮 GamersCove AI — type a message (type 'exit' to quit):");

           while (true) {
               System.out.print("You: ");
               String input = scanner.nextLine();

               if (input.equalsIgnoreCase("exit")) break;

               try {
                   String response = agent.chat(input);
                   System.out.println("🧠 AI: " + response);
               } catch (Exception e) {
                   System.err.println("Error: " + e.getMessage());
               }
           }

           scanner.close();
           System.out.println("👋 Goodbye!");
           }


}
