import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Apples2Apples {
    public static void main(String[] args) {
        // Simple usage check: if args provided, assume it's number of online players
        int onlinePlayersCount = 0;
        if (args.length > 0) {
            try {
                onlinePlayersCount = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                System.out.println("To run as client, use original client code. This is the Server Refactor.");
                return;
            }
        }

        try {
            // Load Decks from files
            List<String> redApples = Files.readAllLines(Paths.get("redApples.txt"), StandardCharsets.ISO_8859_1);
            List<String> greenApples = Files.readAllLines(Paths.get("greenApples.txt"), StandardCharsets.ISO_8859_1);

            List<IPlayer> players = new ArrayList<>();
            ServerSocket serverSocket = null;

            // 1. Connect Online Players
            if (onlinePlayersCount > 0) {
                serverSocket = new ServerSocket(2048);
                System.out.println("Server started on port 2048. Waiting for " + onlinePlayersCount + " players...");
                for (int i = 0; i < onlinePlayersCount; i++) {
                    Socket socket = serverSocket.accept();
                    System.out.println("Player " + i + " connected.");
                    players.add(new NetworkPlayer(i, socket));
                }
            }

            // 2. Fill remaining slots with Bots
            int currentId = players.size();
            while (players.size() < 4) {
                players.add(new BotPlayer(currentId++));
            }

            // 3. Start Game
            System.out.println("Starting Game with " + players.size() + " players.");
            
            IDeckManager deckManager = new DeckManager(redApples, greenApples);

            GameEngine engine = new GameEngine(
                players, 
                deckManager, // Pass the manager, not the raw lists
                new StandardRulesWinningStrategy()
            );
            engine.startGame();

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}