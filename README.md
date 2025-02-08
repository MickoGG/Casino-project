# Casino project

### Casino application implemented in Java using JavaFX library and MySQL.

This project uses **EclipseLink (JPA 2.2)** for **object-relational mapping (ORM)**, allowing seamless interaction between **Java** objects and the **MySQL** database.

## Key features:
- **Multi-threaded architecture** – Enables multiple games to run simultaneously
- **User authentication** – Login, register, and delete accounts
- **Casino games** – Various games available for users to play
  - **Spin-based games** – Includes games with a spinning mechanism
  - **Bonus purchases** – Players can buy in-game bonuses
- **Virtual transactions** – Deposit and withdraw (conceptual, not real money)
- **Transaction history tracking** – Keeps a detailed record of user transactions
  - **Account tracking** – Keeps a record of total deposits and withdrawals (all-time)
  - **Session tracking** – Tracks the total amount of money a user has spent and received during each session
  - **Per-game tracking** – For every game played, the system records how much the user has wagered and how much the game has paid out

## Used libraries:
- **JavaFX** – For the graphical user interface (GUI)
- **MySQL Connector/J** – For database communication
- **EclipseLink (JPA 2.2)** – For ORM and database persistence
