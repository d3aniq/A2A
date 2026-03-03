public class ReplenishPhase implements IGamePhase {
    @Override
    public void execute(GameContext context) {
        if (!context.gameRunning) return;
        
        for (IPlayer p : context.players) {
            if (p != context.currentJudge && context.deckManager.hasRedApples()) {
                p.receiveNewCard(context.deckManager.drawRedApple());
            }
        }
    }
}