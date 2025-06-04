package com.sportradar.interview.footballscoreboard.domain;

import java.time.Instant;
import java.util.Objects;


public class Match {

    private final Instant startTime;
    private final String homeTeam;
    private final String awayTeam;
    private int homeScore;
    private int awayScore;

    public Match(String homeTeam, String awayTeam) {
        if (homeTeam == null || homeTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Home team name can not be null or empty.");
        }
        if (awayTeam == null || awayTeam.trim().isEmpty()) {
            throw new IllegalArgumentException("Away team name can not be null or empty.");
        }
        if (homeTeam.equalsIgnoreCase(awayTeam)) {
            throw new IllegalArgumentException("Home team and away team can not be the same.");
        }

        this.startTime = Instant.now();
        this.homeTeam = homeTeam.trim();
        this.awayTeam = awayTeam.trim();
        this.homeScore = 0;
        this.awayScore = 0;
    }

    public String getHomeTeam() {
        return homeTeam;
    }

    public String getAwayTeam() {
        return awayTeam;
    }

    public int getHomeScore() {
        return homeScore;
    }

    public int getAwayScore() {
        return awayScore;
    }

    public Instant getStartTime() {
        return startTime;
    }

    public void updateScore(int newHomeScore, int newAwayScore) {
        if (newHomeScore < 0 || newAwayScore < 0) {
            throw new IllegalArgumentException("Scores cannot be negative.");
        }
        this.homeScore = newHomeScore;
        this.awayScore = newAwayScore;
    }

    public int getTotalScore() {
        return homeScore + awayScore;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Match match = (Match) o;
        return (homeTeam.equalsIgnoreCase(match.homeTeam) && awayTeam.equalsIgnoreCase(match.awayTeam)) ||
                (homeTeam.equalsIgnoreCase(match.awayTeam) && awayTeam.equalsIgnoreCase(match.homeTeam));
    }

    @Override
    public int hashCode() {
        String team1 = homeTeam.toLowerCase();
        String team2 = awayTeam.toLowerCase();
        if (team1.compareTo(team2) > 0) {
            String temp = team1;
            team1 = team2;
            team2 = temp;
        }
        return Objects.hash(team1, team2);
    }

    @Override
    public String toString() {
        return String.format("%s %d - %s %d", homeTeam, homeScore, awayTeam, awayScore);
    }
}