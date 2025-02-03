package application;

import game.BookOfGoldenSands;
import game.BaseGame;
import account.Player;
import account.Account;
import account.DeleteAccount;
import account.LogOut;
import account.Login;
import account.Register;
import account.ChangeBalance;
import game.SugarRush;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import models.Currentlyplaying;
import models.CurrentlyplayingPK;
import models.Game;
import models.Played;
import models.User;


public class Main extends Application {

    private static final BorderPane root = new BorderPane();
    private static BorderPane startMenuPane;
    private static BorderPane gamePickPane;
    
    private static final ArrayList<BaseGame> listOfGames = new ArrayList<>();

    // Games
    private static BookOfGoldenSands BookOfGoldenSandsGame;
    private static Pane BookOfGoldenSandsPane;
    private static SugarRush SugarRushGame;
    private static Pane SugarRushPane;
    
    private static final List<Currentlyplaying> currPlayings = new ArrayList<>();
    
    
    public static void setStartMenuPane() {
        root.setCenter(startMenuPane);
    }
    
    
    public static void setGamePickPane() {
        root.setCenter(gamePickPane);
    }
    
    
    public static ArrayList<BaseGame> getListOfGames() {
        return listOfGames;
    }
    
    
    public static void loadGames() {
        //
        // This is where all the games are added and defined
        //
        BookOfGoldenSandsGame = new BookOfGoldenSands();
        BookOfGoldenSandsPane = BookOfGoldenSandsGame.game();
        listOfGames.add(BookOfGoldenSandsGame);
        
        SugarRushGame = new SugarRush();
        SugarRushPane = SugarRushGame.game();
        listOfGames.add(SugarRushGame);
    }
    
    
    private static void setGame(BaseGame game, Pane gamePane) {
        game.init();
        root.setCenter(gamePane);
        
        User user = Account.getEntityManager().find(User.class, (int) Player.getCurrentPlayer().getPlayerID());
        Game g = Account.getEntityManager().find(Game.class, game.getGameID());
        
        BaseGame.acquireMutex();
        createCurrentlyPlaying(user, g);
        BaseGame.releaseMutex();
    }
    
    
    public static Currentlyplaying getCurrPlaying(String name) {
        for (Currentlyplaying cp : currPlayings) {
            if (cp.getGame().getName().equals(name)) {
                return cp;
            }
        }
        return null;
    }
    
    
    private static void createCurrentlyPlaying(User user, Game g) {
        for (Currentlyplaying cp : currPlayings) {
            if (cp.getGame().getName().equals(g.getName())) {
                return;
            }
        }
        
        CurrentlyplayingPK cpPK = new CurrentlyplayingPK(user.getIDUser(), g.getIDGame());
        Currentlyplaying cp = new Currentlyplaying();
        cp.setCurrentlyplayingPK(cpPK);
        cp.setUser(user);
        cp.setGame(g);
        cp.setStartTime(new Date());
        cp.setTotalWager(BigDecimal.ZERO);
        cp.setTotalReceived(BigDecimal.ZERO);

        currPlayings.add(cp);

        try {
            Account.getEntityManager().getTransaction().begin();
            Account.getEntityManager().persist(cp);
            Account.getEntityManager().getTransaction().commit();
        }
        finally {
            if (Account.getEntityManager().getTransaction().isActive()) Account.getEntityManager().getTransaction().rollback();
        }
    }
    
    
    public static void finalizeGameBeforeClose(String name) {
        Currentlyplaying currPlaying = null;
        
        for (Currentlyplaying cp : currPlayings) {
            if (cp.getGame().getName().equals(name)) {
                currPlaying = cp;
                currPlayings.remove(cp);
                break;
            }
        }
        
        if (currPlaying == null) return;
        
        Played played = null;
        boolean persist = false;
        
        if (currPlaying.getTotalWager() != BigDecimal.ZERO) {
            persist = true;
            played = new Played();
            played.setIDSes(Account.getSession());
            played.setIDGame(currPlaying.getGame());

            played.setStartTime(currPlaying.getStartTime());
            played.setEndTime(new Date());
            long duration = played.getEndTime().getTime() - played.getStartTime().getTime();
            played.setDuration(new Date(duration - 3600000));    // 1 sat = 3 600 000 milisekundi

            played.setTotalWager(currPlaying.getTotalWager());
            played.setTotalReceived(currPlaying.getTotalReceived());
            BigDecimal profit = played.getTotalReceived().subtract(played.getTotalWager());
            played.setProfit(profit);
        }
        
        try {
            Account.getEntityManager().getTransaction().begin();
            if (persist) Account.getEntityManager().persist(played);
            Account.getEntityManager().remove(currPlaying);
            Account.getEntityManager().getTransaction().commit();
        }
        finally {
            if (Account.getEntityManager().getTransaction().isActive()) Account.getEntityManager().getTransaction().rollback();
        }
    }
    

    private MenuBar createMenuBar(Stage primaryStage) {
        MenuBar mb = new MenuBar();

        Menu fileMenu = new Menu("File");

        // Menu item for adding balance
        MenuItem balanceMenuItem = new MenuItem("Add balance");
        balanceMenuItem.setOnAction(e -> {
            ChangeBalance.changeBalance(Account.TransactionType.DEPOSIT);
        });
        fileMenu.getItems().add(balanceMenuItem);
        
        // Menu item for withdraw
        MenuItem withdrawMenuItem = new MenuItem("Withdraw");
        withdrawMenuItem.setOnAction(e -> {
            ChangeBalance.changeBalance(Account.TransactionType.WITHDRAW);
        });
        fileMenu.getItems().add(withdrawMenuItem);

        // Menu item for log out
        MenuItem logOutMenuItem = new MenuItem("Log out");
        logOutMenuItem.setOnAction(e -> {
            LogOut.logOut();
        });
        fileMenu.getItems().add(logOutMenuItem);

        // Menu item for exit from program
        MenuItem exitMenuItem = new MenuItem("Exit");
        exitMenuItem.setOnAction(e -> {
            onExit(primaryStage);
        });
        fileMenu.getItems().add(exitMenuItem);

        mb.getMenus().add(fileMenu);

        return mb;
    }

    
    private void gamePick() {
        // Creating panel for game pick
        gamePickPane = new BorderPane();
        gamePickPane.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, null, null)));
        gamePickPane.setBorder(new Border(new BorderStroke(Color.BLUEVIOLET, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));

        // Creating label for title
        Label title = new Label(" SELECT GAME ");
        title.setFont(Font.font("", FontWeight.BOLD, 100));
        title.setTextFill(Color.BLACK);
        title.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        title.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));

        // Creating "LOG OUT" button
        Button logOutButton = new Button("LOG OUT");
        logOutButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        logOutButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        logOutButton.setFont(Font.font("", FontWeight.BOLD, 15));
        logOutButton.setTextFill(Color.NAVAJOWHITE);
        logOutButton.setTextAlignment(TextAlignment.CENTER);
        logOutButton.setPrefSize(120, 50);
        logOutButton.setOnAction(e -> {
            LogOut.logOut();
        });

        // Creating main container for game pick
        HBox hbox = new HBox();
        hbox.setAlignment(Pos.CENTER_LEFT);

        // Book of Golden Sands container
        VBox vboxBookOfGoldenSands = new VBox();
        vboxBookOfGoldenSands.setAlignment(Pos.CENTER);

        // Creating label for game name
        Label BookOfGoldenSandsLabel = new Label("Book of the\nGolden Sands");
        BookOfGoldenSandsLabel.setFont(Font.font("", FontWeight.BOLD, 20));
        BookOfGoldenSandsLabel.setTextFill(Color.BLACK);
        BookOfGoldenSandsLabel.setTextAlignment(TextAlignment.CENTER);

        // Creating "PLAY" button
        Button BookOfGoldenSandsButton = new Button("PLAY");
        BookOfGoldenSandsButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        BookOfGoldenSandsButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        BookOfGoldenSandsButton.setFont(Font.font("", FontWeight.BOLD, 15));
        BookOfGoldenSandsButton.setTextFill(Color.NAVAJOWHITE);
        BookOfGoldenSandsButton.setTextAlignment(TextAlignment.CENTER);
        BookOfGoldenSandsButton.setPrefSize(90, 40);
        BookOfGoldenSandsButton.setOnAction(e -> {
            setGame(BookOfGoldenSandsGame, BookOfGoldenSandsPane);
        });

        // Adding game name and "PLAY" button into Book of Golden Sands container
        vboxBookOfGoldenSands.getChildren().addAll(BookOfGoldenSandsLabel, BookOfGoldenSandsButton);

        // Sugar Rush container
        VBox vboxSugarRush = new VBox();
        vboxSugarRush.setAlignment(Pos.CENTER);

        // Creating label for game name
        Label SugarRushLabel = new Label("\nSugar Rush");
        SugarRushLabel.setFont(Font.font("", FontWeight.BOLD, 20));
        SugarRushLabel.setTextFill(Color.BLACK);

        // Creating "PLAY" button
        Button SugarRushButton = new Button("PLAY");
        SugarRushButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        SugarRushButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        SugarRushButton.setFont(Font.font("", FontWeight.BOLD, 15));
        SugarRushButton.setTextFill(Color.NAVAJOWHITE);
        SugarRushButton.setTextAlignment(TextAlignment.CENTER);
        SugarRushButton.setPrefSize(90, 40);
        SugarRushButton.setOnAction(e -> {
            setGame(SugarRushGame, SugarRushPane);
        });

        // Adding game name and "PLAY" button into Sugar Rush container
        vboxSugarRush.getChildren().addAll(SugarRushLabel, SugarRushButton);

        // Coming soon container
        VBox vboxComingSoon = new VBox();
        vboxComingSoon.setAlignment(Pos.CENTER);
        
        // Creating label for game name
        Label comingSoonLabel = new Label("\nComing soon...");
        comingSoonLabel.setFont(Font.font("", FontWeight.BOLD, 20));
        comingSoonLabel.setTextFill(Color.BLACK);
        
        // Creating "PLAY" button
        Button comingSoonButton = new Button("PLAY");
        comingSoonButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        comingSoonButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        comingSoonButton.setFont(Font.font("", FontWeight.BOLD, 15));
        comingSoonButton.setTextFill(Color.NAVAJOWHITE);
        comingSoonButton.setTextAlignment(TextAlignment.CENTER);
        comingSoonButton.setPrefSize(90, 40);
        comingSoonButton.setDisable(true);
        
        // Adding game name and "PLAY" button into Coming soon container
        vboxComingSoon.getChildren().addAll(comingSoonLabel, comingSoonButton);
        
        // Adding all containers into main container for game pick
        hbox.getChildren().addAll(vboxBookOfGoldenSands, new Label("\t\t"), vboxSugarRush, new Label("\t\t"), vboxComingSoon);

        // Setting content in game pick panel
        gamePickPane.setTop(title);
        gamePickPane.setCenter(hbox);
        gamePickPane.setBottom(logOutButton);

        // Setting padding and alignment of label for title
        gamePickPane.setPadding(new Insets(30, 30, 30, 30));
        BorderPane.setAlignment(title, Pos.CENTER);
    }

    
    private void startMenu(Stage primaryStage) {
        // Creating a start panel
        startMenuPane = new BorderPane();
        
        VBox startMenuPaneVBox = new VBox();
        startMenuPaneVBox.setAlignment(Pos.CENTER);
        startMenuPaneVBox.setBackground(new Background(new BackgroundFill(Color.CADETBLUE, null, null)));   // BURLYWOOD // CADETBLUE // POWDERBLUE
        startMenuPaneVBox.setBorder(new Border(new BorderStroke(Color.BLUEVIOLET, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));

        // Creating label for title
        Label title = new Label(" SCAM CASINO ");
        title.setFont(Font.font("", FontWeight.BOLD, 90));
        title.setTextFill(Color.BLACK);
        title.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        title.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));

        // Creating container for adding buttons
        HBox menuButtonsBox = new HBox();
        menuButtonsBox.setAlignment(Pos.CENTER);

        // Creating "Login" button
        Button loginButton = new Button("Login");
        loginButton.setBackground(new Background(new BackgroundFill(Color.DARKBLUE, null, null)));
        loginButton.setBorder(new Border(new BorderStroke(Color.BLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        loginButton.setFont(Font.font("", FontWeight.BOLD, 35));
        loginButton.setTextFill(Color.NAVAJOWHITE);
        loginButton.setTextAlignment(TextAlignment.CENTER);
        loginButton.setPrefSize(230, 160);
        loginButton.setOnAction(e -> {
            Login.login();
        });

        // Creating "Register" button
        Button registerButton = new Button("Register");
        registerButton.setBackground(new Background(new BackgroundFill(Color.DARKGREEN, null, null)));
        registerButton.setBorder(new Border(new BorderStroke(Color.LIMEGREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        registerButton.setFont(Font.font("", FontWeight.BOLD, 35));
        registerButton.setTextFill(Color.NAVAJOWHITE);
        registerButton.setTextAlignment(TextAlignment.CENTER);
        registerButton.setPrefSize(230, 160);
        registerButton.setOnAction(e -> {
            Register.register();
        });

        // Creating "Delete account" button
        Button deleteAccButton = new Button("Delete\naccount");
        deleteAccButton.setBackground(new Background(new BackgroundFill(Color.DARKCYAN, null, null)));
        deleteAccButton.setBorder(new Border(new BorderStroke(Color.CYAN, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        deleteAccButton.setFont(Font.font("", FontWeight.BOLD, 35));
        deleteAccButton.setTextFill(Color.NAVAJOWHITE);
        deleteAccButton.setTextAlignment(TextAlignment.CENTER);
        deleteAccButton.setPrefSize(230, 160);
        deleteAccButton.setOnAction(e -> {
            DeleteAccount.deleteAccount();
        });

        // Adding buttons into container
        menuButtonsBox.getChildren().addAll(loginButton, new Label("\t\t"), registerButton, new Label("\t\t"), deleteAccButton);

        // Creating "Exit" button
        Button exitButton = new Button("Exit");
        exitButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        exitButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        exitButton.setFont(Font.font("", FontWeight.BOLD, 35));
        exitButton.setTextFill(Color.NAVAJOWHITE);
        exitButton.setTextAlignment(TextAlignment.CENTER);
        exitButton.setPrefSize(800, 80);
        exitButton.setOnAction(e -> {
            onExit(primaryStage);
        });

        // Creating container for adding all buttons together
        VBox vbox = new VBox();
        vbox.setAlignment(Pos.CENTER);

        // Adding all buttons together
        vbox.getChildren().addAll(menuButtonsBox, new Label("\n"), exitButton);

        // Adding everything into start panel
        startMenuPaneVBox.getChildren().addAll(title, new Label("\n\n\n\n\n"), vbox, new Label("\n\n\n\n\n\n\n"));
        startMenuPane.setCenter(startMenuPaneVBox);
    }
    

    private static void onExit(Stage primaryStage) {
        LogOut.onLogOut(true);
        primaryStage.close();
    }

    
    @Override
    public void start(Stage primaryStage) {
        try {
            startMenu(primaryStage);
            gamePick();
            MenuBar menuBar = createMenuBar(primaryStage);

            root.setTop(menuBar);
            root.setCenter(startMenuPane);

            Scene scene = new Scene(root, 1280, 720);
            //scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
            primaryStage.setTitle("Casino");
            primaryStage.setScene(scene);
            primaryStage.setOnCloseRequest(e -> {
                onExit(primaryStage);
            });
            primaryStage.show();
        } catch (Exception e) {
            System.err.println("ERROR!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

}
