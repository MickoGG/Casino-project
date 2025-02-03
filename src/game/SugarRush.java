package game;

import application.Utils;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import javafx.util.Pair;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
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


public class SugarRush extends BaseGame {

    private static final int ROWS = 7;
    private static final int COLUMNS = 7;
    private static final int FREE_SPINS = 10;
    private static final String WILD = "W";
    private final Label[][] table = new Label[ROWS][COLUMNS];
    
    private static final String[] symbolProbabilities = new String[] {
        "10", "10", "10", "10", "10", "10", "10",       // 7
        "B", "B", "B", "B", "B", "B", "B",              // 7
        "A", "A", "A", "A", "A", "A",                   // 6
        "J", "J", "J", "J", "J", "J",                   // 6
        "Q", "Q", "Q", "Q", "Q",                        // 5
        "K", "K", "K", "K", "K",                        // 5
        "7", "7", "7", "7",                             // 4
        WILD                                            // 1
    };      // Length: 41
    
    private static final int[] animationRepeatProbabilities = {
        6, 6, 
        7, 7, 7, 
        8, 8, 8, 
        9, 9, 
        10
    };
    
    private static final Color[] COLORS = new Color[] {
        Color.RED, 
        Color.ORANGE, 
        Color.YELLOW, 
        Color.GREEN, 
        Color.LIGHTGREEN, 
        Color.BLUE, 
        Color.LIGHTBLUE, 
        Color.PURPLE, 
        Color.PINK
    };      // Length: 9
    
    private Label payLabel;
    private Label winLabel;
    private Label balanceLabel;
    private Label infoLabel;
    private final Label[] currentPayingsLabels = new Label[4];;
    private Button bonusButton;
    private Button spinButton;
    
    private final BonusData bonusData = new BonusData();
    
    
    private class BonusData implements ResetVariables {
        // Default data
        private int leftSpins = FREE_SPINS;                                         // reset after bonus end
        private BigDecimal totalPayment = BigDecimal.ZERO;                          // reset after bonus end
        private BigDecimal payment = BigDecimal.ZERO;                               // reset after each spin
        private int wildCnt = 0;                                                    // reset after each spin
        private String symbol;
        
        // Connection logic data
        private final String[][] stringTable = new String[ROWS][COLUMNS];
        private boolean[][] checked = new boolean[ROWS][COLUMNS];                   // reset after each spin
        private final Stack<Pair<Integer, Integer>> stack = new Stack<>();
        private final List<Label> listOfFoundLabels = new ArrayList<>();
        private int colorIndex = 0;                                                 // reset after each spin
        private int currentPayingsLabelIndex = currentPayingsLabels.length - 1;     // reset after each spin

        @Override
        public void afterSpinReset() {
            payment = BigDecimal.ZERO;
            wildCnt = 0;
            checked = new boolean[ROWS][COLUMNS];
            colorIndex = 0;
            currentPayingsLabelIndex = currentPayingsLabels.length - 1;
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
        return "SugarRush";
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
        columnConstraints.setPrefWidth(100);
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
                    label.setTextFill(Color.DARKGOLDENROD);
                } else {
                    label.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                    label.setTextFill(Color.BLACK);
                }
                label.setFont(Font.font("", FontWeight.BOLD, 34));
                label.setBackground(new Background(new BackgroundFill(Color.LIGHTSEAGREEN, null, null)));   // BISQUE, LIGHTSEAGREEN
                label.setAlignment(Pos.CENTER);
                label.setPrefSize(100, 100);

                gridPane.add(label, j, i);
            }
        }
        
        Label gameNameLabel = new Label(" Sugar\nRush ");
        gameNameLabel.setPrefSize(200, 150);
        gameNameLabel.setFont(Font.font("", FontWeight.BOLD, 50));
        gameNameLabel.setTextFill(Color.LIGHTBLUE);
        gameNameLabel.setAlignment(Pos.CENTER);
        gameNameLabel.setTextAlignment(TextAlignment.RIGHT);
        gameNameLabel.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        gameNameLabel.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(5))));

        VBox gameNameBox = new VBox();
        gameNameBox.setPrefSize(250, 250);
        gameNameBox.setAlignment(Pos.TOP_CENTER);
        gameNameBox.getChildren().addAll(new Label("\n\n"), gameNameLabel);
        
        HBox bottomGameBox = new HBox();

        payLabel = new Label();
        setPayLabel(BigDecimal.ZERO);
        payLabel.setFont(Font.font("", FontWeight.BOLD, 30));
        payLabel.setTextFill(Color.BLACK);
        payLabel.setBackground(new Background(new BackgroundFill(Color.YELLOWGREEN, null, null)));
        payLabel.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));

        infoLabel = new Label();
        infoLabel.setFont(Font.font("", FontWeight.BOLD, 30));
        infoLabel.setTextFill(Color.WHITE);
        infoLabel.setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
        infoLabel.setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(3))));
        infoLabel.setPrefWidth(300);
        infoLabel.setAlignment(Pos.CENTER);

        Button gameInfo = new Button();
        gameInfo.setText("?");
        gameInfo.setFont(Font.font("", FontWeight.BOLD, 24));
        gameInfo.setShape(new Circle(2));
        gameInfo.setBackground(new Background(new BackgroundFill(Color.CYAN, null, null)));
        Tooltip tt = new Tooltip();
        tt.setText("Spins:\n5 wilds = free bonus\n\nBonus:\n4 wilds = +5 bonus spins");
        tt.setStyle("-fx-font: normal bold 20 Langdon; "
            + "-fx-base: #AE3522; "
            + "-fx-text-fill: orange;");
        gameInfo.setTooltip(tt);
        
        bottomGameBox.getChildren().addAll(new Label("\t\t\t\t "), infoLabel, new Label("\t\t "), payLabel, new Label("\t\t "), gameInfo);
        bottomGameBox.setAlignment(Pos.CENTER_LEFT);
        
        VBox gameBox = new VBox();
        gameBox.setAlignment(Pos.CENTER);
        gameBox.getChildren().addAll(new Label("\n"), gridPane, new Label("\n"), bottomGameBox);

        VBox currentPayingsBox = new VBox();
        currentPayingsBox.setAlignment(Pos.CENTER);

        for (int i = 0; i < currentPayingsLabels.length; i++) {
            currentPayingsLabels[i] = new Label();
            currentPayingsLabels[i].setPrefSize(170, 25);
            currentPayingsLabels[i].setAlignment(Pos.CENTER);
            currentPayingsLabels[i].setFont(Font.font("", FontWeight.BOLD, 18));
            currentPayingsLabels[i].setTextFill(Color.LIGHTBLUE);
            currentPayingsLabels[i].setBackground(new Background(new BackgroundFill(Color.BLUE, null, null)));
            currentPayingsLabels[i].setBorder(new Border(new BorderStroke(Color.DARKBLUE, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
            currentPayingsBox.getChildren().add(currentPayingsLabels[i]);
        }

        bonusButton = new Button("BUY\nFREE\nSPINS\n" + Math.round(chip * 100) + "€");
        bonusButton.setPrefSize(150, 200);
        bonusButton.setFont(Font.font("", FontWeight.BOLD, 29));
        bonusButton.setTextFill(Color.NAVAJOWHITE);
        bonusButton.setTextAlignment(TextAlignment.CENTER);
        bonusButton.setBackground(new Background(new BackgroundFill(Color.DARKRED, null, null)));   // DARKGREEN
        bonusButton.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
        bonusButton.setOnAction(e -> {
            if (bonusActive) {
                return;
            }

            Alert bonusAlert = new Alert(Alert.AlertType.CONFIRMATION);
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
        bonusBox.getChildren().addAll(bonusButton, new Label("\n\n\n"), currentPayingsBox);

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
        borderPane.setRight(gameNameBox);
        borderPane.setLeft(leftSide);
        borderPane.setBottom(infoBox);
        borderPane.setBackground(new Background(new BackgroundFill(Color.PURPLE, null, null)));

        return borderPane;
    }

    
    @Override
    public void spin() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                String currSymbol = symbolProbabilities[rand.nextInt(symbolProbabilities.length)];

                if (currSymbol.equals(WILD)) {
                    table[i][j].setTextFill(Color.DARKGOLDENROD);
                    table[i][j].setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
                    table[i][j].setFont(Font.font("", FontWeight.BOLD, 34));
                }
                else if (table[i][j].getTextFill() != Color.BLACK) {
                    table[i][j].setTextFill(Color.BLACK);
                    table[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                    table[i][j].setFont(Font.font("", FontWeight.BOLD, 34));
                }

                table[i][j].setText(currSymbol);
            }
        }
        
        BigDecimal payment = BigDecimal.ZERO;
        boolean[][] checked = new boolean[ROWS][COLUMNS];
        Stack<Pair<Integer, Integer>> stack = new Stack<>();
        List<Label> listOfFoundLabels = new ArrayList<>();
        int wildCnt = 0;
        int colorIndex = 0;
        int currentPayingsLabelIndex = currentPayingsLabels.length - 1;
        
        for (Label label : currentPayingsLabels) {
            label.setText("");
        }
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (checked[i][j]) continue;
                else checked[i][j] = true;
                
                String symbol = table[i][j].getText();
                if (symbol.equals(WILD)) {
                    wildCnt++;
                    continue;
                }

                int numberOfAppearances = 0;
                stack.push(new Pair<>(i, j));

                while (!stack.empty()) {
                    Pair<Integer, Integer> current = stack.pop();
                    int p = current.getKey();
                    int q = current.getValue();

                    if (p != 0) {    // up
                        checkAround(p - 1, q, table[p - 1][q].getText(), symbol, checked, stack);
                    }
                    if (p != ROWS - 1) {    // down
                        checkAround(p + 1, q, table[p + 1][q].getText(), symbol, checked, stack);
                    }
                    if (q != 0) {    // left
                        checkAround(p, q - 1, table[p][q - 1].getText(), symbol, checked, stack);
                    }
                    if (q != COLUMNS - 1) {    // right
                        checkAround(p, q + 1, table[p][q + 1].getText(), symbol, checked, stack);
                    }

                    numberOfAppearances++;
                    listOfFoundLabels.add(table[p][q]);
                }

                // Payment if symbol appeared (next to each other) more than or equal to 5 times
                if (numberOfAppearances >= 5) {
                    // Payment
                    double payProcent = paymentOfSymbols(symbol, numberOfAppearances);
                    payment = payment.add(bet.multiply(BigDecimal.valueOf(payProcent)).divide(BigDecimal.valueOf(100)).multiply(BigDecimal.valueOf(4)));

                    // Marking connected symbols
                    for (Label label : listOfFoundLabels) {
                        label.setTextFill(COLORS[colorIndex]);
                        label.setBorder(new Border(new BorderStroke(COLORS[colorIndex], BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
                        label.setFont(Font.font("", FontWeight.BOLD, 40));
                    }
                    colorIndex++;

                    // Marking currently found symbols in side box
                    currentPayingsLabels[currentPayingsLabelIndex].setText("Connected " + numberOfAppearances + "x " + symbol);
                    if (--currentPayingsLabelIndex < 0) currentPayingsLabelIndex = currentPayingsLabels.length - 1;
                }

                listOfFoundLabels.clear();
            }
        }
        
        // Proccess the payment
        processPayment(payment);
        
        // Update GUI
        setBalanceLabel(balance);
        setWinLabel(payment);
        
        // Check for wilds
        if (wildCnt >= 5) {
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
         // Update GUI
        if (!loggedOut) {
            Platform.runLater(() -> {
                bonusButton.setText("FREE\nSPINS\nLEFT\n" + FREE_SPINS);
                infoLabel.setText("BONUS ACTIVE!");
                spinButton.setText("SKIP");

                // Setting color of all symbols on black (because maybe before purchase of bonus is spin connected so it changed the color of some fields), except wilds
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        if (!table[i][j].getText().equals(WILD)) {
                            table[i][j].setTextFill(Color.BLACK);
                            table[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                            table[i][j].setFont(Font.font("", FontWeight.BOLD, 34));
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
    protected void animation() throws InterruptedException {
        int animationRepeat = animationRepeatProbabilities[rand.nextInt(animationRepeatProbabilities.length)];
                
        if (!loggedOut) {
            for (int k = 0; k < animationRepeat; k++) {
                Platform.runLater(() -> {
                    for (int i = 0; i < ROWS; i++) {
                        for (int j = 0; j < COLUMNS; j++) {
                            String currSymbol = symbolProbabilities[rand.nextInt(symbolProbabilities.length)];

                            if (currSymbol.equals(WILD)) {
                                table[i][j].setTextFill(Color.DARKGOLDENROD);
                                table[i][j].setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
                            }
                            else if (table[i][j].getText().equals(WILD)) {
                                table[i][j].setTextFill(Color.BLACK);
                                table[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                            }

                            table[i][j].setText(currSymbol);
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
                }
                else {
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
                bonusData.stringTable[i][j] = currSymbol;

                // Update GUI
                if (!loggedOut) {
                    final int ii = i;
                    final int jj = j;
                    Platform.runLater(() -> {
                        if (currSymbol.equals(WILD)) {
                            table[ii][jj].setTextFill(Color.DARKGOLDENROD);
                            table[ii][jj].setBorder(new Border(new BorderStroke(Color.DARKGOLDENROD, BorderStrokeStyle.SOLID, null, new BorderWidths(6))));
                        }
                        else if (table[ii][jj].getText().equals(WILD)) {
                            table[ii][jj].setTextFill(Color.BLACK);
                            table[ii][jj].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                        }

                        table[ii][jj].setText(currSymbol);
                        sem.release();
                    });

                    // Waiting to be executed Platform.runLater()
                    sem.acquire();
                }
            }
        }

        // Update GUI
        if (!loggedOut) {
            Platform.runLater(() -> {
                for (Label label : currentPayingsLabels) {
                    label.setText("");
                }
                sem.release();
            });

            // Waiting to be executed Platform.runLater()
            sem.acquire();
        }

        // Connection logic
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLUMNS; j++) {
                if (bonusData.checked[i][j]) continue;
                else bonusData.checked[i][j] = true;

                bonusData.symbol = bonusData.stringTable[i][j];
                if (bonusData.symbol.equals(WILD)) {
                    bonusData.wildCnt++;
                    continue;
                }

                int numberOfAppearances = 0;
                bonusData.stack.push(new Pair<>(i, j));

                while (!bonusData.stack.empty()) {
                    Pair<Integer, Integer> current = bonusData.stack.pop();
                    int p = current.getKey();
                    int q = current.getValue();

                    if (p != 0) {    // up
                        checkAround(p - 1, q, bonusData.stringTable[p - 1][q], bonusData.symbol, bonusData.checked, bonusData.stack);
                    }
                    if (p != ROWS - 1) {    // down
                        checkAround(p + 1, q, bonusData.stringTable[p + 1][q], bonusData.symbol, bonusData.checked, bonusData.stack);
                    }
                    if (q != 0) {    // left
                        checkAround(p, q - 1, bonusData.stringTable[p][q - 1], bonusData.symbol, bonusData.checked, bonusData.stack);
                    }
                    if (q != COLUMNS - 1) {    // right
                        checkAround(p, q + 1, bonusData.stringTable[p][q + 1], bonusData.symbol, bonusData.checked, bonusData.stack);
                    }

                    numberOfAppearances++;
                    bonusData.listOfFoundLabels.add(table[p][q]);
                }

                // Payment if symbol appeared (next to each other) more than or equal to 5 times
                if (numberOfAppearances >= 5) {
                    // Payment
                    double payProcent = paymentOfSymbols(bonusData.symbol, numberOfAppearances);
                    BigDecimal currPayment = bet.multiply(BigDecimal.valueOf(payProcent)).divide(BigDecimal.valueOf(100));
                    bonusData.payment = bonusData.payment.add(currPayment);
                    bonusData.totalPayment = bonusData.totalPayment.add(currPayment);

                    // Update GUI (Marking connected symbols)
                    if (!loggedOut) {
                        Platform.runLater(() -> {
                            for (Label label : bonusData.listOfFoundLabels) {
                                label.setTextFill(COLORS[bonusData.colorIndex]);
                                label.setBorder(new Border(new BorderStroke(COLORS[bonusData.colorIndex], BorderStrokeStyle.SOLID, null, new BorderWidths(4))));
                                label.setFont(Font.font("", FontWeight.BOLD, 40));
                            }

                            sem.release();
                        });

                        // Waiting to be executed Platform.runLater()
                        sem.acquire();
                        
                        bonusData.colorIndex++;
                    }

                    // Update GUI (Marking currently found symbols in side box)
                    if (!loggedOut) {
                        final int finalNumberOfAppearances = numberOfAppearances;
                        Platform.runLater(() -> {
                            currentPayingsLabels[bonusData.currentPayingsLabelIndex].setText("Connected " + finalNumberOfAppearances + "x " + bonusData.symbol);
                            sem.release();
                        });

                        // Waiting to be executed Platform.runLater()
                        sem.acquire();
                        
                        if (--bonusData.currentPayingsLabelIndex < 0) bonusData.currentPayingsLabelIndex = currentPayingsLabels.length - 1;
                    }
                }

                bonusData.listOfFoundLabels.clear();
            }
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
        if (bonusData.wildCnt >= 4) {
            bonusData.leftSpins += 5;
        }
    }
    
    
    @Override
    protected void onNextSpin() throws InterruptedException {
        // Update for found symbols (back to black text fill, black frames, and default font)
        if (!loggedOut) {
            Platform.runLater(() -> {
                for (int i = 0; i < ROWS; i++) {
                    for (int j = 0; j < COLUMNS; j++) {
                        if (table[i][j].getTextFill() != Color.BLACK && !WILD.equals(table[i][j].getText())) {
                            table[i][j].setTextFill(Color.BLACK);
                            table[i][j].setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, null, new BorderWidths(2))));
                            table[i][j].setFont(Font.font("", FontWeight.BOLD, 34));
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
    ------------------------------
        HELPER METHODS - START
    ------------------------------
    */
    
    private void setPayLabel(BigDecimal value) {
        payLabel.setText(" PAY: " + String.format("%.2f", value) + "€ ");
    }

    
    private void setWinLabel(BigDecimal value) {
        winLabel.setText(" WIN: " + String.format("%.2f", value) + "€ ");
    }
    
    
    private void checkAround(int i, int j, String newSymbol, String symbol, boolean[][] checked, Stack<Pair<Integer, Integer>> stack) {
        if (symbol.equals(newSymbol) && !checked[i][j]) {
            checked[i][j] = true;
            stack.push(new Pair<>(i, j));
        }
    }
    
    
    private double paymentOfSymbols(String symbol, int numberOfAppearances) {
        // (symbol: number_of_appearances) 10: 7 , B: 7 , A: 6 , J: 6 , Q: 5 , K: 5 , 7: 4
        
        double weight = 0;
        
        switch (symbol) {
            case "10":
                weight = 3;
                break;
            case "B":
                weight = 3;
                break;
            case "A":
                weight = 6.5;
                break;
            case "J":
                weight = 6.5;
                break;
            case "Q":
                weight = 10;
                break;
            case "K":
                weight = 10;
                break;
            case "7":
                weight = 13.5;
                break;
            default:
                break;
        }

        return weight * 5 + (numberOfAppearances - 5) * weight / 2;     // return weight * numberOfAppearances;
    }

    /*
    ----------------------------
        HELPER METHODS - END
    ----------------------------
    */
    
}
