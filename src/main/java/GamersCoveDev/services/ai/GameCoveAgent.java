package GamersCoveDev.services.ai;

import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

import java.util.spi.ToolProvider;

public class GameCoveAgent {

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

}
