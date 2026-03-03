public class StandardRulesWinningStrategy implements WinningStrategy {
    @Override
    public boolean hasWon(int playerCount, int score) {
        if (playerCount >= 8) return score >= 4;
        if (playerCount == 7) return score >= 5;
        if (playerCount == 6) return score >= 6;
        if (playerCount == 5) return score >= 7;
        // Default for 4 players (or fewer, though 4 is min)
        return score >= 8;
    }
}