import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;

public class Apples2ApplesTest {

    @Test
    public void testRule15_FourPlayers() {
        WinningStrategy strategy = new StandardRulesWinningStrategy();
        // 4 players -> Need 8 apples to win
        assertFalse("4 players should not win with 4 apples", strategy.hasWon(4, 4));
        assertFalse("4 players should not win with 7 apples", strategy.hasWon(4, 7));
        assertTrue("4 players SHOULD win with 8 apples", strategy.hasWon(4, 8));
    }

    @Test
    public void testRule15_FivePlayers() {
        WinningStrategy strategy = new StandardRulesWinningStrategy();
        // 5 players -> Need 7 apples to win
        assertFalse("5 players should not win with 6 apples", strategy.hasWon(5, 6));
        assertTrue("5 players SHOULD win with 7 apples", strategy.hasWon(5, 7));
    }

    @Test
    public void testRule15_EightPlayers() {
        WinningStrategy strategy = new StandardRulesWinningStrategy();
        // 8+ players -> Need 4 apples to win
        assertFalse("8 players should not win with 3 apples", strategy.hasWon(8, 3));
        assertTrue("8 players SHOULD win with 4 apples", strategy.hasWon(8, 4));
    }

    @Test
    public void testEngineFlowWithMocks() {
        // 1. Arrange: Create Mock Objects
        List<IPlayer> mockPlayers = new ArrayList<>();
        // Add 4 BotPlayers for simplicity
        for(int i = 0; i < 4; i++) mockPlayers.add(new BotPlayer(i));

        // Create a deterministic DeckManager for testing
        IDeckManager testDeck = new DeckManager(
            Arrays.asList("Red1", "Red2", "Red3", "Red4", "Red5", "Red6", "Red7", "Red8"), 
            Arrays.asList("Green1")
        );
    
        WinningStrategy strategy = new StandardRulesWinningStrategy();

        GameContext context = new GameContext(
            mockPlayers, 
            testDeck, 
            strategy, 
            Executors.newFixedThreadPool(1) // En tråd räcker för ett test
        );

        List<IGamePhase> phases = new ArrayList<>();
        phases.add(new DrawPhase());
        phases.add(new PlayCardsPhase());
        phases.add(new StandardJudgingPhase());
        phases.add(new ReplenishPhase());

        // 2. Inject dependencies
        GameEngine engine = new GameEngine(context, phases);

        // 3. Act: Play ONE round
        boolean result = engine.playRound();

        // 4. Assert
        assertTrue("Round should finish successfully", result);
        assertEquals("Judge should rotate", 1, engine.getJudgeIndex());
    }
}