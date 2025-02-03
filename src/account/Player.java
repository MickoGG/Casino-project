package account;

import java.math.BigDecimal;


public class Player {

    private final String username;
    private final String password;
    private final long playerID;
    private BigDecimal balance = BigDecimal.ZERO;
    private static Player currentPlayer;

    
    public Player(String username, String password, long playerID, BigDecimal balance) {
        this.username = username;
        this.password = password;
        this.playerID = playerID;
        this.balance = balance;
    }

    
    public String getUsername() {
        return username;
    }

    
    public String getPassword() {
        return password;
    }

    
    public long getPlayerID() {
        return playerID;
    }

    
    public BigDecimal getBalance() {
        return balance;
    }

    
    public void setBalance(BigDecimal newBalance) {
        balance = newBalance;
    }

    
    public static Player getCurrentPlayer() {
        return currentPlayer;
    }

    
    public static void setCurrentPlayer(Player player) {
        currentPlayer = player;
    }

    
    public void addBalance(BigDecimal amountToAdd) {
        Account.changeBalanceForUser(Account.TransactionType.DEPOSIT, username, password, amountToAdd);
    }

}
