import java.util.List;
import java.util.Random;

public class GameEngine {
    private GameContext context;
    private List<IGamePhase> phases;
    private int judgeIndex = 0;

    public GameEngine(GameContext context, List<IGamePhase> phases) {
        this.context = context;
        this.phases = phases;
    }

    public void startGame() {
        context.deckManager.dealInitialHands(context.players);
        judgeIndex = new Random().nextInt(context.players.size());

        while (context.gameRunning) {
            playRound();
        }
        context.threadPool.shutdown();
    }

    public boolean playRound() {
        context.currentJudge = context.players.get(judgeIndex);
        context.playedCards.clear();

        for (IGamePhase phase : phases) {
            if (!context.gameRunning) break;
            phase.execute(context);
        }

        judgeIndex = (judgeIndex + 1) % context.players.size();
        return context.gameRunning;
    }

    public int getJudgeIndex() { return judgeIndex; }
}