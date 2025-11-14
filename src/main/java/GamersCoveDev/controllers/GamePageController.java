package GamersCoveDev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class GamePageController {

    @GetMapping("/game/{gameId}")
    public String gamePage(@PathVariable("gameId") Long gameId) {
        // Forward to static HTML file - the JavaScript will extract gameId from URL
        return "forward:/game-detail.html";
    }
}

