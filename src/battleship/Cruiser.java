package battleship;

public class Cruiser extends Ship {
    /**
     * It's a constructor of the class.
     */
    public Cruiser() {
        setLength(3);
        for (int i = 0; i < 4; i++) {
            getHit()[i] = false;
        }
    }

    /**
     * @return Returns the ship's type.
     */
    @Override
    public String getShipType() {
        return "cruiser";
    }
}
