package com.sportradar.interview.footballscoreboard.infrastructure;

import com.sportradar.interview.footballscoreboard.domain.Match;
import com.sportradar.interview.footballscoreboard.domain.MatchRepository;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;


public class InMemoryMatchRepository implements MatchRepository {

    private final List<Match> matches;

    public InMemoryMatchRepository() {
        this.matches = new CopyOnWriteArrayList<>();
    }

    @Override
    public Match save(Match match) {
        Optional<Match> existingMatch = findByTeams(match.getHomeTeam(), match.getAwayTeam());
        if (existingMatch.isPresent()) {
            Match foundMatch = existingMatch.get();
            foundMatch.updateScore(match.getHomeScore(), match.getAwayScore());
            return foundMatch;
        } else {
            matches.add(match);
            return match;
        }
    }

    @Override
    public boolean delete(String homeTeam, String awayTeam) {
        Optional<Match> matchToRemove = findByTeams(homeTeam, awayTeam);
        return matchToRemove.map(matches::remove).orElse(false);
    }

    @Override
    public Optional<Match> findByTeams(String team1, String team2) {
        return matches.stream()
                .filter(match ->
                        (match.getHomeTeam().equalsIgnoreCase(team1) && match.getAwayTeam().equalsIgnoreCase(team2)) ||
                                (match.getHomeTeam().equalsIgnoreCase(team2) && match.getAwayTeam().equalsIgnoreCase(team1))
                )
                .findFirst();
    }

    @Override
    public List<Match> findAll() {
        return List.copyOf(matches);
    }
}
