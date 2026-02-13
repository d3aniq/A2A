import org.junit.Test;
import static org.junit.Assert.*;

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
}