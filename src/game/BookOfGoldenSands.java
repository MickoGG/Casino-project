package game;

import java.util.ArrayList;
import java.util.List;
import application.Utils;
import java.math.BigDecimal;
import java.util.Arrays;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;


public class BookOfGoldenSands extends BaseGame {

    private static final int ROWS = 3;
    private static final int COLUMNS = 6;
    private static final int FREE_SPINS = 10;
    private static final String WILD = "W";
    private static final String[] symbols = new String[]{"10", "B", "A", "J", "Q", "K", "7", "R", WILD};
    private final Label[][] table = new Label[ROWS][COLUMNS];

    private static final String[] symbolProbabilities = new String[] {
        "10", "10", "10", "10", "10",       // 5
        "B", "B", "B", "B", "B",            // 5
        "A", "A", "A", "A",                 // 4
        "J", "J", "J", "J",                 // 4
        "Q", "Q", "Q",                      // 3
        "K", "K", "K",                      // 3
        "7", "7",                           // 2
        "R", "R",                           // 2
        WILD                                // 1
    };      // Length: 29

    private static final int[] animationRepeatProbabilities = {
        4, 4, 
        5, 5, 5, 
        6, 6, 6, 
        7, 7, 
        8
    };
    
    private Label payLabel;
    private Label winLabel;
    private Label balanceLabel;
    private Label infoLabel;
    private Label symbolLabel;
    private Button bonusButton;
    private Button spinButton;

    private final BonusData bonusData = new BonusData();
    
    
    private class BonusData implements ResetVariables {
        // Default data
        private int leftSpins = FREE_SPINS;                                 // reset after bonus end
        private BigDecimal totalPayment = BigDecimal.ZERO;                  // reset after bonus end
        private BigDecimal payment = BigDecimal.ZERO;                       // reset after each spin
        private int wildCnt = 0;                                            // reset after each spin
        private String symbol;
        
        // Connection logic data
        private int numberOfAppearances = 0;                                // reset after each spin
        private boolean[] symbolAppearsInColumn = new boolean[COLUMNS];     // reset after each spin
        
        @Override
        public void afterSpinReset() {
            payment = BigDecimal.ZERO;
            wildCnt = 0;
            numberOfAppearances = 0;
            symbolAppearsInColumn = new boolean[COLUMNS];
        }
        
        @Override
        public void afterBonusReset() {
            leftSpins = FREE_SPINS;
            totalPayment = BigDecimal.ZERO;
        }
    }
    
    
    @Override
    protected ResetVariables getResetVariables() {
        return bonusData;
    }

    
    @Override
    protected String getGameName() {
        return "BookOfGoldenSands";
    }
    
    
    @Override
    public void setBalanceLabel(BigDecimal newBalance) {
        balanceLabel.setText(" BALANCE: " + String.format("%.2f", newBalance) + "€ ");
    }
    
    
    @Override
    public BorderPane game() {
        GridPane gridPane = new GridPane();
        gridPane.setHgap(1);
        gridPane.setVgap(1);
        gridPane.setAlignment(Pos.CENTER);

        ColumnConstraints columnConstraints = new ColumnConstraints();
        //columnConstraints.setPercentWidth(100 / 6);
        columnConstraints.setPrefWidth(130);
        columnConstraints.setHalignment(HPos.CENTER);

        for (int i = 0; i < COLUMNS; i++) {
            gridPane.getColumnConstraints().add(columnConstraints);
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                Label label = new Label(symbolProbabilities[rand.nextInt(symbolProbabilities.length)]);
                table[i][j] = label;
                if (label.getText().equals(WILD)) {
                    label.setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
                    label.setTextFill(Color.GOLD);
                } else {
                    label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                    label.setTextFill(Color.BLACK);
                }
                label.setFont(Font.font("", FontWeight.BOLD, 55));
                label.setBackground(new Background(new BackgroundFill(Color.LIGHTSEAGREEN, null, null)));   // BISQUE, LIGHTSEAGREEN
                label.setAlignment(Pos.CENTER);
                label.setPrefSize(130, 130);

                gridPane.add(label, j, i);
            }
        }

        Label gameNameLabel = new Label(" Book of the Golden Sands ");
        gameNameLabel.setFont(Font.font("", FontWeight.BOLD, 50));
        gameNameLabel.setTextFill(Color.BLACK);
        gameNameLabel.setBackground(new Background(new BackgroundFill(Color.DARKORANGE, null, null)));
        gameNameLabel.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));

        HBox bottomGameBox = new HBox();

        payLabel = new Label();
        setPayLabel(BigDecimal.ZERO);
        payLabel.setFont(Font.font("", FontWeight.BOLD, 30));
        payLabel.setTextFill(Color.BLACK);
        payLabel.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        payLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        infoLabel = new Label();
        infoLabel.setFont(Font.font("", FontWeight.BOLD, 30));
        infoLabel.setTextFill(Color.BLACK);
        infoLabel.setBackground(new Background(new BackgroundFill(Color.DARKORANGE, null, null)));
        infoLabel.setBorder(new Border(new BorderStroke(Color.ORANGE, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        infoLabel.setPrefWidth(300);
        infoLabel.setAlignment(Pos.CENTER);

        Button gameInfo = new Button();
        gameInfo.setText("?");
        gameInfo.setFont(Font.font("", FontWeight.BOLD, 24));
        gameInfo.setShape(new Circle(2));
        gameInfo.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
        Tooltip tt = new Tooltip();
        tt.setText("Spins:\n3 wilds = free bonus\n\nBonus:\n3 wilds = +5 bonus spins");
        tt.setStyle("-fx-font: normal bold 20 Langdon; "
            + "-fx-base: #AE3522; "
            + "-fx-text-fill: orange;");
        gameInfo.setTooltip(tt);
        
        bottomGameBox.getChildren().addAll(new Label("\t\t\t\t "), infoLabel, new Label("\t\t "), payLabel, new Label("\t\t "), gameInfo);
        bottomGameBox.setAlignment(Pos.CENTER_LEFT);

        VBox gameBox = new VBox();
        gameBox.setAlignment(Pos.CENTER);
        gameBox.getChildren().addAll(gameNameLabel, new Label("\n"), gridPane, new Label("\n"), bottomGameBox);

        Label label1 = new Label(" YOUR SYMBOL: ");
        label1.setFont(Font.font("", FontWeight.BOLD, 22));
        label1.setTextFill(Color.BLACK);
        label1.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        //label1.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        String symbol = WILD;
        while (symbol.equals(WILD)) {
            symbol = symbols[rand.nextInt(symbols.length)];
        }
        symbolLabel = new Label(symbol);
        symbolLabel.setPrefSize(174, 160);
        symbolLabel.setAlignment(Pos.CENTER);
        symbolLabel.setFont(Font.font("", FontWeight.BOLD, 100));
        symbolLabel.setTextFill(Color.GRAY);
        symbolLabel.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        symbolLabel.setBorder(new Border(new BorderStroke(Color.GREEN, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));

        bonusButton = new Button("BUY\nFREE\nSPINS\n" + Math.round(chip * 100) + "€");
        bonusButton.setPrefSize(180, 200);
        bonusButton.setFont(Font.font("", FontWeight.BOLD, 29));
        bonusButton.setTextFill(Color.NAVAJOWHITE);
        bonusButton.setTextAlignment(TextAlignment.CENTER);
        bonusButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));   // DARKGREEN
        bonusButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        bonusButton.setOnAction(e -> {
            if (bonusActive) {
                return;
            }

            Alert bonusAlert = new Alert(AlertType.CONFIRMATION);
            bonusAlert.setTitle("Bonus");
            bonusAlert.setHeaderText("Do you want to buy bonus for " + Math.round(chip * 100) + "€ ?");

            ButtonType yesButtonType = new ButtonType("YES", ButtonBar.ButtonData.OK_DONE);
            ButtonType noButtonType = new ButtonType("NO", ButtonBar.ButtonData.CANCEL_CLOSE);
            bonusAlert.getButtonTypes().remove(ButtonType.OK);
            bonusAlert.getButtonTypes().remove(ButtonType.CANCEL);
            bonusAlert.getButtonTypes().addAll(yesButtonType, noButtonType);

            Button yesButton = (Button) bonusAlert.getDialogPane().lookupButton(yesButtonType);
            yesButton.setOnAction(e2 -> {
                int ret = onBonusBought(100);
                if (ret != 0) {
                    Alert alert = new Alert(AlertType.INFORMATION);
                    alert.setTitle("Wrong balance");
                    alert.setHeaderText("You don't have enough balance!");
                    alert.showAndWait();
                }
            });

            bonusAlert.showAndWait();
        });

        VBox bonusBox = new VBox();
        bonusBox.setPadding(new Insets(0, 0, 0, 100));
        bonusBox.setAlignment(Pos.CENTER);
        bonusBox.getChildren().addAll(label1, symbolLabel, new Label("\n\n\n"), bonusButton);

        Button backButton = new Button("BACK");
        backButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        backButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        backButton.setFont(Font.font("", FontWeight.BOLD, 20));
        backButton.setTextFill(Color.NAVAJOWHITE);
        backButton.setTextAlignment(TextAlignment.CENTER);
        backButton.setPrefSize(100, 40);
        backButton.setOnAction(e -> {
            onBack();
        });

        BorderPane leftSide = new BorderPane();
        leftSide.setCenter(bonusBox);
        leftSide.setTop(backButton);
        BorderPane.setMargin(backButton, new Insets(10));

        Label chipLabel = new Label(String.format("%.2f", chip) + "€");
        chipLabel.setFont(Font.font("", FontWeight.BOLD, 40));
        chipLabel.setTextAlignment(TextAlignment.CENTER);
        chipLabel.setTextFill(Color.BLACK);
        chipLabel.setBackground(new Background(new BackgroundFill(Color.GREEN, null, null)));
        //chipLabel.setBorder(new Border(new BorderStroke(Color.RED, Color.GREEN, Color.RED, Color.GREEN, BorderStrokeStyle.SOLID, null, BorderStrokeStyle.SOLID, null, null, new BorderWidths(2), null)));
        chipLabel.setPadding(new Insets(0, 5, 0, 5));

        Button decreaseButton = new Button("-");
        decreaseButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        decreaseButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        decreaseButton.setFont(Font.font("", FontWeight.BOLD, 20));
        decreaseButton.setTextFill(Color.NAVAJOWHITE);
        decreaseButton.setPrefSize(58, 58);
        decreaseButton.setOnAction(e -> {
            if (bonusActive) {
                return;
            }
            if (chip > 0.1 && chip <= 1) {
                chip -= 0.1;
            } else if (chip > 1 && chip <= 10) {
                chip -= 1;
            } else if (chip > 10 && chip <= 100) {
                chip -= 3;
            }
            chip = Utils.roundToNDec(chip, 2);
            chipLabel.setText(String.format("%.2f", chip) + "€");
            bonusButton.setText("BUY\nFREE\nSPINS\n" + Math.round(chip * 100) + "€");
        });

        Button increaseButton = new Button("+");
        increaseButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        increaseButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
        increaseButton.setFont(Font.font("", FontWeight.BOLD, 20));
        increaseButton.setTextFill(Color.NAVAJOWHITE);
        increaseButton.setPrefSize(58, 58);
        increaseButton.setOnAction(e -> {
            if (bonusActive) {
                return;
            }
            if (chip >= 0.1 && chip < 1) {
                chip += 0.1;
            } else if (chip >= 1 && chip < 10) {
                chip += 1;
            } else if (chip >= 10 && chip < 100) {
                chip += 3;
            }
            chip = Utils.roundToNDec(chip, 2);
            chipLabel.setText(String.format("%.2f", chip) + "€");
            bonusButton.setText("BUY\nFREE\nSPINS\n" + Math.round(chip * 100) + "€");
        });

        balanceLabel = new Label();
        setBalanceLabel(balance);
        balanceLabel.setFont(Font.font("", FontWeight.BOLD, 38));
        balanceLabel.setTextFill(Color.BLACK);
        balanceLabel.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        balanceLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        winLabel = new Label();
        setWinLabel(BigDecimal.ZERO);
        winLabel.setFont(Font.font("", FontWeight.BOLD, 38));
        winLabel.setTextFill(Color.BLACK);
        winLabel.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        winLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        spinButton = new Button("SPIN");
        spinButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));
        spinButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(3))));
        spinButton.setFont(Font.font("", FontWeight.BOLD, 40));
        spinButton.setTextFill(Color.NAVAJOWHITE);
        spinButton.setTextAlignment(TextAlignment.CENTER);
        spinButton.setPrefSize(155, 100);
        spinButton.setOnAction(e -> {
            if (skippableBonus) {
                skipBonus();
            }
            else if (!bonusActive) {
                onSpin();
            }
        });

        HBox infoBox = new HBox();
        infoBox.setPadding(new Insets(0, 0, 15, 0));
        infoBox.setAlignment(Pos.CENTER);
        infoBox.getChildren().addAll(decreaseButton, chipLabel, increaseButton, new Label("\t\t"), balanceLabel, new Label("\t\t"), winLabel, new Label("\t\t\t"), spinButton);

        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(gameBox);
        borderPane.setLeft(leftSide);
        borderPane.setBottom(infoBox);
        borderPane.setBackground(new Background(new BackgroundFill(Color.GOLDENROD, null, null)));

        return borderPane;
    }
    
    
    @Override
    public void spin() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                String currSymbol = symbolProbabilities[rand.nextInt(symbolProbabilities.length)];
                table[i][j].setText(currSymbol);
                
                if (currSymbol.equals(WILD)) {
                    table[i][j].setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
                    table[i][j].setTextFill(Color.GOLD);
                } else {
                    table[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                    table[i][j].setTextFill(Color.BLACK);
                }
            }
        }

        List<String> arrayList = new ArrayList<>();
        arrayList.addAll(Arrays.asList(symbols));
        arrayList.remove(WILD);

        BigDecimal payment = BigDecimal.ZERO;
        boolean found = false;
        ArrayList<Label> listOfFoundLabels = new ArrayList<>();

        for (int k = 0; k < arrayList.size(); k++) {
            int numberOfAppearances = 0;
            String symbol = arrayList.get(k);

            for (int i = 0; i < COLUMNS; i++) {
                for (int j = 0; j < ROWS; j++) {
                    if (table[j][i].getText().equals(symbol) || table[j][i].getText().equals(WILD)) {
                        if (found == false) {
                            numberOfAppearances++;
                            found = true;
                        }

                        if (!table[j][i].getText().equals(WILD)) {
                            listOfFoundLabels.add(table[j][i]);
                        }
                    }
                }

                found = false;
                if (numberOfAppearances != i + 1) {
                    break;
                }
            }

            if (numberOfAppearances < 3) {
                listOfFoundLabels.clear();
                continue;
            }

            for (Label label : listOfFoundLabels) {
                label.setTextFill(Color.DARKRED);
                label.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
            }

            listOfFoundLabels.clear();

            double payProcent = paymentOfSymbols(symbol, numberOfAppearances);
            payment = payment.add(bet.multiply(BigDecimal.valueOf(payProcent)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(4)));
        }

        // Proccess the payment
        processPayment(payment);
        
        // Update GUI
        setBalanceLabel(balance);
        setWinLabel(payment);

        // Check for wilds
        int wildCnt = 0;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (table[i][j].getText().equals(WILD)) {
                    wildCnt++;
                }
            }
        }

        if (wildCnt >= 3) {
            infoLabel.setText("BONUS WON!");
            onSpinBonus(100);
        }
    }

    
    /*
    -----------------------------
        BONUS METHODS - START
    -----------------------------
    */
    
    @Override
    protected void onBonusEnd() throws InterruptedException {
        // Proccess the payment
        processPayment(bonusData.totalPayment);
        
        // Update GUI
        if (!loggedOut) {
            Platform.runLater(() -> {
                setBalanceLabel(balance);
                bonusButton.setText("BUY\nFREE\nSPINS\n" + Math.round(chip * 100) + "€");
                spinButton.setText("SPIN");
                infoLabel.setText("");
                symbolLabel.setTextFill(Color.GRAY);

                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        if (table[i][j].getText().equals(WILD)) {
                            table[i][j].setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
                        }
                    }
                }

                sem.release();
            });

            // Waiting to be executed Platform.runLater()
            sem.acquire();
        }
    }
    
    
    @Override
    protected void onFirstSpin() throws InterruptedException {
        // Generate user symbol
        bonusData.symbol = WILD;
        while (bonusData.symbol.equals(WILD)) {
            bonusData.symbol = symbols[rand.nextInt(symbols.length)];
        }

        // Update GUI
        if (!loggedOut) {
            Platform.runLater(() -> {
                bonusButton.setText("FREE\nSPINS\nLEFT\n" + FREE_SPINS);
                infoLabel.setText("BONUS ACTIVE!");
                spinButton.setText("SKIP");
                symbolLabel.setText(bonusData.symbol);
                symbolLabel.setTextFill(Color.BLACK);

                // Setting color of all symbols on black (because maybe before purchase of bonus is spin connected so it changed the color of some fields), except wilds
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        if (!table[i][j].getText().equals(WILD)) {
                            table[i][j].setTextFill(Color.BLACK);
                        }
                        table[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                    }
                }

                sem.release();
            });

            // Waiting to be executed Platform.runLater()
            sem.acquire();
        }
    }
    
    
    @Override
    protected void animation() throws InterruptedException {
        int animationRepeat = animationRepeatProbabilities[rand.nextInt(animationRepeatProbabilities.length)];
        
        // Animation
        if (!loggedOut) {
            for (int k = 0; k < animationRepeat; k++) {
                Platform.runLater(() -> {
                    for (int i = 0; i < ROWS; i++) {
                        for (int j = 0; j < COLUMNS; j++) {
                            String currSymbol = symbolProbabilities[rand.nextInt(symbolProbabilities.length)];
                            table[i][j].setText(currSymbol);

                            if (currSymbol.equals(WILD)) {
                                table[i][j].setTextFill(Color.GOLD);
                            }
                            else {
                                table[i][j].setTextFill(Color.BLACK);
                            }
                        }
                    }

                    sem.release();
                });

                // Waiting to be executed Platform.runLater()
                sem.acquire();
                sleep(animationSleep);
            }
        }
    }
    
    
    @Override
    protected void connectionLogic() throws InterruptedException {
        // Spin counter
        bonusData.leftSpins--;
        
        // Update GUI
        if (!loggedOut) {
            Platform.runLater(() -> {
                if (bonusData.leftSpins == 0) {
                    bonusButton.setText("LAST\nFREE\nSPIN");
                } else {
                    bonusButton.setText("FREE\nSPINS\nLEFT\n" + bonusData.leftSpins);
                }

                sem.release();
            });

            // Waiting to be executed Platform.runLater()
            sem.acquire();
        }
        
        // Generation of symbols
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                // Generate symbols
                String currSymbol = symbolProbabilities[rand.nextInt(symbolProbabilities.length)];
                
                // Finding columns where user symbol appeared and finding number of wilds
                if (currSymbol.equals(bonusData.symbol)) {
                    bonusData.symbolAppearsInColumn[j] = true;
                }
                else if (currSymbol.equals(WILD)) {
                    bonusData.wildCnt++;
                }

                // Update GUI
                if (!loggedOut) {
                    final int ii = i;
                    final int jj = j;
                    Platform.runLater(() -> {
                        table[ii][jj].setText(currSymbol);

                        if (currSymbol.equals(bonusData.symbol)) {
                            table[ii][jj].setTextFill(Color.DARKRED);

                            table[0][jj].setBorder(new Border(new BorderStroke(Color.RED, Color.RED, Color.BLACK, Color.RED,
                                    BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(8, 3, 2, 3), null)));
                            table[1][jj].setBorder(new Border(new BorderStroke(Color.BLACK, Color.RED, Color.BLACK, Color.RED,
                                    BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(2, 3, 2, 3), null)));
                            table[2][jj].setBorder(new Border(new BorderStroke(Color.BLACK, Color.RED, Color.RED, Color.RED,
                                    BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, BorderStrokeStyle.SOLID, null, new BorderWidths(2, 3, 8, 3), null)));
                        } else if (currSymbol.equals(WILD)) {
                            table[ii][jj].setTextFill(Color.GOLD);
                        } else {
                            table[ii][jj].setTextFill(Color.BLACK);
                        }

                        sem.release();
                    });

                    // Waiting to be executed Platform.runLater()
                    sem.acquire();
                }
            }
        }

        // Connection logic
        for (int i = 0; i < COLUMNS; i++) {
            if (bonusData.symbolAppearsInColumn[i]) bonusData.numberOfAppearances++;
        }

        // Payment of user symbol if appeared more than or equal to 3 times
        if (bonusData.numberOfAppearances >= 3) {
            double payProcent = paymentOfSymbols(bonusData.symbol, bonusData.numberOfAppearances);
            bonusData.payment = bet.multiply(BigDecimal.valueOf(payProcent)).divide(BigDecimal.valueOf(100));
            bonusData.totalPayment = bonusData.totalPayment.add(bonusData.payment);
        }

        // Update GUI
        if (!loggedOut) {
            Platform.runLater(() -> {
                setPayLabel(bonusData.payment);
                setWinLabel(bonusData.totalPayment);
                sem.release();
            });

            // Waiting to be executed Platform.runLater()
            sem.acquire();
        }
        
        // Checking for number of wilds
        if (bonusData.wildCnt >= 3) {
            bonusData.leftSpins += 5;
        }
    }
    
    
    @Override
    protected void onNextSpin() throws InterruptedException {
        // Update of table parts containing user symbol (back to black frames and black text fill)
        if (!loggedOut) {
            Platform.runLater(() -> {
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        if (table[i][j].getTextFill() == Color.DARKRED) {
                            table[i][j].setTextFill(Color.BLACK);
                            for (int k = 0; k < ROWS; k++) {
                                table[k][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                            }
                        }
                    }
                }
                sem.release();
            });

            // Waiting to be executed Platform.runLater()
            sem.acquire();
        }
        
        // Check if bonus is over
        if (bonusData.leftSpins == 0) {
            pause = true;
        }
    }

    /*
    ---------------------------
        BONUS METHODS - END
    ---------------------------
    */
    
    
    /*
    -------------------------------
        HELPER METHODS - START
    -------------------------------
    */
    
    private void setPayLabel(BigDecimal value) {
        payLabel.setText(" PAY: " + String.format("%.2f", value) + "€ ");
    }

    
    private void setWinLabel(BigDecimal value) {
        winLabel.setText(" WIN: " + String.format("%.2f", value) + "€ ");
    }
    
    
    private double paymentOfSymbols(String symbol, int numberOfAppearances) {
        // (symbol: number_of_appearances) 10: 5 , B: 5 , A: 4 , J: 4 , Q: 3 , K: 3 , 7: 2 , R: 2
        
        double payProcent = 0;
        
        switch (symbol) {
            case "10": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 15;
                        break;
                    case 4:
                        payProcent = 25;
                        break;
                    case 5:
                        payProcent = 45;
                        break;
                    case 6:
                        payProcent = 85;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "B": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 15;
                        break;
                    case 4:
                        payProcent = 25;
                        break;
                    case 5:
                        payProcent = 45;
                        break;
                    case 6:
                        payProcent = 85;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "A": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 25;
                        break;
                    case 4:
                        payProcent = 40;
                        break;
                    case 5:
                        payProcent = 65;
                        break;
                    case 6:
                        payProcent = 115;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "J": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 25;
                        break;
                    case 4:
                        payProcent = 40;
                        break;
                    case 5:
                        payProcent = 65;
                        break;
                    case 6:
                        payProcent = 115;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "Q": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 35;
                        break;
                    case 4:
                        payProcent = 55;
                        break;
                    case 5:
                        payProcent = 100;
                        break;
                    case 6:
                        payProcent = 180;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "K": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 35;
                        break;
                    case 4:
                        payProcent = 55;
                        break;
                    case 5:
                        payProcent = 100;
                        break;
                    case 6:
                        payProcent = 180;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "7": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 50;
                        break;
                    case 4:
                        payProcent = 80;
                        break;
                    case 5:
                        payProcent = 180;
                        break;
                    case 6:
                        payProcent = 300;
                        break;
                    default:
                        break;
                }
                break;
            }
            case "R": {
                switch (numberOfAppearances) {
                    case 3:
                        payProcent = 50;
                        break;
                    case 4:
                        payProcent = 80;
                        break;
                    case 5:
                        payProcent = 180;
                        break;
                    case 6:
                        payProcent = 300;
                        break;
                    default:
                        break;
                }
                break;
            }
            default: {
                break;
            }
        }

        return payProcent;
    }

    /*
    -----------------------------
        HELPER METHODS - END
    -----------------------------
    */
    
}
