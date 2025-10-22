package GamersCoveDev.mockdata;

import GamersCoveDev.domains.entities.ReviewEntity;
import java.util.List;

public class mockreview {

    public static final List<ReviewEntity> REVIEWS = List.of(
            new ReviewEntity(1L, 1L, 10, "An absolute masterpiece — haunting atmosphere and rewarding gameplay."),
            new ReviewEntity(2L, 1L, 9, "Stunning art direction and deep lore. A must-play."),
            new ReviewEntity(3L, 2L, 8, "Celeste is emotional and challenging — tight controls and a moving story."),
            new ReviewEntity(4L, 3L, 9, "Ori offers one of the most heartfelt adventures in gaming."),
            new ReviewEntity(5L, 4L, 7, "Dead Cells brings fast-paced roguelike action with great replayability.")
    );
}
