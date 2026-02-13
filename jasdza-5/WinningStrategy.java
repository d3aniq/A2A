public interface WinningStrategy {
    /**
     * Determines if a player has won based on the number of players and their score.
     * @param playerCount Total number of players in the game.
     * @param score The number of Green Apples the player has collected.
     * @return true if the win condition is met.
     */
    boolean hasWon(int playerCount, int score);
}