package application;

import account.Account;
import account.Player;
import java.util.List;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.layout.GridPane;
import models.Currentlyplaying;


public class Utils {
    
    private static Alert errorAlert;
    private static Alert infoAlert;
    
    
    private Utils() {}
    
    
    // Just in case after log in, if error occurred last time and Currentlyplaying was not processed to the end, should not exist
    public static void deleteCurrentlyPlayingForPlayerIfExists() {
        if (Player.getCurrentPlayer() == null) return;
        
        List<Currentlyplaying> currentlyPlayingList = Account.getEntityManager()
                .createNamedQuery("Currentlyplaying.findByIDUser", Currentlyplaying.class)
                .setParameter("iDUser", Player.getCurrentPlayer().getPlayerID()).getResultList();
        
        try {
            Account.getEntityManager().getTransaction().begin();
            for (Currentlyplaying cp : currentlyPlayingList) {
                Account.getEntityManager().remove(cp);
            }
            Account.getEntityManager().getTransaction().commit();
        }
        finally {
            if (Account.getEntityManager().getTransaction().isActive()) Account.getEntityManager().getTransaction().rollback();
        }
    }
    
    
    public static double roundToNDec(double value, int numDec) {
        double multiplier = Math.pow(10, numDec);
        return (double) Math.round(value * multiplier) / multiplier;
    }
    
    
    // Creating error dialog based of function parameters
    public static void errorAlert(String header, String content) {
        if (errorAlert != null) {
            errorAlert.close();
        }
        errorAlert = new Alert(Alert.AlertType.ERROR);
        errorAlert.setTitle("Error");
        errorAlert.setHeaderText(header);
        errorAlert.setContentText(content);

        errorAlert.showAndWait();
    }

    
    // Creating information dialog based of function parameters
    public static void infoAlert(String header, String content) {
        if (infoAlert != null) {
            infoAlert.close();
        }
        infoAlert = new Alert(Alert.AlertType.INFORMATION);
        infoAlert.setTitle("Information");
        infoAlert.setHeaderText(header);
        infoAlert.setContentText(content);

        infoAlert.showAndWait();
    }

    
    // Creating confirmation dialog based of function parameters
    public static Alert createAlert(String title, String header, String content) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.setContentText(content);

        // Creating and setting layout for alert
        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.setPadding(new Insets(20));
        alert.getDialogPane().setContent(gp);

        return alert;
    }
    
}
