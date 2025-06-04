package com.sportradar.interview.footballscoreboard.domain;

import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.Optional;

public interface MatchRepository {

    /**
     * @param match The match to save.
     * @return The saved or updated Match.
     */
    Match save(@NotNull Match match);

    /**
     * @param homeTeam The home team name.
     * @param awayTeam The away team name.
     * @return true if deleted, false otherwise.
     */
    boolean delete(@NotNull String homeTeam, @NotNull String awayTeam);

    /**
     * @param team1 One team name.
     * @param team2 The other team name.
     * @return Optional containing the Match, or empty.
     */
    Optional<Match> findByTeams(@NotNull String team1, @NotNull String team2);

    /**
     * Retrieves all matches from the repository.
     *
     * @return A list of all matches.
     */
    List<Match> findAll();
}