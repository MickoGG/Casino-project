package account;

import account.Account.TransactionType;
import application.Main;
import application.Utils;
import java.util.Date;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import game.BaseGame;
import java.math.BigDecimal;
import models.Deposit;
import models.User;
import models.Withdraw;


public class ChangeBalance {

    private ChangeBalance() {}
    
    
    public static void changeBalance(TransactionType type) {
        // Checking if player is logged in
        if (Player.getCurrentPlayer() == null) {
            Utils.errorAlert("You must be logged in.", null);
            return;
        }
        
        // Creating dialog
        Alert balanceAlert;
        if (type == TransactionType.DEPOSIT) balanceAlert = Utils.createAlert("Add balance", "Enter username and amount to add:", null);
        else balanceAlert = Utils.createAlert("Withdraw", "Enter username and amount to withdraw:", null);

        // Creating textual fields
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        TextField creditCardTextField = new TextField();
        creditCardTextField.setPromptText("coming soon");
        creditCardTextField.setDisable(true);
        TextField balanceTextField = new TextField();
        if (type == TransactionType.DEPOSIT) balanceTextField.setPromptText("Amount to add (€)");
        else balanceTextField.setPromptText("Amount to withdraw (€)");
        
        // Adding labels and textual fields into container
        GridPane gp = (GridPane) balanceAlert.getDialogPane().getContent();
        gp.add(new Label("Username:"), 0, 0);
        gp.add(usernameTextField, 1, 0);
        gp.add(new Label("Password:"), 0, 1);
        gp.add(passwordField, 1, 1);
        gp.add(new Label("Credit card:"), 0, 2);
        gp.add(creditCardTextField, 1, 2);
        if (type == TransactionType.DEPOSIT) gp.add(new Label("Amount to add (€):"), 0, 3);
        else gp.add(new Label("Amount to withdraw (€):"), 0, 3);
        gp.add(balanceTextField, 1, 3);

        // Creating and setting types of buttons
        ButtonType addButtonType = new ButtonType("Confirm", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        balanceAlert.getButtonTypes().remove(ButtonType.OK);
        balanceAlert.getButtonTypes().remove(ButtonType.CANCEL);
        balanceAlert.getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Getting "Add" button
        Button addButton = (Button) balanceAlert.getDialogPane().lookupButton(addButtonType);
        addButton.setOnAction(e -> {
            String username;
            String password;
            BigDecimal amount;

            // Getting username
            if (usernameTextField.getText().length() == 0) {
                Utils.errorAlert("You didn't enter username.", null);
                return;
            }
            username = usernameTextField.getText();
            
            // Getting password
            if (passwordField.getText().length() == 0) {
                Utils.errorAlert("You didn't enter password.", null);
                return;
            }
            password = passwordField.getText();

            // Getting amount
            try {
                amount = BigDecimal.valueOf(Double.parseDouble(balanceTextField.getText()));
            } catch (NumberFormatException e2) {
                Utils.errorAlert("Wrong value for amount.", null);
                return;
            }
            
            // Checking amount
            if (amount.compareTo(BigDecimal.ZERO) != 1) {
                Utils.errorAlert("Amount must be greater than 0.", null);
                return;
            }
            else if (type != TransactionType.DEPOSIT && amount.compareTo(Player.getCurrentPlayer().getBalance()) == 1) {
                Utils.errorAlert("You don't have enough balance.", "Your balance is: " + Player.getCurrentPlayer().getBalance());
                return;
            }

            int ret;
            if (type == TransactionType.DEPOSIT) ret = Account.changeBalanceForUser(TransactionType.DEPOSIT, username, password, amount);
            else ret = Account.changeBalanceForUser(TransactionType.WITHDRAW, username, password, amount);

            // Checking for error
            if (ret == -1) {
                Utils.errorAlert("Username doesn't exist.", null);
                return;
            }
            else if (ret == -2) {
                Utils.errorAlert("Wrong password.", null);
                return;
            }

            // Update balance labels of all games because player could be in some game
            for (BaseGame game : Main.getListOfGames()) {
                game.updateBalance();
                game.setBalanceLabel(Player.getCurrentPlayer().getBalance());
            }
            
            Object obj;
            User user = Account.getEntityManager().createNamedQuery("User.findByUsername", User.class).setParameter("username", username).getSingleResult();
            
            if (type == TransactionType.DEPOSIT) {
                // Making deposit object
                Deposit deposit = new Deposit();
                deposit.setDate(new Date());
                deposit.setAmount(amount);
                deposit.setIDUser(user);
                obj = deposit;
            }
            else {
                // Making withdraw object
                Withdraw withdraw = new Withdraw();
                withdraw.setDate(new Date());
                withdraw.setAmount(amount);
                withdraw.setIDUser(user);
                obj = withdraw;
            }
            
            // Update for table Deposit or Withdraw based on type of transaction
            try {
                Account.getEntityManager().getTransaction().begin();
                if (type == TransactionType.DEPOSIT) Account.getEntityManager().persist((Deposit) obj);
                else Account.getEntityManager().persist((Withdraw) obj);
                Account.getEntityManager().getTransaction().commit();
            }
            finally {
                if (Account.getEntityManager().getTransaction().isActive()) Account.getEntityManager().getTransaction().rollback();
            }

            // Creating dialog for operation success
            if (type == TransactionType.DEPOSIT) Utils.infoAlert("Balance added successfully.", null);
            else Utils.infoAlert("Withdraw done successfully.", null);
        });

        balanceAlert.show();
    }
    
}
