package ui;

import battleship.Ocean;
import javafx.stage.Stage;
import javafx.util.Pair;

public class PlacementStage extends Stage {
    /**
     * Get some values after closing the window. In this case window
     * has info about user's placement of the ships.
     *
     * @param controller The controller of the stage.
     * @return pair (number put ships, instance of ocean)
     */
    public Pair<Integer, Ocean> showAndReturn(PlacementController controller) {
        super.showAndWait();
        return controller.getOcean();
    }
}

