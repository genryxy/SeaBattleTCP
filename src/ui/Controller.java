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
import java.util.Optional;


public class Controller {
    private Ocean ocean;
    private int x;
    private int y;
    private StringBuilder loggingMsg = new StringBuilder();
    NetworkConnection connection;

    @FXML
    private TextField txtYCoord;
    @FXML
    private TextField txtXCoord;
    @FXML
    private Text txtLogging;
    @FXML
    private Text txtInfo;
    @FXML
    private GridPane gridBattleFieldRival;
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

    @FXML
    private void handleBtnPlayAgain() {
        createAlertPlayAgain(ocean.isGameOver());
    }

    /**
     * Creates instance of Ocean and places ships.
     * Adds buttons to the GridPane.
     */
    public void initializeAll(NetworkConnection connection) {
        this.connection = connection;
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        for (int i = 0; i < ocean.SIZE + 1; i++) {
            for (int j = 0; j < ocean.SIZE + 1; j++) {
                if (i == 0 && j == 0) {
                    // skip this cell
                } else if (i == 0) {
                    Label lbl = new Label(String.valueOf(j - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    getGridBattleFieldRival().add(lbl, 0, j);
                } else if (j == 0) {
                    Label lbl = new Label(String.valueOf(i - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    getGridBattleFieldRival().add(lbl, i, 0);
                } else {
                    getGridBattleFieldRival().add(createButton(""), j, i);
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
                    getGridBattleFieldMe().getChildren().get(getGridBattleFieldMe().getChildren().size() - 1).setDisable(true);
                }
            }
        }
        getTxtXCoord().requestFocus();
        setTxtInfo(createInfoTextAboutShot());
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
    private void doShootAtCoordinates(int row, int column) {
        // GridPane contains labels in the first row and in the first column (except
        // position [0,0]). Shape(11x11). Children store in ObservableList<Node>.
        try {
            connection.send(row + "," + column);
            System.out.println(row + "," + column);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ((Button) getGridBattleFieldRival().getChildren().get((row + 1) * ocean.SIZE + column + (row + 1))).fire();
    }

    /**
     * Creates a new instance of the button with specified parameters.
     * Also adds action listener.
     *
     * @param text The content of the button.
     * @return The new instance of button.
     */
    private Button createButton(String text) {
        Button btn = new Button(text);
        setBtnBackground(btn, Color.LIGHTBLUE);
        // Allows to resize the button.
        btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        btn.setOnAction(actionEvent -> {
            clickListener(btn);
        });
        return btn;
    }

    /**
     * The method with the main logic of interaction with the user. Checks end of the game,
     * takes shots, changes color, write actions of the user.
     *
     * @param btn The instance of Button for determination of coordinates in GridPane.
     */
    private void clickListener(Button btn) {
        if (!ocean.isGameOver()) {
            // We need to subtract 1 because GridPane store labels in the
            // first row and in the first column.
            int row = GridPane.getRowIndex(btn) - 1;
            int column = GridPane.getColumnIndex(btn) - 1;
            //try {
            //    connection.send(row + "," + column);
            //    System.out.println(row + "," + column);
            // } catch (IOException e) {
            //     e.printStackTrace();
            // }

            if (ocean.getShipsArray()[row][column].isAlreadyFired(row, column)) {
                loggingMsg.append("attempt to shoot the marked cell\n");
                createAlertRepeatedShot();
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
                setTxtInfo(createInfoTextAboutShot());
                loggingMsg.append(ocean.getShotsFired()).append(". Move: ").append(row)
                        .append(",").append(column).append("\n");
                loggingMsg.append(ocean.getInfoAboutShot());
                if (ocean.isGameOver()) {
                    createAlertPlayAgain(true);
                }
            }
            setTxtLogging(loggingMsg.toString());
        } else {
            createAlertPlayAgain(true);
        }
    }

    /**
     * Creates alert that confirms the end of the game. If user press 'ok',
     * he'll begin a new game. Otherwise nothing happens.
     *
     * @param isGameOver It defines the content of the alert (again or new game).
     */
    private void createAlertPlayAgain(boolean isGameOver) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Choice");
        alert.setHeaderText(null);
        if (isGameOver) {
            alert.setContentText("The game is over! Do you want to play again?");
        } else {
            alert.setContentText("Do you want to start a new game?");
        }

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            reset();
        }
    }

    /**
     * Creates a new instance of Ocean.Resets values of fields.
     * Reset the background color of the buttons.
     */
    private void reset() {
        ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        for (Object obj : getGridBattleFieldRival().getChildren()) {
            if (obj instanceof Button) {
                Button btn = (Button) obj;
                btn.setText("");
                setBtnBackground(btn, Color.LIGHTBLUE);
            }
        }
        setTxtInfo(createInfoTextAboutShot());
        loggingMsg = new StringBuilder();
        setTxtLogging(loggingMsg.toString());
    }

    /**
     * Creates alert with warning about repeating shot in the cell.
     */
    private void createAlertRepeatedShot() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setHeaderText(null);
        alert.setContentText("You've already fired at this cell. Please, try to shoot at another cell.");
        alert.showAndWait();
    }

    /**
     * @return Gives some information about the current game. (Number shots,
     * hits, undamaged ships, partially damaged, sunk)
     */
    private String createInfoTextAboutShot() {
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
                Button currBtn = (Button) getGridBattleFieldRival().getChildren().get((i + 1) * ocean.SIZE + j + (i + 1));
                currBtn.setText(ocean.getShipsArray()[i][j].toString());
                setBtnBackground(currBtn, Color.SILVER);
                if (ocean.getShipsArray()[i][j].isSunk()) {
                    setBtnBackground(currBtn, Color.RED);
                }
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

    /**
     * It sets text in the Text txtLogging from .fxml
     *
     * @param text The content of Text.
     */
    private void setTxtLogging(String text) {
        txtLogging.setText(text);
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
     * @return Return the reference on the GridPane (battlefield of the rival).
     */
    private GridPane getGridBattleFieldRival() {
        return gridBattleFieldRival;
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