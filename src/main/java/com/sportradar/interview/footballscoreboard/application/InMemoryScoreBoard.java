package com.sportradar.interview.footballscoreboard.application;

import com.sportradar.interview.footballscoreboard.domain.Match;
import com.sportradar.interview.footballscoreboard.domain.MatchRepository;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


public class InMemoryScoreBoard implements ScoreBoard {

    private final MatchRepository matchRepository;

    public InMemoryScoreBoard(MatchRepository matchRepository) {
        this.matchRepository = matchRepository;
    }

    @Override
    public Match startGame(String homeTeam, String awayTeam) {
        Optional<Match> existingMatch = matchRepository.findByTeams(homeTeam, awayTeam);
        if (existingMatch.isPresent()) {
            throw new IllegalArgumentException("A game between " + homeTeam + " and " + awayTeam + " is already in progress.");
        }

        Match newMatch = new Match(homeTeam, awayTeam);
        return matchRepository.save(newMatch);
    }

    @Override
    public void finishGame(String homeTeam, String awayTeam) {
        boolean deleted = matchRepository.delete(homeTeam, awayTeam);
        if (!deleted) {
            throw new IllegalArgumentException("Game between " + homeTeam + " and " + awayTeam + " not found on the scoreboard.");
        }
    }

    @Override
    public Match updateScore(String homeTeam, String awayTeam, int homeScore, int awayScore) {
        Optional<Match> matchToUpdate = matchRepository.findByTeams(homeTeam, awayTeam);
        if (matchToUpdate.isPresent()) {
            Match match = matchToUpdate.get();
            match.updateScore(homeScore, awayScore);
            return matchRepository.save(match);
        } else {
            throw new IllegalArgumentException("Game between " + homeTeam + " and " + awayTeam + " not found on the scoreboard.");
        }
    }

    @Override
    public List<Match> getSummary() {
        return matchRepository.findAll().stream()
                .sorted(Comparator.comparingInt(Match::getTotalScore)
                        .thenComparing(Match::getStartTime)
                        .reversed())
                .toList();
    }

    @Override
    public List<Match> getCurrentMatches() {
        return Collections.unmodifiableList(matchRepository.findAll());
    }
}
