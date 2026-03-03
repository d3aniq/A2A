import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;

/**
 * A local human player that interacts via the console (System.in / System.out).
 * This restores the original server-player functionality from the legacy code,
 * allowing the person hosting the game to participate as a player.
 */
public class ConsolePlayer extends Player {
    private BufferedReader reader;

    public ConsolePlayer(int id) {
        super(id, false);
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    @Override
    public String playCard(String greenApple) {
        System.out.println("Choose a red apple to play:");
        for (int i = 0; i < hand.size(); i++) {
            System.out.println("  [" + i + "] " + hand.get(i));
        }
        System.out.println();

        int choice = readChoice(0, hand.size() - 1);
        String card = hand.get(choice);
        removeCardFromHand(card);
        return card;
    }

    @Override
    public String judge(String greenApple, List<String> candidates) {
        System.out.println("Choose which red apple wins:");
        for (int i = 0; i < candidates.size(); i++) {
            System.out.println("  [" + i + "] " + candidates.get(i));
        }
        System.out.println();

        int choice = readChoice(0, candidates.size() - 1);
        return candidates.get(choice);
    }

    @Override
    public void notifyRoundStart(boolean isJudge, String greenApple) {
        System.out.println("*****************************************************");
        if (isJudge) {
            System.out.println("**                 NEW ROUND - JUDGE               **");
        } else {
            System.out.println("**                    NEW ROUND                    **");
        }
        System.out.println("*****************************************************");
        System.out.println("Green apple: " + greenApple + "\n");
    }

    @Override
    public void notifyPlayedApples(List<String> playedApples) {
        System.out.println("\nThe following apples were played:");
        for (int i = 0; i < playedApples.size(); i++) {
            System.out.println("  [" + i + "] " + playedApples.get(i));
        }
        System.out.println();
    }

    @Override
    public void notifyRoundResults(String winningApple, String winnerMessage) {
        System.out.println(winnerMessage + "\n");
    }

    @Override
    public void notifyGameEnd(String winnerMessage) {
        System.out.println("\nFINISHED: " + winnerMessage);
    }

    /**
     * Reads an integer choice from the console within the given range.
     * Re-prompts if the input is invalid.
     */
    private int readChoice(int min, int max) {
        while (true) {
            try {
                String input = reader.readLine();
                int choice = Integer.parseInt(input.trim());
                if (choice >= min && choice <= max) {
                    return choice;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ":");
            } catch (NumberFormatException e) {
                System.out.println("That is not a valid option. Please enter a number:");
            } catch (Exception e) {
                System.out.println("Error reading input: " + e.getMessage());
                return min;
            }
        }
    }
}
