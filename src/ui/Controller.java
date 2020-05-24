package ui;

import battleship.Ocean;
import battleship.Ship;
import connection.NetworkConnection;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;


public class Controller {
    private Ocean ocean = new Ocean(true);
    private Ocean tmpOcean;
    private int x;
    private int y;
    private StringBuilder myMoveLoggingMsg = new StringBuilder();
    private NetworkConnection connection;
    private Boolean isGotAnswer;
    private Boolean isServer;
    private boolean hasOpponent;
    private boolean wasSend;

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
    public Button btnEndGame;

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

    /**
     * Creates instance of Ocean and places ships.
     * Adds buttons to the GridPane.
     */
    public void initializeOcean(Ocean createdOcean) {
        if (createdOcean == null) {
            tmpOcean = new Ocean(true);
            tmpOcean.placeAllShipsRandomly();
            Dialogs.createAlertInfo("Randomly placement", "Ships were put by random");
        } else {
            tmpOcean = createdOcean;
        }

        // Waiting for a new thread to be created
        while (!isServer && hasOpponent && !connection.isCreated()) ;
        if (!isServer) {
            wasSend = true;
            sendInfo(tmpOcean.getShipsArray(), null, true);
            sendInfo("Client", null, true);
        }
        initOpponentField();
        initMyField();

        getTxtXCoord().requestFocus();
        setTxtInfo(createInfoTextAboutGame());
    }

    public void setOcean(Ocean ocean) {
        this.ocean = ocean;
    }

    /**
     * @return Temporary ocean, before exchange.
     */
    public Ocean getTmpOcean() {
        return tmpOcean;
    }

    /**
     * It sets connection of the server and of the client.
     *
     * @param connection The instance of client or server.
     */
    public void setConnection(NetworkConnection connection) {
        this.connection = connection;
    }

    /**
     * Initializes the field of the opponent. Creates and adds buttons and labels.
     */
    private void initOpponentField() {
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
                    Button btn = Utils.createButton("");
                    btn.setOnAction(actionEvent -> {
                        clickListener(btn);
                    });
                    getGridBattleFieldOpponent().add(btn, j, i);
                }
            }
        }
    }

    /**
     * Initializes the field of the current user. Creates and adds buttons and labels.
     */
    private void initMyField() {
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
                    getGridBattleFieldMe().add(Utils.createButton(""), j, i);
                }
            }
        }
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
        if (!isGotAnswer || !hasOpponent) {
            Dialogs.createAlertInfo("Patience", "You must wait for the opponent to make a move");
            return;
        }
        Button btn = ((Button) getGridBattleFieldOpponent().getChildren().get((row + 1) * ocean.SIZE + column + (row + 1)));
        btn.fire();
        sendInfo(row + "," + column + "," + btn.getText(), btn, false);
    }

    /**
     * It marks area after opponent's shot. Sets the content of the button.
     * Changes its background color.
     *
     * @param row    Coordinate Y of the ship.
     * @param column Coordinate X of the ship.
     * @param text   Content of the button with such coordinates.
     */
    public void markShotFromOpponent(int row, int column, String text) {
        // GridPane contains labels in the first row and in the first column (except
        // position [0,0]). Shape(11x11). Children store in ObservableList<Node>.
        Button btn = ((Button) getGridBattleFieldMe().getChildren().get((row + 1) * ocean.SIZE + column + (row + 1)));
        btn.fire();
        btn.setText(text);
        if (text.equals("x")) {
            Utils.setBtnBackground(btn, Color.RED);
            // Opponent should continue
            setGotAnswer(false);
        } else if (text.equals("S")) {
            Utils.setBtnBackground(btn, Color.TOMATO);
            // Opponent should continue
            setGotAnswer(false);
        } else {
            Utils.setBtnBackground(btn, Color.LIGHTGRAY);
        }
    }

    /**
     * It sends info from client to server and from server to client.
     *
     * @param info        Sending string.
     * @param btn         Instance of button to check the content. null - otherwise
     * @param isAuxiliary false - it was shot, true - otherwise
     */
    public void sendInfo(Serializable info, Button btn, boolean isAuxiliary) {
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
     * The method with the main logic of interaction with the user. Checks end of the game,
     * takes shots, changes color, write actions of the user.
     *
     * @param btn The instance of Button for determination of coordinates in GridPane.
     */
    private void clickListener(Button btn) {
        if (!hasOpponent) {
            Dialogs.createAlertInfo("Patience", "You don't have an opponent. Please, wait!");
            return;
        }
        if (!ocean.isGameOver()) {
            // We need to subtract 1 because GridPane store labels in the
            // first row and in the first column.
            int row = GridPane.getRowIndex(btn) - 1;
            int column = GridPane.getColumnIndex(btn) - 1;
            if (!isGotAnswer) {
                Dialogs.createAlertInfo("Patience", "You must wait for the opponent to make a move");
                return;
            }

            if (ocean.getShipsArray()[row][column].isAlreadyFired(row, column)) {
                myMoveLoggingMsg.append("attempt to shoot the marked cell\n");
                Dialogs.createAlertInfo("Warning", "You've already fired at this cell. Please, try to shoot at another cell.");
            } else {
                if (ocean.shootAt(row, column)) {
                    Utils.setBtnBackground(btn, Color.TOMATO);
                } else {
                    Utils.setBtnBackground(btn, Color.SILVER);
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
        ocean = new Ocean(true);
        ocean.placeAllShipsRandomly();
        for (Object obj : getGridBattleFieldOpponent().getChildren()) {
            if (obj instanceof Button) {
                Button btn = (Button) obj;
                btn.setText("");
                Utils.setBtnBackground(btn, Color.LIGHTBLUE);
            }
        }
        for (Object obj : getGridBattleFieldMe().getChildren()) {
            if (obj instanceof Button) {
                Button btn = (Button) obj;
                btn.setText("");
                Utils.setBtnBackground(btn, Color.LIGHTBLUE);
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
                Utils.setBtnBackground(currBtn, Color.SILVER);
                if (ocean.getShipsArray()[i][j].isSunk()) {
                    Utils.setBtnBackground(currBtn, Color.RED);
                }

                sendInfo(i + "," + j + "," + currBtn.getText(), null, true);
            }
        }
    }

    /**
     * @param gotAnswer true - got answer from opponent or opponent
     *                  had a successful shot,
     *                  false - otherwise
     */
    public void setGotAnswer(Boolean gotAnswer) {
        isGotAnswer = gotAnswer;
    }

    /**
     * It sets the value.
     *
     * @param hasOpponent true - has opponent, false - no
     */
    public void setHasOpponent(Boolean hasOpponent) {
        this.hasOpponent = hasOpponent;
    }

    /**
     * It sets the value.
     *
     * @param isServer true - server, false - client.
     */
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

    public boolean isWasSend() {
        return wasSend;
    }

    /**
     * It closes window/
     *
     * @param actionEvent Some auxiliary info about action.
     */
    public void handleBtnEndGame(ActionEvent actionEvent) {
        Stage stage = (Stage) btnEndGame.getScene().getWindow();
        stage.close();
    }
}