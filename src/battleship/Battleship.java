package battleship;

public class Battleship extends Ship {
    /**
     * It's a constructor of the class.
     */
    public Battleship() {
        setLength(4);
        for (int i = 0; i < 4; i++) {
            getHit()[i] = false;
        }
    }

    /**
     * @return Returns the ship's type.
     */
    @Override
    public String getShipType() {
        return "battleship";
    }
}
