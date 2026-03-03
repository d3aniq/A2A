import java.util.List;

public interface IPlayer {
    int getId();
    boolean isBot();
    
    // Game Actions
    void receiveHand(List<String> hand);
    void receiveNewCard(String redApple);
    String playCard(String greenApple);
    String judge(String greenApple, List<String> candidates);
    
    // Scoring
    void addPoint(String greenApple);
    int getScore();
    
    // Notifications (Observer pattern elements)
    void notifyRoundStart(boolean isJudge, String greenApple);
    void notifyPlayedApples(List<String> playedApples);
    void notifyRoundResults(String winningApple, String winnerName);
    void notifyGameEnd(String winnerName);
}