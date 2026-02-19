import java.util.*;
import java.util.concurrent.*;

public class PlayCardsPhase implements IGamePhase {
    @Override
    public void execute(GameContext context) {
        if (!context.gameRunning) return;

        Map<IPlayer, Future<String>> submissions = new HashMap<>();
        
        for (IPlayer p : context.players) {
            if (p != context.currentJudge) {
                submissions.put(p, context.threadPool.submit(() -> p.playCard(context.currentGreenApple)));
            }
        }

        context.playedCards.clear();
        
        for (Map.Entry<IPlayer, Future<String>> entry : submissions.entrySet()) {
            try {
                String cardText = entry.getValue().get();
                if (cardText != null && !cardText.isEmpty()) {
                    context.playedCards.add(new PlayedCard(cardText, entry.getKey()));
                }
            } catch (Exception e) { 
                e.printStackTrace(); 
            }
        }
        
        Collections.shuffle(context.playedCards);
    }
}