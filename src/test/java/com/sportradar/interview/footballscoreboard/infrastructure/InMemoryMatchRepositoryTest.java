package com.sportradar.interview.footballscoreboard.infrastructure;


import com.sportradar.interview.footballscoreboard.domain.Match;
import com.sportradar.interview.footballscoreboard.domain.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryMatchRepositoryTest {

    private MatchRepository matchRepository;

    @BeforeEach
    void setUp() {
        matchRepository = new InMemoryMatchRepository();
    }

    @Test
    @DisplayName("Should save a new match and find it")
    void shouldSaveNewMatch() {
        // given
        Match match = new Match("TeamA", "TeamB");

        // when
        Match savedMatch = matchRepository.save(match);

        // then
        assertNotNull(savedMatch);
        assertEquals(match, savedMatch);
        assertTrue(matchRepository.findByTeams("TeamA", "TeamB").isPresent());
        assertEquals(1, matchRepository.findAll().size());
    }

    @Test
    @DisplayName("Should update existing match's score when saving with same teams")
    void shouldUpdateExistingMatch() {
        // given
        Match match1 = new Match("TeamA", "TeamB");
        Match match2 = new Match("TeamA", "TeamB");

        // when
        matchRepository.save(match1);
        match2.updateScore(2, 1);
        Match updatedMatch = matchRepository.save(match2);

        // then
        assertEquals(2, updatedMatch.getHomeScore());
        assertEquals(1, updatedMatch.getAwayScore());
        assertEquals(1, matchRepository.findAll().size());
        assertEquals(2, matchRepository.findByTeams("TeamA", "TeamB").get().getHomeScore());
    }

    @Test
    @DisplayName("Should delete a match by teams")
    void shouldRemoveMatch() {
        // given
        Match match = new Match("TeamA", "TeamB");
        matchRepository.save(match);
        assertEquals(1, matchRepository.findAll().size());

        // when
        boolean deleted = matchRepository.delete("TeamA", "TeamB");

        // then
        assertTrue(deleted);
        assertFalse(matchRepository.findByTeams("TeamA", "TeamB").isPresent());
        assertTrue(matchRepository.findAll().isEmpty());
    }

    @Test
    @DisplayName("Should return false if deleting a non-existent match")
    void shouldReturnFalseForNonExistentMatch() {
        // given

        // when
        boolean deleted = matchRepository.delete("XXX", "YYY");

        // then
        assertFalse(deleted);
    }

    @Test
    @DisplayName("Should find a match by teams")
    void shouldFindMatchCaseAndOrderInsensitive() {
        // given
        Match match = new Match("HomeTeam", "AwayTeam");
        matchRepository.save(match);

        // when // then
        assertTrue(matchRepository.findByTeams("HomeTeam", "AwayTeam").isPresent());
        assertTrue(matchRepository.findByTeams("hometeam", "awayteam").isPresent());
        assertTrue(matchRepository.findByTeams("AwayTeam", "HomeTeam").isPresent());
        assertTrue(matchRepository.findByTeams("awayteam", "hometeam").isPresent());
        assertFalse(matchRepository.findByTeams("XXX", "YYY").isPresent());
    }

    @Test
    @DisplayName("Should return all saved matches")
    void shouldReturnAllMatches() {
        // given
        matchRepository.save(new Match("Team1", "Team2"));
        matchRepository.save(new Match("Team3", "Team4"));

        // when
        List<Match> allMatches = matchRepository.findAll();

        // then
        assertEquals(2, allMatches.size());
        assertTrue(allMatches.contains(new Match("Team1", "Team2")));
        assertTrue(allMatches.contains(new Match("Team3", "Team4")));
    }

    @Test
    @DisplayName("Should return an immutable list from findAll")
    void shouldReturnImmutableList() {
        // given
        matchRepository.save(new Match("Team1", "Team2"));

        // when
        List<Match> allMatches = matchRepository.findAll();

        // then
        assertThrows(UnsupportedOperationException.class, () -> allMatches.add(new Match("Team3", "Team4")));
    }
}