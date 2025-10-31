package GamersCoveDev.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import java.io.File;
import java.util.List;
import java.util.Map;

public class PostGamesToDb {
    private static final String API_URL = "http://localhost:8080/api/games";
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final RestTemplate restTemplate = new RestTemplate();

    public static void main(String[] args) {
        try {
            // Read the JSON file
            File file = new File("games_dto.json");
            List<Map<String, Object>> games = objectMapper.readValue(file, new TypeReference<>() {});
            
            System.out.println("Found " + games.size() + " games to import");
            
            // Ask for confirmation
            System.out.println("\nWARNING: This will post " + games.size() + " games to " + API_URL);
            System.out.print("Do you want to continue? (yes/no): ");
            
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            String confirmation = scanner.nextLine().trim().toLowerCase();
            
            if (!confirmation.equals("yes")) {
                System.out.println("Operation cancelled by user.");
                return;
            }
            
            // Set up headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            int successCount = 0;
            
            // Post each game to the API
            for (Map<String, Object> game : games) {
                try {
                    HttpEntity<Map<String, Object>> request = new HttpEntity<>(game, headers);
                    ResponseEntity<String> response = restTemplate.postForEntity(API_URL, request, String.class);
                    
                    if (response.getStatusCode().is2xxSuccessful()) {
                        System.out.println("Successfully added game: " + game.get("title"));
                        successCount++;
                    } else {
                        System.err.println("Failed to add game " + game.get("title") + ": " + response.getStatusCode());
                    }
                } catch (Exception e) {
                    System.err.println("Error adding game " + game.get("title") + ": " + e.getMessage());
                }
                
                // Small delay to avoid overwhelming the server
                Thread.sleep(100);
            }
            
            System.out.println("\nImport completed. Successfully imported " + successCount + " out of " + games.size() + " games.");
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
