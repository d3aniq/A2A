import java.util.*;
import java.util.concurrent.*;

public class GameEngine {
    private List<IPlayer> players;
    private IDeckManager deckManager; // Refactored: Use Interface for Testability
    private WinningStrategy winningStrategy;
    private int judgeIndex = 0;
    private ExecutorService threadPool;

    // Refactored: Constructor now accepts IDeckManager instead of raw Lists
    public GameEngine(List<IPlayer> players, IDeckManager deckManager, WinningStrategy strategy) {
        this.players = players;
        this.deckManager = deckManager;
        this.winningStrategy = strategy;
        this.threadPool = Executors.newFixedThreadPool(Math.max(1, players.size()));
    }

    public void startGame() {
        // Delegate dealing to the manager
        deckManager.dealInitialHands(players);

        judgeIndex = new Random().nextInt(players.size());

        boolean gameRunning = true;
        while (gameRunning) {
            gameRunning = playRound();
        }
        threadPool.shutdown();
    }

    // Refactored: Returns boolean so we can unit test one round at a time
    public boolean playRound() {
        if (!deckManager.hasGreenApples()) {
            System.out.println("Out of green apples!");
            return false; // Stop game
        }

        String currentGreenApple = deckManager.drawGreenApple();
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

        // FIX: Use List<PlayedCard> instead of Map to allow duplicate words from different players
        List<PlayedCard> playedCards = new ArrayList<>();

        for (Map.Entry<IPlayer, Future<String>> entry : submissions.entrySet()) {
            try {
                String cardText = entry.getValue().get(); // Waits for player response
                if (cardText != null && !cardText.isEmpty()) {
                    playedCards.add(new PlayedCard(cardText, entry.getKey()));
                }
            } catch (Exception e) { e.printStackTrace(); }
        }

        Collections.shuffle(playedCards);

        // Create a list of strings for the players/judge to see
        List<String> playedStrings = new ArrayList<>();
        for (PlayedCard pc : playedCards) {
            playedStrings.add(pc.cardText);
        }

        // 3. Show played apples to everyone
        for (IPlayer p : players) {
            p.notifyPlayedApples(playedStrings);
        }

        // 4. Judge picks a winner (Judge sees strings, returns a string)
        String winningString = judge.judge(currentGreenApple, playedStrings);
        
        // Match the winning string back to the Player owner
        PlayedCard winningCard = null;
        for (PlayedCard pc : playedCards) {
            if (pc.cardText.equals(winningString)) {
                winningCard = pc;
                break; // The first match wins (list was already shuffled)
            }
        }
        
        if (winningCard != null) {
            IPlayer winner = winningCard.owner;
            winner.addPoint(currentGreenApple);
            
            String winMsg = (winner.isBot() ? "Bot " : "Player ") + "ID" + winner.getId() + " won with: " + winningCard.cardText;
            System.out.println(winMsg); // Log to server console
            
            for (IPlayer p : players) {
                p.notifyRoundResults(winningCard.cardText, winMsg);
            }
            
            // 5. Check Win Condition
            if (winningStrategy.hasWon(players.size(), winner.getScore())) {
                String gameWinMsg = (winner.isBot() ? "Bot " : "Player ") + "ID" + winner.getId() + " won the game";
                for (IPlayer p : players) p.notifyGameEnd(gameWinMsg);
                System.out.println(gameWinMsg);
                return false; // Stop game
            }
        }

        // 6. Replenish hands
        for (IPlayer p : players) {
            if (p != judge && deckManager.hasRedApples()) {
                p.receiveNewCard(deckManager.drawRedApple());
            }
        }

        // 7. Rotate Judge
        judgeIndex = (judgeIndex + 1) % players.size();
        return true; // Continue game
    }

    public int getJudgeIndex() {
        return judgeIndex;
    }
}