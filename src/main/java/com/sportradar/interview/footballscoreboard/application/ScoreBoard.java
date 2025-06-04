package com.sportradar.interview.footballscoreboard.application;

import com.sportradar.interview.footballscoreboard.domain.Match;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public interface ScoreBoard {

    /**
     * @param homeTeam Home team name.
     * @param awayTeam Away team name.
     * @return The newly started Match.
     * @throws IllegalArgumentException if game exists or names are invalid.
     */
    Match startGame(@NotNull String homeTeam, @NotNull String awayTeam);

    /**
     * @param homeTeam Home team name.
     * @param awayTeam Away team name.
     * @throws IllegalArgumentException if game not found.
     */
    void finishGame(@NotNull String homeTeam, @NotNull String awayTeam);

    /**
     * @param homeTeam  Home team name.
     * @param awayTeam  Away team name.
     * @param homeScore New home score.
     * @param awayScore New away score.
     * @return The updated Match.
     * @throws IllegalArgumentException if game not found or scores negative.
     */
    Match updateScore(@NotNull String homeTeam, @NotNull String awayTeam, int homeScore, int awayScore);

    /**
     * @return Unmodifiable list of Match objects.
     */
    List<Match> getSummary();

    /**
     * @return Unmodifiable list of ongoing matches.
     */
    List<Match> getCurrentMatches();
}
