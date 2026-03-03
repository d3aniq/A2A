================================================================================
                           APPLES TO APPLES - REFACTORED
================================================================================

1. PREREQUISITES
--------------------------------------------------------------------------------
- Java Development Kit (JDK) 8 or higher.
- JUnit 4.13.2 and Hamcrest Core 1.3 (Included in folder).

2. COMPILATION INSTRUCTIONS
--------------------------------------------------------------------------------
To compile the game:
    javac *.java

To compile the Unit Tests (Windows PowerShell/CMD):
    javac -cp ".;junit-4.13.2.jar;hamcrest-core-1.3.jar" *.java

(Note: If on Mac/Linux, replace the semicolons ';' with colons ':')

3. RUNNING THE GAME
--------------------------------------------------------------------------------
This refactored application runs as the Game Server. The person running the
server participates as a local console player.

Start the Server (Host):
   Run the following command to start a server. You can optionally specify the
   number of expected online players. The game will wait until this many clients
   connect before filling the rest of the slots with Bots. The host always plays
   as a local console player.

   Usage: java Apples2Apples [numberOfOnlinePlayers]

   Example (Play locally with 3 Bots):
       java Apples2Apples

   Example (Host a game for 1 remote friend + 2 Bots):
       java Apples2Apples 1

(Note: To play as a Client, use the original legacy client application to connect
 to this server on port 2048.)

4. RUNNING UNIT TESTS
--------------------------------------------------------------------------------
To run the JUnit tests:

    java -cp ".;junit-4.13.2.jar;hamcrest-core-1.3.jar" org.junit.runner.JUnitCore Apples2ApplesTest

Expected Output:
    JUnit version 4.13.2
    ....................
    OK (20 tests)

The test suite covers all game rules (1-15) including:
- Rules 1-2: Deck loading from files
- Rule 3: Deck shuffling
- Rule 4: Dealing 7 cards to each player
- Rule 5: Random starting judge
- Rule 6: Green apple drawn each round
- Rule 7: All non-judge players play a card
- Rule 8: Played cards order randomised
- Rule 9: All cards collected before judging
- Rule 10: Judge selects winner, point awarded
- Rule 11: Submitted apples discarded between rounds
- Rule 12: Players replenished to 7 cards
- Rule 13: Judge rotation
- Rule 14: Score tracked via green apples
- Rule 15: Win conditions scale by player count (all thresholds tested)
- Integration: Full round flow verification

5. FILE OVERVIEW
--------------------------------------------------------------------------------
Core Game:
- Apples2Apples.java ........ Main entry point (Bootstrapper).
- GameEngine.java ........... Core logic (State machine, round loop).
- GameContext.java ........... Shared state passed between phases.

Phase Pipeline (IGamePhase implementations):
- IGamePhase.java ........... Interface for game phases.
- DrawPhase.java ............ Draws a green apple and notifies players.
- PlayCardsPhase.java ....... Collects red apple submissions from players.
- StandardJudgingPhase.java . Judge selects winner, awards point.
- ReplenishPhase.java ....... Replenishes player hands with red apples.

Player Abstraction:
- IPlayer.java .............. Interface for game participants.
- Player.java ............... Abstract base class for players.
- BotPlayer.java ............ AI implementation (random choices).
- ConsolePlayer.java ........ Local human player via terminal.
- NetworkPlayer.java ........ Remote client implementation (TCP).

Strategy:
- WinningStrategy.java ...... Strategy interface for win conditions.
- StandardRulesWinningStrategy.java ... Official rules (Rule 15).

Deck Management:
- IDeckManager.java ......... Interface for deck operations.
- DeckManager.java .......... Loads, shuffles, and deals cards.

Other:
- PlayedCard.java ........... Value object for a submitted card + owner.
- Apples2ApplesTest.java .... JUnit test class (20 tests, rules 1-15).
- junit-4.13.2.jar .......... Required library for testing.
- hamcrest-core-1.3.jar ..... Required library for testing.
- redApples.txt ............. Data file for Red Apple cards.
- greenApples.txt ........... Data file for Green Apple cards.
- Report.pdf ................ Written answers for Part A (Questions 1-2).
