package GamersCoveDev.controllers;

import GamersCoveDev.services.ai.GameCoveAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;

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
        System.out.println("\n=== [CHAT REQUEST] ===");
        System.out.println("Raw request: " + request);
        
        String userMessage = request.get("message");
        System.out.println("User message: " + userMessage);
        
        if (userMessage == null || userMessage.trim().isEmpty()) {
            System.out.println("Empty message received");
            return ResponseEntity.badRequest()
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"reply\":\"Please provide a message\"}");
        }

        try {
            System.out.println("Passing to GameCoveAgent...");
            String aiResponse = gameCoveAgent.chat(userMessage);
            System.out.println("Raw AI response: " + aiResponse);
            
            // Ensure the response is valid JSON
            try {
                // This will throw an exception if the response is not valid JSON
                ObjectMapper objectMapper = new ObjectMapper();
                objectMapper.readTree(aiResponse);
                
                System.out.println("============================\n");
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(aiResponse);
            } catch (Exception e) {
                // If not valid JSON, wrap it
                System.out.println("Wrapping non-JSON response");
                String wrappedResponse = "{\"reply\":\"" + 
                    aiResponse.replace("\"", "\\\"") + "\"}";
                    
                System.out.println("Wrapped response: " + wrappedResponse);
                return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(wrappedResponse);
            }
        } catch (Exception e) {
            System.err.println("Error in chat controller: " + e.getMessage());
            e.printStackTrace();
            
            String errorResponse = "{\"reply\":\"Sorry, I encountered an error: " + 
                e.getMessage().replace("\"", "'") + "\"}";
                
            return ResponseEntity.internalServerError()
                .contentType(MediaType.APPLICATION_JSON)
                .body(errorResponse);
        }
    }
}
