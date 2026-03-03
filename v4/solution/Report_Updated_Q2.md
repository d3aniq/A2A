# 2. Software Architecture Design and Refactoring

## Architecture Overview

The software architecture has been redesigned to transition from a monolithic script to a modular, object-oriented design. The primary goal is to separate Domain Logic (Rules, Game Flow) from Infrastructure (Networking, Console I/O) and to make the game phases independently extensible.

### Key Components

- **GameEngine**: The central controller responsible for running the game loop. It iterates over a configurable list of game phases each round. It operates purely on logic and abstractions, with no dependencies on sockets or specific UI implementations.

- **IGamePhase (Interface)**: An abstraction for a single step in the game round. Each phase (DrawPhase, PlayCardsPhase, StandardJudgingPhase, ReplenishPhase) implements this interface. The GameEngine holds an ordered list of IGamePhase objects and executes them sequentially. This allows phases to be added, removed, or replaced without modifying the engine.

- **GameContext**: A shared state object passed to each phase, containing the player list, deck manager, current judge, played cards, and game status. This decouples phases from each other — they communicate through the context rather than direct references.

- **IPlayer (Interface)**: An abstraction layer that normalizes the interaction between the Engine and different actor types (BotPlayer, NetworkPlayer, ConsolePlayer, or future types). The engine never depends on concrete player implementations.

- **IDeckManager (Interface)**: Abstracts deck operations (draw, deal, check availability). This allows for alternative deck implementations, such as decks with special card types (Wild red apples, Apples and Pears).

- **WinningStrategy (Interface)**: Encapsulates the logic for determining win conditions. The StandardRulesWinningStrategy implements the official rules (Rule 15), but alternative strategies can be injected without modifying the engine.

## Diagrams

### Class Diagram (Structure)

```
                          +------------------+
                          |   GameEngine     |
                          |------------------|
                          | -context         |
                          | -phases: List    |
                          | -judgeIndex      |
                          |------------------|
                          | +startGame()     |
                          | +playRound()     |
                          +--------+---------+
                                   |
                    +--------------+--------------+
                    |              |              |
             uses phases     uses context    rotates judge
                    |              |
        +-----------+      +------+--------+
        |                  |  GameContext   |
+-------v--------+        |---------------|
| <<interface>>  |        | players       |
| IGamePhase     |        | deckManager   |
|----------------|        | winStrategy   |
| +execute(ctx)  |        | currentJudge  |
+-------+--------+        | playedCards   |
        |                  | greenApple    |
        |                  +---------------+
   +----+----+----+----+
   |         |         |         |
+--v---+ +---v--+ +----v---+ +--v--------+
|Draw  | |Play  | |Standard| |Replenish  |
|Phase | |Cards | |Judging | |Phase      |
|      | |Phase | |Phase   | |           |
+------+ +------+ +--------+ +-----------+


        +----------------+          +--------------------+
        | <<interface>>  |          | <<interface>>      |
        | IPlayer        |          | WinningStrategy    |
        |----------------|          |--------------------|
        | +playCard()    |          | +hasWon(count,score)|
        | +judge()       |          +----------+---------+
        | +addPoint()    |                     |
        | +getScore()    |          +----------+---------+
        | +receiveHand() |          | StandardRules      |
        | +notify...()   |          | WinningStrategy    |
        +-------+--------+          +--------------------+
                |
     +----------+----------+
     |          |          |
+----v---+ +---v------+ +-v----------+
|BotPlayer| |Network  | |Console    |
|        | |Player   | |Player     |
+--------+ +---------+ +-----------+


        +----------------+
        | <<interface>>  |
        | IDeckManager   |
        |----------------|
        | +drawRedApple()|
        | +drawGreenApple|
        | +dealInitial() |
        +-------+--------+
                |
        +-------v--------+
        | DeckManager    |
        +----------------+
```

### Sequence Diagram (One Game Round)

```
GameEngine        DrawPhase      PlayCardsPhase    JudgingPhase     ReplenishPhase
    |                |                |                |                |
    |--execute(ctx)->|                |                |                |
    |   draw green   |                |                |                |
    |   notify all   |                |                |                |
    |<--return-------|                |                |                |
    |                                 |                |                |
    |--execute(ctx)------------------>|                |                |
    |   submit via threadpool         |                |                |
    |   Future.get() all cards        |                |                |
    |   shuffle played cards          |                |                |
    |<--return------------------------|                |                |
    |                                                  |                |
    |--execute(ctx)----------------------------------->|                |
    |   notify players of played cards                 |                |
    |   judge selects winner                           |                |
    |   award green apple to winner                    |                |
    |   check WinningStrategy.hasWon()                 |                |
    |<--return-----------------------------------------|                |
    |                                                                   |
    |--execute(ctx)---------------------------------------------------->|
    |   give 1 red apple to each non-judge                              |
    |<--return----------------------------------------------------------|
    |
    | rotate judgeIndex
    | repeat from DrawPhase
```

## Design Motivation and Quality Attributes

The redesign explicitly targets the required quality attributes by adhering to SOLID principles and optimizing Booch's metrics (High Cohesion, Low Coupling).

### Addressing Extensibility (Future Modifications)

**Requirement**: The system must support future modifications such as adding or replacing game phases, new card types, and rule variations.

**Design Choice 1: Phase Pipeline with IGamePhase (Command Pattern)**

- **Design**: The game round is decomposed into a list of IGamePhase objects that the GameEngine iterates through. Each phase is a self-contained unit of logic operating on the shared GameContext.
- **SOLID Principle**: Open/Closed Principle (OCP). New phases can be added without modifying the GameEngine or existing phases. Existing phases can be replaced by alternative implementations.
- **Concrete future modification examples**:
  - **Bad Harvest**: Create a `BadHarvestPhase` implementing IGamePhase. Insert it into the phase list after ReplenishPhase. In its `execute()`, each player discards 3 unwanted cards and draws 3 new ones from the deck. No existing code needs to change.
  - **Two-For-One Apples**: Create a `TwoForOneDrawPhase` that draws two green apples instead of one. Replace the standard DrawPhase in the phase list. The judging phase would need to award both green apples to the winner.
  - **Apple's Eye View**: Create an `ApplesEyeViewPhase` that runs before the DrawPhase. It prompts the player to the judge's left to choose a persona for the judge. This persona information can be stored in the GameContext.
  - **Voting instead of Judge**: Create a `VotingJudgingPhase` implementing IGamePhase. Replace StandardJudgingPhase in the phase list. Each non-judge player votes for their favourite (but not their own). The majority-voted card wins.
  - **Judge Hand Replacement**: Create a `JudgeSwapPhase` implementing IGamePhase. Insert it before DrawPhase in the list. The judge can discard and redraw cards.
- **Metric Improvement**: Each phase has high cohesion (single responsibility) and low coupling (communicates only through GameContext). New phases can be unit-tested independently.

**Design Choice 2: Strategy Pattern for Win Conditions**

- **Design**: The logic for checking if a player has won is extracted into the WinningStrategy interface. StandardRulesWinningStrategy implements the official Rule 15 thresholds.
- **SOLID Principle**: Open/Closed Principle (OCP). The GameEngine is closed for modification but open for extension. To implement a "Quick Game" mode or tournament rules, a developer simply creates a new class implementing WinningStrategy.
- **Metric Improvement**: Reduces functional complexity within the game engine by removing conditional logic related to specific rule sets.

### Addressing Modifiability (Different Player Types)

**Requirement**: The system currently manages Bots and Remote Clients but may need to support new actor types (e.g., Local Console Players, GUI Players).

**Design Choice: Dependency Inversion via IPlayer Interface**

- **Design**: The GameEngine depends on the IPlayer interface rather than concrete NetworkPlayer, BotPlayer, or ConsolePlayer classes. Three implementations exist: BotPlayer (AI), NetworkPlayer (remote TCP client), and ConsolePlayer (local human via terminal).
- **SOLID Principle**: Dependency Inversion Principle (DIP). High-level modules (Game Logic) do not depend on low-level modules (Network Sockets, Console I/O). Both depend on abstractions. This allows the underlying communication protocol to change (e.g., from TCP to WebSocket) without affecting the core game logic.
- **SOLID Principle**: Liskov Substitution Principle (LSP). Any IPlayer implementation can be substituted into the game without affecting correctness. The GameEngine treats all players identically.
- **Metric Improvement**: Significantly reduces coupling. The GameEngine is decoupled from java.net and java.io packages entirely.

**Design Choice: IDeckManager Interface**

- **Design**: Deck operations are abstracted behind IDeckManager. The DeckManager class handles loading, shuffling, and dealing cards.
- **Future Modification**: To add "Wild red apples" or "Apples and Pears" special cards, one could create a new IDeckManager implementation (e.g., WildCardDeckManager) that includes special card types. The GameEngine and phases do not need modification.
- **SOLID Principle**: Single Responsibility Principle (SRP). Deck management is isolated from game flow logic.

### Addressing Testability

**Requirement**: The ability to verify business rules (like Rule 15) without complex integration environments.

**Design Choice: Decoupling Logic from I/O via Dependency Injection**

- **Design**: All dependencies (players, deck, strategy, thread pool) are injected into GameContext via the constructor. The IPlayer interface exposes only the methods required for game flow (playCard, judge), hiding connection management.
- **Motivation**: This separation allows for the creation of test players (BotPlayer) during testing. We can instantiate a GameEngine in a JUnit environment, populate it with in-memory players and a controlled deck, and simulate game scenarios instantly — no sockets, no console input, no blocking.
- **IGamePhase testability**: Each phase can be tested independently by creating a GameContext with known state, executing the phase, and asserting the resulting state. This enables fine-grained unit tests for each game rule.
- **Metric Improvement**: Increases cohesion. The NetworkPlayer class focuses solely on network serialization, the ConsolePlayer on terminal I/O, the BotPlayer on AI logic, and the GameEngine focuses solely on game rules.

### Design Patterns Summary

| Pattern | Where Applied | Purpose |
|---------|--------------|---------|
| **Command** | IGamePhase + DrawPhase, PlayCardsPhase, StandardJudgingPhase, ReplenishPhase | Encapsulates each game phase as an independent, swappable command. Enables adding/removing/replacing phases. |
| **Strategy** | WinningStrategy + StandardRulesWinningStrategy | Encapsulates win-condition logic. Allows different rule sets to be swapped without engine changes. |
| **Template Method** | Player (abstract) + BotPlayer, NetworkPlayer, ConsolePlayer | Common player behavior (hand management, scoring) in base class; specific I/O behavior in subclasses. |
| **Observer-like** | IPlayer notification methods (notifyRoundStart, notifyPlayedApples, notifyRoundResults, notifyGameEnd) | Decouples game events from how they are displayed to each player type. |

## References

- Gamma, E., Helm, R., Johnson, R., & Vlissides, J. (1994). *Design Patterns: Elements of Reusable Object-Oriented Software*. Addison-Wesley.
- Martin, R. C. (2003). *Agile Software Development, Principles, Patterns, and Practices*. Prentice Hall.
- Booch, G. (2007). *Object-Oriented Analysis and Design with Applications* (3rd ed.). Addison-Wesley.
- Oracle. (2024). *Java SE Documentation*. https://docs.oracle.com/en/java/javase/
