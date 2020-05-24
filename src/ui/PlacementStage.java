package ui;

import battleship.Ocean;
import javafx.stage.Stage;

public class PlacementStage extends Stage {
    public Ocean showAndReturn(PlacementController controller) {
        super.showAndWait();
        return controller.getOcean();
    }
}

