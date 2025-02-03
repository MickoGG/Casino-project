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


public class Register {
    
    private Register() {}
    
    
    public static void register() {
        // Creating dialog
        Alert registerAlert = Utils.createAlert("Register", "Enter information to register:", null);

        // Creating textual fields
        TextField fullNameTextField = new TextField();
        fullNameTextField.setPromptText("Full name");
        TextField emailTextField = new TextField();
        emailTextField.setPromptText("Email");
        TextField usernameTextField = new TextField();
        usernameTextField.setPromptText("Username");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Adding labels and textual fields into container
        GridPane gp = (GridPane) registerAlert.getDialogPane().getContent();
        gp.add(new Label("Full name:"), 0, 0);
        gp.add(fullNameTextField, 1, 0);
        gp.add(new Label("Email:"), 0, 1);
        gp.add(emailTextField, 1, 1);
        gp.add(new Label("Username:"), 0, 2);
        gp.add(usernameTextField, 1, 2);
        gp.add(new Label("Password:"), 0, 3);
        gp.add(passwordField, 1, 3);

        // Creating and setting types of buttons
        ButtonType registerButtonType = new ButtonType("Register", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        registerAlert.getButtonTypes().remove(ButtonType.OK);
        registerAlert.getButtonTypes().remove(ButtonType.CANCEL);
        registerAlert.getButtonTypes().addAll(registerButtonType, cancelButtonType);

        // Getting "Register" button
        Button registerButton = (Button) registerAlert.getDialogPane().lookupButton(registerButtonType);
        registerButton.setOnAction(e -> {
            String fullName;
            String email;
            String username;
            String password;

            // Getting fullName
            if (fullNameTextField.getText().length() == 0) {
                Utils.errorAlert("You didn't enter name.", null);
                return;
            }
            else if (fullNameTextField.getText().length() > 99) {
                Utils.errorAlert("Name is too long.", null);
                return;
            }
            fullName = fullNameTextField.getText();
            
            // Getting email
            if (emailTextField.getText().length() == 0) {
                Utils.errorAlert("You didn't enter email.", null);
                return;
            }
            else if (emailTextField.getText().length() > 90) {
                Utils.errorAlert("Email is too long.", null);
                return;
            }
            email = emailTextField.getText();
            
            // Getting username
            if (usernameTextField.getText().length() == 0) {
                Utils.errorAlert("You didn't enter username.", null);
                return;
            }
            else if (usernameTextField.getText().length() > 40) {
                Utils.errorAlert("Username is too long.", null);
                return;
            }
            username = usernameTextField.getText();

            // Getting password
            if (passwordField.getText().length() == 0) {
                Utils.errorAlert("You didn't enter password.", null);
                return;
            }
            else if (passwordField.getText().length() > 49) {
                Utils.errorAlert("Password is too long.", null);
                return;
            }
            password = passwordField.getText();

            int ret = Account.registerUser(fullName, email, username, password);

            // Checking for error
            switch (ret) {
                case -1:
                    Utils.errorAlert("Full name must contain only letters and white spaces.", null);
                    return;
                case -2:
                    Utils.errorAlert("Email is in the wrong format.", null);
                    return;
                case -3:
                    Utils.errorAlert("Username must start with a letter and must contain\nonly letters, numbers and underscores.", null);
                    return;
                case -4:
                    Utils.errorAlert("Password must be at least 6 characters long.", null);
                    return;
                case -5:
                    Utils.errorAlert("Username already exist.", null);
                    return;
                case -6:
                    Utils.errorAlert("Email already exist.", null);
                    return;
            }

            // Creating dialog for success in registration
            Utils.infoAlert("Registration successfully completed.", null);
        });

        registerAlert.show();
    }
    
}
