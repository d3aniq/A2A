import java.util.*;
import java.util.concurrent.*;

public class GameEngine {
    private List<IPlayer> players;
    private List<String> redApplesDeck;
    private List<String> greenApplesDeck;
    private WinningStrategy winningStrategy;
    private int judgeIndex = 0;
    private boolean isRunning = true;
    private ExecutorService threadPool;

    public GameEngine(List<IPlayer> players, List<String> redApples, List<String> greenApples, WinningStrategy strategy) {
        this.players = players;
        this.redApplesDeck = new ArrayList<>(redApples);
        this.greenApplesDeck = new ArrayList<>(greenApples);
        this.winningStrategy = strategy;
        this.threadPool = Executors.newFixedThreadPool(Math.max(1, players.size()));
        
        Collections.shuffle(this.redApplesDeck);
        Collections.shuffle(this.greenApplesDeck);
    }

    public void startGame() {
        // Deal initial hands (7 cards)
        for (IPlayer p : players) {
            List<String> hand = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (!redApplesDeck.isEmpty()) hand.add(redApplesDeck.remove(0));
            }
            p.receiveHand(hand);
        }

        judgeIndex = new Random().nextInt(players.size());

        while (isRunning) {
            playRound();
        }
        threadPool.shutdown();
    }

    private void playRound() {
        if (greenApplesDeck.isEmpty()) {
            System.out.println("Out of green apples!");
            isRunning = false; 
            return;
        }

        String currentGreenApple = greenApplesDeck.remove(0);
        IPlayer judge = players.get(judgeIndex);
        
        // 1. Notify start of round
        for (IPlayer p : players) {
            p.notifyRoundStart(p == judge, currentGreenApple);
        }

        // 2. Collect played apples from non-judges (Concurrent)
        Map<IPlayer, Future<String>> submissions = new HashMap<>();
        for (IPlayer p : players) {
            if (p != judge) {
                submissions.put(p, threadPool.submit(() -> p.playCard(currentGreenApple)));
            }
        }

        List<String> playedCards = new ArrayList<>();
        Map<String, IPlayer> cardOwnerMap = new HashMap<>();

        for (Map.Entry<IPlayer, Future<String>> entry : submissions.entrySet()) {
            try {
                String card = entry.getValue().get(); // Waits for player response
                if (card != null && !card.isEmpty()) {
                    playedCards.add(card);
                    cardOwnerMap.put(card, entry.getKey());
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        Collections.shuffle(playedCards);

        // 3. Show played apples to everyone
        for (IPlayer p : players) {
            p.notifyPlayedApples(playedCards);
        }

        // 4. Judge picks a winner
        String winningCard = judge.judge(currentGreenApple, playedCards);
        IPlayer winner = cardOwnerMap.get(winningCard);
        
        if (winner != null) {
            winner.addPoint(currentGreenApple);
            String winMsg = (winner.isBot() ? "Bot " : "Player ") + "ID" + winner.getId() + " won with: " + winningCard;
            System.out.println(winMsg); // Log to server console
            
            for (IPlayer p : players) {
                p.notifyRoundResults(winningCard, winMsg);
            }
            
            // 5. Check Win Condition (Fixing Rule 15)
            if (winningStrategy.hasWon(players.size(), winner.getScore())) {
                String gameWinMsg = (winner.isBot() ? "Bot " : "Player ") + "ID" + winner.getId() + " won the game";
                for (IPlayer p : players) p.notifyGameEnd(gameWinMsg);
                System.out.println(gameWinMsg);
                isRunning = false;
                return;
            }
        }

        // 6. Replenish hands
        for (IPlayer p : players) {
            if (p != judge && !redApplesDeck.isEmpty()) {
                p.receiveNewCard(redApplesDeck.remove(0));
            }
        }

        // 7. Rotate Judge
        judgeIndex = (judgeIndex + 1) % players.size();
    }
}