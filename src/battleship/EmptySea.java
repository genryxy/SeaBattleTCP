package battleship;

public class EmptySea extends Ship {
    public EmptySea() {
        setLength(1);
        getHit()[0] = false;
    }

    @Override
    public boolean shootAt(int row, int column) {
        getHit()[0] = true;
        return false;
    }

    /**
     * @return Returns true if every part of the ship has been hit, false otherwise.
     * In this case always return false.
     */
    @Override
    public boolean isSunk() {
        return false;
    }

    /**
     * @return Returns a single-character String to use in the Ocean's print method.
     */
    @Override
    public String toString() {
        return "-";
    }
}
