package ui;

import battleship.Ocean;
import battleship.Ship;
import connection.NetworkConnection;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.IOException;


public class Controller {
    private Ocean ocean;
    private int x;
    private int y;
    private StringBuilder myMoveLoggingMsg = new StringBuilder();
    private NetworkConnection connection;
    private Boolean isGotAnswer;
    private Boolean isServer;
    private Boolean hasOpponent;

    @FXML
    private TextField txtYCoord;
    @FXML
    private TextField txtXCoord;
    @FXML
    private Text txtLoggingMyMove;
    @FXML
    private Text txtLoggingOppMove;
    @FXML
    private Text txtInfo;
    @FXML
    private GridPane gridBattleFieldOpponent;
    @FXML
    private GridPane gridBattleFieldMe;

    @FXML
    private void handleBtnShoot() {
        if (checkInputValues()) {
            doShootAtCoordinates(x, y);
            getTxtXCoord().requestFocus();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Coordinates must be from [0,9]. Please, enter other values");
            alert.showAndWait();
        }
    }

//    @FXML
//    private void handleBtnPlayAgain() {
//        if (Dialogs.createAlertPlayAgain(ocean.isGameOver())) {
//            reset();
//        }
//    }

    /**
     * Creates instance of Ocean and places ships.
     * Adds buttons to the GridPane.
     */
    public void initializeAll(NetworkConnection connection) {
        this.connection = connection;
        // Waiting for a new thread to be created
        while (hasOpponent && !connection.isCreated()) ;
        if (hasOpponent) {
            sendInfo("Client", null, true);
        }
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        for (int i = 0; i < ocean.SIZE + 1; i++) {
            for (int j = 0; j < ocean.SIZE + 1; j++) {
                if (i == 0 && j == 0) {
                    // skip this cell
                } else if (i == 0) {
                    Label lbl = new Label(String.valueOf(j - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    getGridBattleFieldOpponent().add(lbl, 0, j);
                } else if (j == 0) {
                    Label lbl = new Label(String.valueOf(i - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    getGridBattleFieldOpponent().add(lbl, i, 0);
                } else {
                    Button btn = createButton("");
                    btn.setOnAction(actionEvent -> {
                        clickListener(btn);
                    });
                    getGridBattleFieldOpponent().add(btn, j, i);
                }
            }
        }

        for (int i = 0; i < ocean.SIZE + 1; i++) {
            for (int j = 0; j < ocean.SIZE + 1; j++) {
                if (i == 0 && j == 0) {
                    // skip this cell
                } else if (i == 0) {
                    Label lbl = new Label(String.valueOf(j - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    getGridBattleFieldMe().add(lbl, 0, j);
                } else if (j == 0) {
                    Label lbl = new Label(String.valueOf(i - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    getGridBattleFieldMe().add(lbl, i, 0);
                } else {
                    getGridBattleFieldMe().add(createButton(""), j, i);
                }
            }
        }
        getTxtXCoord().requestFocus();
        setTxtInfo(createInfoTextAboutGame());
    }

    /**
     * Checks the input values.
     *
     * @return true - valid input (x,y from [0,9]), false - otherwise
     */
    private boolean checkInputValues() {
        try {
            x = Integer.parseInt(txtXCoord.getCharacters().toString());
            y = Integer.parseInt(txtYCoord.getCharacters().toString());
            if (x < 0 || x > 9 || y < 0 || y > 9) {
                return false;
            }
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * This method allows to programmatically click on the button with certain coordinates.
     *
     * @param row    The index of row in GridPane.
     * @param column The index of column in GridPane.
     */
    public void doShootAtCoordinates(int row, int column) {
        // GridPane contains labels in the first row and in the first column (except
        // position [0,0]). Shape(11x11). Children store in ObservableList<Node>.
        if (!isGotAnswer && !hasOpponent) {
            Dialogs.createAlertOpponentMove();
            return;
        }
        Button btn = ((Button) getGridBattleFieldOpponent().getChildren().get((row + 1) * ocean.SIZE + column + (row + 1)));
        btn.fire();
        sendInfo(row + "," + column + "," + btn.getText(), btn, false);
    }

    public void markShotFromOpponent(int row, int column, String text) {
        // GridPane contains labels in the first row and in the first column (except
        // position [0,0]). Shape(11x11). Children store in ObservableList<Node>.
        Button btn = ((Button) getGridBattleFieldMe().getChildren().get((row + 1) * ocean.SIZE + column + (row + 1)));
        btn.fire();
        btn.setText(text);
        if (text.equals("x")) {
            setBtnBackground(btn, Color.RED);
            // Opponent should continue
            setGotAnswer(false);
        } else if (text.equals("S")) {
            setBtnBackground(btn, Color.TOMATO);
            // Opponent should continue
            setGotAnswer(false);
        } else {
            setBtnBackground(btn, Color.LIGHTGRAY);
        }
    }

    public void sendInfo(String info, Button btn, boolean isAuxiliary) {
        try {
            connection.send(info);
            if (!isAuxiliary && btn.getText().equals("-")) {
                setGotAnswer(false);
            }
//            System.out.println(info);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new instance of the button with specified parameters.
     *
     * @param text The content of the button.
     * @return The new instance of button.
     */
    private Button createButton(String text) {
        Button btn = new Button(text);
        setBtnBackground(btn, Color.LIGHTBLUE);
        // Allows to resize the button.
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        return btn;
    }

    /**
     * The method with the main logic of interaction with the user. Checks end of the game,
     * takes shots, changes color, write actions of the user.
     *
     * @param btn The instance of Button for determination of coordinates in GridPane.
     */
    private void clickListener(Button btn) {
        if (!hasOpponent) {
            Dialogs.createAlertNotOpponent();
            return;
        }
        if (!ocean.isGameOver()) {
            // We need to subtract 1 because GridPane store labels in the
            // first row and in the first column.
            int row = GridPane.getRowIndex(btn) - 1;
            int column = GridPane.getColumnIndex(btn) - 1;
            if (!isGotAnswer) {
                Dialogs.createAlertOpponentMove();
                return;
            }

            if (ocean.getShipsArray()[row][column].isAlreadyFired(row, column)) {
                myMoveLoggingMsg.append("attempt to shoot the marked cell\n");
                Dialogs.createAlertRepeatedShot();
            } else {
                if (ocean.shootAt(row, column)) {
                    setBtnBackground(btn, Color.TOMATO);
                } else {
                    setBtnBackground(btn, Color.SILVER);
                }
                btn.setText(ocean.getShipsArray()[row][column].toString());
                if (ocean.getShipsArray()[row][column].isSunk()) {
                    markAreaAroundShip(ocean.getShipsArray()[row][column]);
                }
                setTxtInfo(createInfoTextAboutGame());
                myMoveLoggingMsg.append(ocean.getShotsFired()).append(". Move: ").append(row)
                        .append(",").append(column).append("\n");
                myMoveLoggingMsg.append(ocean.getInfoAboutShot());

                sendInfo(row + "," + column + "," + btn.getText(), btn, false);

                if (ocean.isGameOver()) {
                    sendInfo("Winner: " + (isServer ? "Server\n" : "Client\n") + createInfoTextAboutGame(), null, true);
                }
            }
            setTxtLoggingMyMove(myMoveLoggingMsg.toString());
            sendInfo(myMoveLoggingMsg.toString(), null, true);

        } else {
            sendInfo("Winner: " + (isServer ? "Server\n" : "Client\n") + createInfoTextAboutGame(), null, true);
        }
    }

    /**
     * Creates a new instance of Ocean.Resets values of fields.
     * Reset the background color of the buttons.
     */
    public void reset() {
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        for (Object obj : getGridBattleFieldOpponent().getChildren()) {
            if (obj instanceof Button) {
                Button btn = (Button) obj;
                btn.setText("");
                setBtnBackground(btn, Color.LIGHTBLUE);
            }
        }
        for (Object obj : getGridBattleFieldMe().getChildren()) {
            if (obj instanceof Button) {
                Button btn = (Button) obj;
                btn.setText("");
                setBtnBackground(btn, Color.LIGHTBLUE);
            }
        }
        setTxtInfo(createInfoTextAboutGame());
        myMoveLoggingMsg = new StringBuilder();
        setTxtLoggingMyMove(myMoveLoggingMsg.toString());
        sendInfo(myMoveLoggingMsg.toString(), null, true);
    }

    /**
     * @return Gives some information about the current game. (Number shots,
     * hits, undamaged ships, partially damaged, sunk)
     */
    public String createInfoTextAboutGame() {
        return "Number shots: " + ocean.getShotsFired() +
                "\nNumber hits: " + ocean.getHitCount() +
                "\nUndamaged: " + (10 - ocean.getShipsSunk() - ocean.getShipWrecked()) +
                "\nPartially damaged: " + ocean.getShipWrecked() +
                "\nSunk: " + ocean.getShipsSunk();
    }

    /**
     * We will help player by marking the area around the sunken ship
     * to improve the game's usability.
     *
     * @param ship The instance of the sunken ship.
     */
    private void markAreaAroundShip(Ship ship) {
        int addRow = ship.isHorizontal() ? 1 : ship.getLength();
        int addColumn = ship.isHorizontal() ? ship.getLength() : 1;

        // GridPane contains labels in the first row and in the first column
        // (except position [0,0]). Shape(11x11).
        for (int i = Math.max(0, ship.getBowRow() - 1); i <= Math.min(ocean.SIZE - 1, ship.getBowRow() + addRow); i++) {
            for (int j = Math.max(0, ship.getBowColumn() - 1); j <= Math.min(ocean.SIZE - 1, ship.getBowColumn() + addColumn); j++) {
                // Children store in ObservableList<Node>.
                Button currBtn = (Button) getGridBattleFieldOpponent().getChildren().get((i + 1) * ocean.SIZE + j + (i + 1));
                currBtn.setText(ocean.getShipsArray()[i][j].toString());
                setBtnBackground(currBtn, Color.SILVER);
                if (ocean.getShipsArray()[i][j].isSunk()) {
                    setBtnBackground(currBtn, Color.RED);
                }

                sendInfo(i + "," + j + "," + currBtn.getText(), null, true);
            }
        }
    }

    /**
     * It sets the button's background. It helps to have the similar
     * values of corner radius and insets.
     *
     * @param btn   The instance of button for changing.
     * @param color The required color of background.
     */
    private void setBtnBackground(Button btn, Color color) {
        btn.setBackground(new Background(new BackgroundFill(color, new CornerRadii(3), new Insets(0.5))));
    }

    public void setGotAnswer(Boolean gotAnswer) {
        isGotAnswer = gotAnswer;
    }

    public void setHasOpponent(Boolean hasOpponent) {
        this.hasOpponent = hasOpponent;
    }

    public void setIsServer(Boolean isServer) {
        this.isServer = isServer;
    }


    /**
     * It sets text in the Text txtLoggingOppMove from .fxml
     *
     * @param text The content of Text.
     */
    public void setTxtLoggingOppMove(String text) {
        txtLoggingOppMove.setText(text);
    }

    /**
     * It sets text in the Text txtLoggingMyMove from .fxml
     *
     * @param text The content of Text.
     */
    private void setTxtLoggingMyMove(String text) {
        txtLoggingMyMove.setText(text);
    }

    /**
     * It sets text in the Text txtInfo from .fxml
     *
     * @param text The content of Text.
     */
    private void setTxtInfo(String text) {
        txtInfo.setText(text);
    }

    /**
     * @return Return the reference on the GridPane (battlefield of the opponent).
     */
    private GridPane getGridBattleFieldOpponent() {
        return gridBattleFieldOpponent;
    }

    /**
     * @return Return the reference on the GridPane (your battlefield).
     */
    private GridPane getGridBattleFieldMe() {
        return gridBattleFieldMe;
    }

    /**
     * @return Return the reference on the TextField of X coordinate.
     */
    private TextField getTxtXCoord() {
        return txtXCoord;
    }
}