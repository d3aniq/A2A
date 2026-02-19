public class DrawPhase implements IGamePhase {
    @Override
    public void execute(GameContext context) {
        if (!context.deckManager.hasGreenApples()) {
            context.gameRunning = false;
            return;
        }
        context.currentGreenApple = context.deckManager.drawGreenApple();
        for (IPlayer p : context.players) {
            p.notifyRoundStart(p == context.currentJudge, context.currentGreenApple);
        }
    }
}