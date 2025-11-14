package GamersCoveDev.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        // Spring Boot automatically serves index.html from static folder
        // Redirect to ensure proper serving of static content
        return "redirect:/index.html";
    }
}

