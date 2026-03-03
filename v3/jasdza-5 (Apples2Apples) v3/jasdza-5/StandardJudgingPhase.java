import java.util.ArrayList;
import java.util.List;

public class StandardJudgingPhase implements IGamePhase {
    @Override
    public void execute(GameContext context) {
        if (!context.gameRunning || context.playedCards.isEmpty()) return;

        List<String> playedStrings = new ArrayList<>();
        for (PlayedCard pc : context.playedCards) {
            playedStrings.add(pc.cardText);
        }

        for (IPlayer p : context.players) {
            p.notifyPlayedApples(playedStrings);
        }

        String winningString = context.currentJudge.judge(context.currentGreenApple, playedStrings);
        
        PlayedCard winningCard = context.playedCards.stream()
            .filter(pc -> pc.cardText.equals(winningString))
            .findFirst()
            .orElse(null);

        if (winningCard != null) {
            IPlayer winner = winningCard.owner;
            winner.addPoint(context.currentGreenApple);
            
            String winMsg = (winner.isBot() ? "Bot " : "Player ") + "ID" + winner.getId() + " won with: " + winningCard.cardText;
            System.out.println(winMsg);
            
            for (IPlayer p : context.players) {
                p.notifyRoundResults(winningCard.cardText, winMsg);
            }
            
            if (context.winningStrategy.hasWon(context.players.size(), winner.getScore())) {
                String gameWinMsg = (winner.isBot() ? "Bot " : "Player ") + "ID" + winner.getId() + " won the game";
                for (IPlayer p : context.players) p.notifyGameEnd(gameWinMsg);
                System.out.println(gameWinMsg);
                context.gameRunning = false;
            }
        }
    }
}