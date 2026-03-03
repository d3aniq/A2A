import java.util.ArrayList;
import java.util.List;

public abstract class Player implements IPlayer {
    protected int id;
    protected boolean isBot;
    protected List<String> hand = new ArrayList<>();
    protected List<String> greenApplesWon = new ArrayList<>();

    public Player(int id, boolean isBot) {
        this.id = id;
        this.isBot = isBot;
    }

    @Override
    public int getId() { return id; }

    @Override
    public boolean isBot() { return isBot; }

    @Override
    public void receiveHand(List<String> hand) {
        this.hand.addAll(hand);
    }

    @Override
    public void receiveNewCard(String redApple) {
        this.hand.add(redApple);
    }

    @Override
    public void addPoint(String greenApple) {
        greenApplesWon.add(greenApple);
    }

    @Override
    public int getScore() {
        return greenApplesWon.size();
    }

    protected void removeCardFromHand(String card) {
        hand.remove(card);
    }
}