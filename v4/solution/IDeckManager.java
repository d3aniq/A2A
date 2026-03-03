import java.util.List;

public interface IDeckManager {
    String drawRedApple();
    String drawGreenApple();
    boolean hasGreenApples();
    boolean hasRedApples();
    void dealInitialHands(List<IPlayer> players);
}