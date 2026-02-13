import java.util.*;

public class DeckManager implements IDeckManager {
    private List<String> redApples;
    private List<String> greenApples;

    public DeckManager(List<String> redApples, List<String> greenApples) {
        this.redApples = new ArrayList<>(redApples);
        this.greenApples = new ArrayList<>(greenApples);
        Collections.shuffle(this.redApples);
        Collections.shuffle(this.greenApples);
    }

    public String drawRedApple() {
        return redApples.isEmpty() ? null : redApples.remove(0);
    }

    public String drawGreenApple() {
        return greenApples.isEmpty() ? null : greenApples.remove(0);
    }

    public boolean hasGreenApples() { return !greenApples.isEmpty(); }
    public boolean hasRedApples() { return !redApples.isEmpty(); }

    public void dealInitialHands(List<IPlayer> players) {
        for (IPlayer p : players) {
            List<String> hand = new ArrayList<>();
            for (int i = 0; i < 7; i++) {
                if (hasRedApples()) hand.add(drawRedApple());
            }
            p.receiveHand(hand);
        }
    }
}