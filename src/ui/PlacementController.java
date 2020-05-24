package ui;

import battleship.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.List;

public class PlacementController {
    @FXML
    public Text txtHint;
    @FXML
    public Text txtReadyShips;
    @FXML
    public Text txtCurrOrientation;
    @FXML
    public GridPane gridBattleField;
    @FXML
    public Button btnStartGame;
    @FXML
    public Button btnRandomPut;
    @FXML
    public Button btnRevertLast;

    private String hint = "You need to place 10 ships on the field.\n Current ship: ";
    private List<String> placedShips = new ArrayList<>();
    private Ocean ocean;
    private boolean isHorizontal;
    private int numberReadyShips;
    private Ship[] ships = {new Battleship(), new Cruiser(), new Cruiser(), new Destroyer(), new Destroyer(),
            new Destroyer(), new Submarine(), new Submarine(), new Submarine(), new Submarine()};
    private Ship lastShip;
    private int lastRow;
    private int lastColumn;
    private boolean hasRevert = true;

    public PlacementController() {
        ocean = new Ocean(true);
    }

    /**
     * It sets values for the different element of the interface.
     */
    public void initializeAll() {
        setTxtCurrOrientation(isHorizontal);
        setTxtHint(hint + ships[numberReadyShips].getShipType() + "\nSize: " + ships[numberReadyShips].getLength());
        placedShips.add("List of ships that were placed:\n");
        updateTxtReadyShips();
        createField();
        btnStartGame.setDisable(true);
        btnRevertLast.setDisable(true);
    }

    /**
     * Initializes the field for putting ships. Creates and adds buttons and labels.
     */
    private void createField() {
        for (int i = 0; i < ocean.SIZE + 1; i++) {
            for (int j = 0; j < ocean.SIZE + 1; j++) {
                if (i == 0 && j == 0) {
                    // skip this cell
                } else if (i == 0) {
                    Label lbl = new Label(String.valueOf(j - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    gridBattleField.add(lbl, 0, j);
                } else if (j == 0) {
                    Label lbl = new Label(String.valueOf(i - 1));
                    GridPane.setHalignment(lbl, HPos.CENTER);
                    gridBattleField.add(lbl, i, 0);
                } else {
                    Button btn = Utils.createButton("");
                    btn.setOnAction(actionEvent -> {
                        clickListener(btn);
                    });
                    gridBattleField.add(btn, j, i);
                }
            }
        }
    }

    /**
     * @return Return the pair with the number ships that were put and
     * the instance of class Ocean with user's placement.
     */
    public Pair<Integer, Ocean> getOcean() {
        return new Pair<>(numberReadyShips, ocean);
    }

    /**
     * The method with the main logic of interaction with the user. Checks the number
     * of the ready ships, changes color.
     *
     * @param btn The instance of Button for determination of coordinates in GridPane.
     */
    private void clickListener(Button btn) {
        if (numberReadyShips == 10) {
            Dialogs.createAlertInfo("Start game", "You put all ships. You should start a game.");
            return;
        }
        // We need to subtract 1 because GridPane store labels in the
        // first row and in the first column.
        int row = GridPane.getRowIndex(btn) - 1;
        int column = GridPane.getColumnIndex(btn) - 1;

        if (ships[numberReadyShips].okToPlaceShipAt(row, column, isHorizontal, ocean)) {
            ships[numberReadyShips].placeShipAt(row, column, isHorizontal, ocean);
            if (isHorizontal) {
                // GridPane contains labels in the first row and in the first column (except
                // position [0,0]). Shape(11x11). Children store in ObservableList<Node>.
                for (int i = column; i < column + ships[numberReadyShips].getLength(); i++) {
                    Button tmpBtn = ((Button) gridBattleField.getChildren().get((row + 1) * ocean.SIZE + i + (row + 1)));
                    Utils.setBtnBackground(tmpBtn, Color.GRAY);
                }
            } else {
                for (int i = row; i < row + ships[numberReadyShips].getLength(); i++) {
                    Button tmpBtn = ((Button) gridBattleField.getChildren().get((i + 1) * ocean.SIZE + column + (i + 1)));
                    Utils.setBtnBackground(tmpBtn, Color.GRAY);
                }
            }
            lastShip = ships[numberReadyShips];
            lastRow = row;
            lastColumn = column;
            placedShips.add((numberReadyShips + 1) + ") " + ships[numberReadyShips].getShipType() + "\n");
            updateTxtReadyShips();
            btnRevertLast.setDisable(false);
            numberReadyShips++;
            if (numberReadyShips < 10) {
                setTxtHint(hint + ships[numberReadyShips].getShipType() + "\nSize: " + ships[numberReadyShips].getLength());
            } else {
                btnStartGame.setDisable(false);
                setTxtHint("It's a great work! \nAll ships were placed.");
            }
        } else {
            Dialogs.createAlertInfo("Impossible place", "You can't put a ship here. Please, try another place!");
        }
    }

    /**
     * Change orientation of the ship that should be put.
     */
    public void handleBtnChangeOrientation(ActionEvent actionEvent) {
        isHorizontal = !isHorizontal;
        setTxtCurrOrientation(isHorizontal);
    }

    /**
     * It reverts last allocation.
     *
     * @param actionEvent Some auxiliary info about action.
     */
    public void handleBtnRevertLast(ActionEvent actionEvent) {
        btnStartGame.setDisable(true);
        if (numberReadyShips > 0) {
            numberReadyShips--;
            placedShips.remove(placedShips.size() - 1);
            updateTxtReadyShips();
            setTxtHint(hint + ships[numberReadyShips].getShipType() + "\nSize: " + ships[numberReadyShips].getLength());
            ocean.removeShip(lastRow, lastColumn, lastShip);
            if (lastShip.isHorizontal()) {
                // GridPane contains labels in the first row and in the first column (except
                // position [0,0]). Shape(11x11). Children store in ObservableList<Node>.
                for (int i = lastColumn; i < lastColumn + lastShip.getLength(); i++) {
                    Button tmpBtn = ((Button) gridBattleField.getChildren().get((lastRow + 1) * ocean.SIZE + i + (lastRow + 1)));
                    Utils.setBtnBackground(tmpBtn, Color.LIGHTBLUE);
                }
            } else {
                for (int i = lastRow; i < lastRow + lastShip.getLength(); i++) {
                    Button tmpBtn = ((Button) gridBattleField.getChildren().get((i + 1) * ocean.SIZE + lastColumn + (i + 1)));
                    Utils.setBtnBackground(tmpBtn, Color.LIGHTBLUE);
                }
            }
            btnRevertLast.setDisable(true);
        }
    }

    /**
     * It closes window, when all ships are put.
     *
     * @param actionEvent Some auxiliary info about action.
     */
    public void handleBtnStartGame(ActionEvent actionEvent) {
        Stage stage = (Stage) btnStartGame.getScene().getWindow();
        stage.close();
    }

    /**
     * User can close the window at any time with random allocation.
     *
     * @param actionEvent Some auxiliary info about action.
     */
    public void handleBtnStartGameWithRandom(ActionEvent actionEvent) {
        Stage stage = (Stage) btnRandomPut.getScene().getWindow();
        stage.close();
    }

    /**
     * It sets text in the Text txtCurrOrientation from .fxml
     * Text depends on orientation.
     *
     * @param isHorizontal true - horizontal, false - vertical
     */
    private void setTxtCurrOrientation(boolean isHorizontal) {
        txtCurrOrientation.setText(isHorizontal ? "Current orientation: horizontal" :
                "Current orientation: vertical");
    }

    /**
     * It sets text in the Text txtHint from .fxml
     *
     * @param text The content of Text.
     */
    public void setTxtHint(String text) {
        txtHint.setText(text);
    }

    /**
     * It updates text in the Text txtReadyShips from .fxml
     */
    public void updateTxtReadyShips() {
        StringBuilder sb = new StringBuilder();
        for (String str : placedShips) {
            sb.append(str);
        }
        txtReadyShips.setText(sb.toString());
    }
}
