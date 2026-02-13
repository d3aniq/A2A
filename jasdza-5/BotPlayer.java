import java.util.List;
import java.util.Random;

public class BotPlayer extends Player {
    private Random rnd = new Random();

    public BotPlayer(int id) {
        super(id, true);
    }

    @Override
    public String playCard(String greenApple) {
        if (hand.isEmpty()) return "";
        // Simple random choice
        int index = rnd.nextInt(hand.size());
        String card = hand.get(index);
        removeCardFromHand(card);
        return card;
    }

    @Override
    public String judge(String greenApple, List<String> candidates) {
        if (candidates.isEmpty()) return "";
        // Randomly pick a winner
        return candidates.get(rnd.nextInt(candidates.size()));
    }

    // Bots don't need to see console output for notifications
    @Override public void notifyRoundStart(boolean isJudge, String greenApple) {}
    @Override public void notifyPlayedApples(List<String> playedApples) {}
    @Override public void notifyRoundResults(String winningApple, String winnerName) {}
    @Override public void notifyGameEnd(String winnerName) {}
}