package GamersCoveDev.controllers;

import GamersCoveDev.services.ai.GameCoveAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class AIChatController {

    private final GameCoveAgent gameCoveAgent;

    @Autowired
    public AIChatController(GameCoveAgent gameCoveAgent) {
        this.gameCoveAgent = gameCoveAgent;
    }

    @PostMapping("/chat")
    public ResponseEntity<String> chat(@RequestBody Map<String, String> request) {
        System.out.println("\n=== [CHAT REQUEST RECEIVED] ===");
        System.out.println("Raw request: " + request);
        
        String userMessage = request.get("message");
        System.out.println("User message: " + userMessage);
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            System.out.println("Empty message received");
            return ResponseEntity.badRequest().body("{\"reply\":\"Please provide a message\"}");
        }

        try {
            System.out.println("Passing to GameCoveAgent...");
            String aiResponse = gameCoveAgent.chat(userMessage);
            System.out.println("Raw AI response: " + aiResponse);
            
            // Return the raw JSON string directly
            System.out.println("============================\n");
            return ResponseEntity.ok(aiResponse);
        } catch (Exception e) {
            String errorResponse = String.format("{\"reply\":\"Sorry, I encountered an error: %s\"}", 
                e.getMessage().replace("\"", "'"));
            return ResponseEntity.internalServerError()
                    .body(errorResponse);
        }
    }
}
