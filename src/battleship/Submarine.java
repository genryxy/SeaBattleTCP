package battleship;

import java.io.Serializable;

public class Submarine extends Ship implements Serializable {
    /**
     * It's a constructor of the class.
     */
    public Submarine() {
        setLength(1);
        for (int i = 0; i < 4; i++) {
            getHit()[i] = false;
        }
    }

    /**
     * @return Returns the ship's type.
     */
    @Override
    public String getShipType() {
        return "submarine";
    }
}
