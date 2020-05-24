package ui;

import battleship.*;
import com.sun.webkit.network.Util;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.util.Pair;

public class PlacementController {
    @FXML
    public Text txtHint;
    @FXML
    public Text txtReadyShips;
    @FXML
    public Text txtCurrOrientation;
    @FXML
    public GridPane gridBattleField;

    private String hint = "You need to place 10 ships on the field.\n Current ship: ";
    private StringBuilder placedShips = new StringBuilder();
    private Ocean ocean;
    private boolean isHorizontal;
    private int numberReadyShips;
    private Ship[] ships = {new Battleship(), new Cruiser(), new Cruiser(), new Destroyer(), new Destroyer(),
            new Destroyer(), new Submarine(), new Submarine(), new Submarine(), new Submarine()};

    public PlacementController() {
        ocean = new Ocean();
    }

    public void initializeAll() {
        setTxtCurrOrientation(isHorizontal);
        setTxtHint(hint + ships[numberReadyShips].getShipType() + "\nSize: " + ships[numberReadyShips].getLength());
        placedShips.append("List of ships that were placed:\n");
        setTxtReadyShips(placedShips.toString());
        createField();
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

//        System.out.println(row + ", " + column);
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
            placedShips.append(numberReadyShips + 1)
                    .append(") ")
                    .append(ships[numberReadyShips].getShipType())
                    .append("\n");
            setTxtReadyShips(placedShips.toString());
            numberReadyShips++;
            if (numberReadyShips < 10) {
                setTxtHint(hint + ships[numberReadyShips].getShipType() + "\nSize: " + ships[numberReadyShips].getLength());
            } else {
                setTxtHint("It's a great work! \nAll ships were placed.");
            }
        } else {
            Dialogs.createAlertInfo("Impossible place", "You can't put a ship here. Please, try another place!");
        }
    }

    /**
     * Place all ten ships randomly on the (initially empty) ocean.
     */
    public void handleBtnChangeOrientation(ActionEvent actionEvent) {
        isHorizontal = !isHorizontal;
        setTxtCurrOrientation(isHorizontal);
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
     * It sets text in the Text txtReadyShips from .fxml
     *
     * @param text The content of Text.
     */
    public void setTxtReadyShips(String text) {
        txtReadyShips.setText(text);
    }
}
