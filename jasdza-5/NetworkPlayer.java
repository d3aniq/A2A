import java.io.*;
import java.net.Socket;
import java.util.List;

public class NetworkPlayer extends Player {
    private Socket socket;
    private BufferedReader in;
    private DataOutputStream out;

    public NetworkPlayer(int id, Socket socket) throws IOException {
        super(id, false);
        this.socket = socket;
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        this.out = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void receiveHand(List<String> hand) {
        super.receiveHand(hand);
        try {
            // Protocol: Semi-colon separated list of cards
            String handString = String.join(";", hand);
            out.writeBytes(handString + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void notifyRoundStart(boolean isJudge, String greenApple) {
        try {
            out.writeBytes((isJudge ? "JUDGE" : "NOTJUDGE") + "\n");
            out.writeBytes("Green apple: " + greenApple + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public String playCard(String greenApple) {
        try {
            // Client sends the text of the selected apple
            String playedCard = in.readLine();
            if (playedCard == null) return "";
            removeCardFromHand(playedCard);
            return playedCard;
        } catch (IOException e) { return ""; }
    }

    @Override
    public void notifyPlayedApples(List<String> playedApples) {
        try {
            // Protocol format required by client to print correctly
            StringBuilder sb = new StringBuilder();
            sb.append("Played Apples:"); 
            for (int i = 0; i < playedApples.size(); i++) {
                sb.append("#\t[").append(i).append("] ").append(playedApples.get(i));
            }
            out.writeBytes(sb.toString() + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public String judge(String greenApple, List<String> candidates) {
        try {
            // Client sends the index of the winning apple
            String line = in.readLine();
            int index = Integer.parseInt(line);
            if (index >= 0 && index < candidates.size()) {
                return candidates.get(index);
            }
        } catch (Exception e) { e.printStackTrace(); }
        return candidates.get(0); // Fallback
    }

    @Override
    public void notifyRoundResults(String winningApple, String winnerName) {
        try {
            out.writeBytes(winnerName + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void receiveNewCard(String redApple) {
        super.receiveNewCard(redApple);
        try {
            out.writeBytes(redApple + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }

    @Override
    public void notifyGameEnd(String winnerName) {
        try {
            out.writeBytes("FINISHED: " + winnerName + "\n");
        } catch (IOException e) { e.printStackTrace(); }
    }
}