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
    javac Apples2Apples.java

To compile the Unit Tests (Windows PowerShell/CMD):
    javac -cp ".;junit-4.13.2.jar;hamcrest-core-1.3.jar" Apples2ApplesTest.java

(Note: If on Mac/Linux, replace the semicolons ';' with colons ':')

3. RUNNING THE GAME
--------------------------------------------------------------------------------
This refactored application runs as the Game Server.

Start the Server (Host):
   Run the following command to start a server. You must specify the number of 
   expected online players. The game will wait until this many clients connect 
   before filling the rest of the slots with Bots.

   Usage: java Apples2Apples [numberOfOnlinePlayers]

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
    ...
    OK (3 tests)

5. FILE OVERVIEW
--------------------------------------------------------------------------------
- Apples2Apples.java ........ Main entry point (Bootstrapper).
- GameEngine.java ........... Core logic (State machine, flow control).
- IPlayer.java .............. Interface for game participants.
- Player.java ............... Abstract base class for players.
- BotPlayer.java ............ AI implementation.
- NetworkPlayer.java ........ Remote client implementation.
- WinningStrategy.java ...... Strategy interface for win conditions.
- StandardRulesWinningStrategy.java ... Implements the specific rules (Rule 15).
- Apples2ApplesTest.java .... JUnit test class.
- junit-4.13.2.jar .......... Required library for testing.
- hamcrest-core-1.3.jar ..... Required library for testing.
- redApples.txt ............. Data file for Red Apple cards.
- greenApples.txt ........... Data file for Green Apple cards.