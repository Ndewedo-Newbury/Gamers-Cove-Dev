package GamersCoveDev.mockdata;

import GamersCoveDev.domains.entities.GameEntity;

import java.time.LocalDate;
import java.util.List;

public class mockgames {

    public static final List<GameEntity> GAMES = List.of(
            new GameEntity(
                    1L,
                    "API-001",
                    "Hollow Knight",
                    "A challenging 2D action-adventure through a vast, ruined kingdom of insects.",
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co1r9j.jpg",
                    LocalDate.of(2017, 2, 24),
                    new String[]{"PC", "Switch", "PS4", "Xbox"},
                    new String[]{"Metroidvania", "Action", "Platformer"}
            ),
            new GameEntity(
                    2L,
                    "API-002",
                    "Celeste",
                    "A platforming masterpiece about climbing a mountain and overcoming anxiety.",
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co2t4g.jpg",
                    LocalDate.of(2018, 1, 25),
                    new String[]{"PC", "Switch", "PS4", "Xbox"},
                    new String[]{"Platformer", "Indie", "Adventure"}
            ),
            new GameEntity(
                    3L,
                    "API-003",
                    "Ori and the Blind Forest",
                    "An emotional journey through a beautiful forest filled with secrets and challenges.",
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co1qv7.jpg",
                    LocalDate.of(2015, 3, 11),
                    new String[]{"PC", "Switch", "Xbox"},
                    new String[]{"Adventure", "Platformer", "Metroidvania"}
            ),
            new GameEntity(
                    4L,
                    "API-004",
                    "Dead Cells",
                    "A roguelike, Metroidvania-inspired action-platformer where you explore an ever-changing castle.",
                    "https://images.igdb.com/igdb/image/upload/t_cover_big/co2ox1.jpg",
                    LocalDate.of(2018, 8, 7),
                    new String[]{"PC", "Switch", "PS4", "Xbox"},
                    new String[]{"Roguelike", "Action", "Platformer"}
            )
    );
}
