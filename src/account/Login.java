package account;

import application.Main;
import application.Utils;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;


public class Login {
    
    private Login() {}
    
    
    public static void login() {
        // Creating dialog
        Alert loginAlert = Utils.createAlert("Login", "Enter information to login:", null);

        // Creating textual fields
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Adding labels and textual fields into container
        GridPane gp = (GridPane) loginAlert.getDialogPane().getContent();
        gp.add(new Label("Username:"), 0, 0);
        gp.add(usernameTextField, 1, 0);
        gp.add(new Label("Password:"), 0, 1);
        gp.add(passwordField, 1, 1);

        // Creating and setting types of buttons
        ButtonType loginButtonType = new ButtonType("Login", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        loginAlert.getButtonTypes().remove(ButtonType.OK);
        loginAlert.getButtonTypes().remove(ButtonType.CANCEL);
        loginAlert.getButtonTypes().addAll(loginButtonType, cancelButtonType);

        // Getting "Login" button
        Button loginButton = (Button) loginAlert.getDialogPane().lookupButton(loginButtonType);
        loginButton.setOnAction(e -> {
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

            int ret = Account.loginUser(username, password);

            // Checking for error
            if (ret == -1) {
                Utils.errorAlert("Username doesn't exist.", null);
                return;
            } else if (ret == -2) {
                Utils.errorAlert("Wrong password.", null);
                return;
            }

            Utils.deleteCurrentlyPlayingForPlayerIfExists();
            Main.loadGames();
            Main.setGamePickPane();
        });

        loginAlert.show();
    }
    
}
