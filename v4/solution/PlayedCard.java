public class PlayedCard {
    public final String cardText;
    public final IPlayer owner;

    public PlayedCard(String cardText, IPlayer owner) {
        this.cardText = cardText;
        this.owner = owner;
    }

    @Override
    public String toString() {
        return cardText;
    }
}