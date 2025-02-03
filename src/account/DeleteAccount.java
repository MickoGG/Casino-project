package account;

import application.Utils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class DeleteAccount {
    
    private DeleteAccount() {}
    
    
    public static void deleteAccount() {
        // Creating dialog
        Alert deleteAccAlert = Utils.createAlert("Delete account", "Enter username and password\nto delete account:", null);

        // Creating textual fields
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Adding labels and textual fields into container
        GridPane gp = (GridPane) deleteAccAlert.getDialogPane().getContent();
        gp.add(new Label("Username:"), 0, 0);
        gp.add(usernameTextField, 1, 0);
        gp.add(new Label("Password:"), 0, 1);
        gp.add(passwordField, 1, 1);

        // Creating and setting types of buttons
        ButtonType deleteButtonType = new ButtonType("Delete", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        deleteAccAlert.getButtonTypes().remove(ButtonType.OK);
        deleteAccAlert.getButtonTypes().remove(ButtonType.CANCEL);
        deleteAccAlert.getButtonTypes().addAll(deleteButtonType, cancelButtonType);

        // Getting "Delete" button
        Button deleteButton = (Button) deleteAccAlert.getDialogPane().lookupButton(deleteButtonType);
        deleteButton.setOnAction(e -> {
            String username;
            String password;

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

            int ret = Account.deleteAccountForUser(username, password);

            // Checking for error
            if (ret == -1) {
                Utils.errorAlert("Username doesn't exist.", null);
                return;
            } else if (ret == -2) {
                Utils.errorAlert("Wrong password.", null);
                return;
            }

            // Creating dialog for success in deleting account
            Utils.infoAlert("Account deleted successfully.", null);
        });

        deleteAccAlert.show();
    }
    
}
