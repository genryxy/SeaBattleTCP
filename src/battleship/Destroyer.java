package battleship;

import java.io.Serializable;

public class Destroyer extends Ship implements Serializable {
    /**
     * It's a constructor of the class.
     */
    public Destroyer() {
        setLength(2);
        for (int i = 0; i < 4; i++) {
            getHit()[i] = false;
        }
    }

    /**
     * @return Returns the ship's type.
     */
    @Override
    public String getShipType() {
        return "destroyer";
    }
}
