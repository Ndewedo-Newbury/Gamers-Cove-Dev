package GamersCoveDev.mockdata;

import GamersCoveDev.domains.entities.ReviewEntity;
import java.util.List;

public class mockreview {

    public static final List<ReviewEntity> REVIEWS = List.of(
            // Hollow Knight reviews (gameId = 1)
            new ReviewEntity(1L, 1L, 10, "An absolute masterpiece — haunting atmosphere and rewarding gameplay."),
            new ReviewEntity(2L, 1L, 9, "Stunning art direction and deep lore. A must-play."),
            
            // Celeste reviews (gameId = 2)
            new ReviewEntity(3L, 2L, 8, "Celeste is emotional and challenging — tight controls and a moving story."),
            
            // Ori and the Blind Forest reviews (gameId = 3)
            new ReviewEntity(4L, 3L, 9, "Ori offers one of the most heartfelt adventures in gaming."),
            
            // Dead Cells reviews (gameId = 4)
            new ReviewEntity(5L, 4L, 7, "Dead Cells brings fast-paced roguelike action with great replayability."),
            
            // Hollow Knight reviews (gameId = 5)
            new ReviewEntity(6L, 5L, 10, "Hollow Knight is a masterpiece of the Metroidvania genre with beautiful hand-drawn art and tight controls."),
            new ReviewEntity(7L, 5L, 9, "The atmosphere and world-building in Hollow Knight are incredible. The combat is challenging but fair."),
            new ReviewEntity(8L, 5L, 10, "One of the best indie games ever made. The attention to detail in every aspect of the game is astounding.")
    );
}
