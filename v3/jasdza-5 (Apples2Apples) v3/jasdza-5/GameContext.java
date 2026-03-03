import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;

public class GameContext {
    public List<IPlayer> players;
    public IDeckManager deckManager;
    public ExecutorService threadPool;
    public WinningStrategy winningStrategy;
    
    public IPlayer currentJudge;
    public String currentGreenApple;
    public List<PlayedCard> playedCards = new ArrayList<>();
    public boolean gameRunning = true;

    public GameContext(List<IPlayer> players, IDeckManager deckManager, WinningStrategy winningStrategy, ExecutorService threadPool) {
        this.players = players;
        this.deckManager = deckManager;
        this.winningStrategy = winningStrategy;
        this.threadPool = threadPool;
    }
}