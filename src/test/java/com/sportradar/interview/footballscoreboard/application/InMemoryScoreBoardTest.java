package com.sportradar.interview.footballscoreboard.application;

import com.sportradar.interview.footballscoreboard.domain.Match;
import com.sportradar.interview.footballscoreboard.domain.MatchRepository;
import com.sportradar.interview.footballscoreboard.infrastructure.InMemoryMatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InMemoryScoreBoardTest {

    private ScoreBoard scoreBoard;
    private MatchRepository matchRepository = new InMemoryMatchRepository();

    @BeforeEach
    void setUp() {
        scoreBoard = new InMemoryScoreBoard(matchRepository);
    }

    @Test
    @DisplayName("Should start a new game with 0-0 score")
    void shouldStartNewGame() {
        //given

        // when
        Match match = scoreBoard.startGame("Brazil", "Argentina");

        //then
        assertNotNull(match);
        assertEquals("Brazil", match.getHomeTeam());
        assertEquals("Argentina", match.getAwayTeam());
        assertEquals(0, match.getHomeScore());
        assertEquals(0, match.getAwayScore());
        assertEquals(1, scoreBoard.getCurrentMatches().size());
        assertEquals(1, matchRepository.findAll().size());
    }

    @Test
    @DisplayName("Should throw exception when starting a game that already exists")
    void shouldThrowExceptionIfGameExistsIfStatGame() {
        // given

        // when
        scoreBoard.startGame("Brazil", "Argentina");

        // then
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Brazil", "Argentina"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Argentina", "Brazil"));
    }

    @Test
    @DisplayName("Should throw exception for null or empty team names when starting a game")
    void shouldThrowExceptionForInvalidTeamNames() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame(null, "Argentina"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Brazil", ""));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame(" ", "Argentina"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Brazil", null));
    }

    @Test
    @DisplayName("Should throw exception if home team and away team are the same")
    void shouldThrowExceptionIfTeamsAreSame() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Brazil", "Brazil"));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.startGame("Brazil", "brazil"));
    }

    @Test
    @DisplayName("Should update an ongoing game's score")
    void shouldUpdateGameScore() {
        // given
        scoreBoard.startGame("Brazil", "Argentina");

        // when
        Match updatedMatch = scoreBoard.updateScore("Brazil", "Argentina", 2, 1);

        // then
        assertEquals(2, updatedMatch.getHomeScore());
        assertEquals(1, updatedMatch.getAwayScore());
        // Verify the match in the repository is also updated
        Optional<Match> repoMatch = matchRepository.findByTeams("Brazil", "Argentina");
        assertTrue(repoMatch.isPresent());
        assertEquals(2, repoMatch.get().getHomeScore());
        assertEquals(1, repoMatch.get().getAwayScore());
    }

    @Test
    @DisplayName("Should update score for a game identified by reverse team order")
    void shouldUpdateGameScoreReverseOrder() {
        // given
        scoreBoard.startGame("Brazil", "Argentina");

        // when
        Match updatedMatch = scoreBoard.updateScore("Argentina", "Brazil", 1, 2);

        // then
        assertEquals(2, updatedMatch.getAwayScore());
        assertEquals(1, updatedMatch.getHomeScore());
        Optional<Match> repoMatch = matchRepository.findByTeams("Brazil", "Argentina");
        assertTrue(repoMatch.isPresent());
        assertEquals(2, repoMatch.get().getAwayScore());
        assertEquals(1, repoMatch.get().getHomeScore());
    }

    @Test
    @DisplayName("Should throw exception when updating score for a non-existent game")
    void shouldThrowExceptionIfGameNotFoundIfUpdateScore() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore("Brazil", "Argentina", 1, 0));
    }

    @Test
    @DisplayName("Should throw exception when updating with negative scores")
    void shouldThrowExceptionForNegativeScores() {
        // given

        // when
        scoreBoard.startGame("Brazil", "Argentina");

        // then
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore("Brazil", "Argentina", -1, 0));
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.updateScore("Brazil", "Argentina", 0, -5));
    }

    @Test
    @DisplayName("Should finish an existing game")
    void shouldRemoveGameIfFinished() {
        // given
        scoreBoard.startGame("Brazil", "Argentina");
        assertEquals(1, scoreBoard.getCurrentMatches().size());

        // when
        scoreBoard.finishGame("Brazil", "Argentina");

        // then
        assertEquals(0, scoreBoard.getCurrentMatches().size());
        assertEquals(0, matchRepository.findAll().size());
    }

    @Test
    @DisplayName("Should finish a game identified by reverse team order")
    void shouldRemoveGameReverseOrder() {
        // given
        scoreBoard.startGame("Brazil", "Argentina");
        assertEquals(1, scoreBoard.getCurrentMatches().size());

        // when
        scoreBoard.finishGame("Argentina", "Brazil");

        // then
        assertEquals(0, scoreBoard.getCurrentMatches().size());
        assertEquals(0, matchRepository.findAll().size());
    }

    @Test
    @DisplayName("Should throw exception when finishing a non-existent game")
    void shouldThrowExceptionIfGameNotFoundIfFinishGame() {
        assertThrows(IllegalArgumentException.class, () -> scoreBoard.finishGame("Brazil", "Argentina"));
    }

    @Test
    @DisplayName("Should return empty summary for an empty scoreboard")
    void shouldReturnEmptyListForEmptyBoard() {
        assertTrue(scoreBoard.getSummary().isEmpty());
    }

    @Test
    @DisplayName("Should return games sorted by total score and then by most recent start time")
    void shouldReturnSortedGames() throws InterruptedException {
        // given
        scoreBoard.startGame("Mexico", "Canada");
        scoreBoard.updateScore("Mexico", "Canada", 0, 5);

        Thread.sleep(10);

        scoreBoard.startGame("Spain", "Brazil");
        scoreBoard.updateScore("Spain", "Brazil", 10, 2);

        Thread.sleep(10);

        scoreBoard.startGame("Germany", "France");
        scoreBoard.updateScore("Germany", "France", 2, 2);

        Thread.sleep(10);

        scoreBoard.startGame("Uruguay", "Italy");
        scoreBoard.updateScore("Uruguay", "Italy", 6, 6);

        Thread.sleep(10);

        scoreBoard.startGame("Argentina", "Australia");
        scoreBoard.updateScore("Argentina", "Australia", 3, 1);

        // when
        List<Match> summary = scoreBoard.getSummary();

        // then
        assertEquals(5, summary.size());
        assertEquals("Uruguay", summary.get(0).getHomeTeam());
        assertEquals("Spain", summary.get(1).getHomeTeam());
        assertEquals("Mexico", summary.get(2).getHomeTeam());
        assertEquals("Argentina", summary.get(3).getHomeTeam());
        assertEquals("Germany", summary.get(4).getHomeTeam());

        assertEquals(12, summary.get(0).getTotalScore());
        assertEquals(12, summary.get(1).getTotalScore());
        assertEquals(5, summary.get(2).getTotalScore());
        assertEquals(4, summary.get(3).getTotalScore());
        assertEquals(4, summary.get(4).getTotalScore());

        assertTrue(summary.get(0).getStartTime().isAfter(summary.get(1).getStartTime()));
        assertTrue(summary.get(3).getStartTime().isAfter(summary.get(4).getStartTime()));
    }
}
