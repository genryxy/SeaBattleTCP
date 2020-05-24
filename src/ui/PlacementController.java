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

    public Ocean getOcean() {
        return ocean;
    }

    /**
     * The method with the main logic of interaction with the user. Checks the number
     * of the ready ships, changes color.
     *
     * @param btn The instance of Button for determination of coordinates in GridPane.
     */
    private void clickListener(Button btn) {
        if (numberReadyShips == 10) {
            Dialogs.createAlertPutAllShips();
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
            Dialogs.createAlertBadPlace();
        }
    }

    /**
     * Place all ten ships randomly on the (initially empty) ocean.
     */
    public void handleBtnChangeOrientation(ActionEvent actionEvent) {
        isHorizontal = !isHorizontal;
        setTxtCurrOrientation(isHorizontal);
    }

    private void setTxtCurrOrientation(boolean isHorizontal) {
        txtCurrOrientation.setText(isHorizontal ? "Current orientation: horizontal" :
                "Current orientation: vertical");
    }

    public void setTxtHint(String text) {
        txtHint.setText(text);
    }

    public void setTxtReadyShips(String text) {
        txtReadyShips.setText(text);
    }
}
