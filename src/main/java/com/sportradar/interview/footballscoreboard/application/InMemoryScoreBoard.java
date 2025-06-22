package com.sportradar.interview.footballscoreboard.application;

import com.sportradar.interview.footballscoreboard.domain.Match;
import com.sportradar.interview.footballscoreboard.domain.MatchRepository;

import java.util.*;
import java.util.stream.Collectors;


public class InMemoryScoreBoard implements ScoreBoard {

    private final MatchRepository matchRepository;
    private static final Map<String, String> teamToContinentMap = new HashMap<>();

    static {
        teamToContinentMap.put("Francja", "Europa");
        teamToContinentMap.put("Japonia", "Azja");
        teamToContinentMap.put("Meksyk", "Ameryka Północna");
        teamToContinentMap.put("Brazylia", "Ameryka Południowa");
        teamToContinentMap.put("Niemcy", "Europa");
        teamToContinentMap.put("Argentyna", "Ameryka Południowa");
        teamToContinentMap.put("Nigeria", "Afryka");
        teamToContinentMap.put("Australia", "Australia i Oceania");
        teamToContinentMap.put("USA", "Ameryka Północna");

    }

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


        /** zwroc kontynenty wraz z liczba bramek każdej z drużyn*/
    @Override
    public Map<String, Integer> getSummaryScore() {
        List<Match> allMatches = matchRepository.findAll();

        // 1. Stwórz tymczasową mapę, która będzie sumować gole dla każdej drużyny (Home + Away)
        Map<String, Integer> teamScores = new HashMap<>();

        for (Match match : allMatches) {
            // Dodaj wynik gospodarzy
            teamScores.merge(match.getHomeTeam(), match.getHomeScore(), Integer::sum);
            // Dodaj wynik gości
            teamScores.merge(match.getAwayTeam(), match.getAwayScore(), Integer::sum);
        }

        // 2. Stwórz finalną mapę, która będzie sumować gole według kontynentów
        Map<String, Integer> continentScores = new HashMap<>();

        teamScores.forEach((team, totalScore) -> {
            String continent = teamToContinentMap.get(team); // Pobierz kontynent dla danej drużyny

            if (continent != null) {
                // Jeśli kontynent istnieje, dodaj wynik drużyny do wyniku kontynentu
                continentScores.merge(continent, totalScore, Integer::sum);
            } else {
                // Opcjonalnie: loguj, jeśli brakuje kontynentu dla drużyny
                System.out.println("Ostrzeżenie: Brak informacji o kontynencie dla drużyny: " + team);
            }
        });

        return continentScores;
    }
}
