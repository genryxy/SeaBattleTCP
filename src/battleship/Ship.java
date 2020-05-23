package battleship;

public class Ship {
    private int bowRow;
    private int bowColumn;
    private int length;
    private boolean horizontal;
    private boolean[] hit = new boolean[4];

    /**
     * @return Returns the length of this particular ship.
     */
    public int getLength() {
        return length;
    }

    /**
     * @return Returns the value of bowRow.
     */
    public int getBowRow() {
        return bowRow;
    }

    /**
     * @return Returns the value of bowColumn.
     */
    public int getBowColumn() {
        return bowColumn;
    }

    /**
     * @return Returns the value of horizontal.
     */
    public boolean isHorizontal() {
        return horizontal;
    }

    /**
     * @return Returns the array of the hits.
     */
    public boolean[] getHit() {
        return hit;
    }

    /**
     * @param bowRow The row's value of the ship's bow.
     */
    public void setBowRow(int bowRow) {
        this.bowRow = bowRow;
    }

    /**
     * @param bowColumn The columns's value of the ship's bow.
     */
    public void setBowColumn(int bowColumn) {
        this.bowColumn = bowColumn;
    }

    /**
     * @param length The value of the ship's length.
     */
    public void setLength(int length) {
        this.length = length;
    }

    /**
     * @param horizontal Is the ship's orientation vertical?
     */
    public void setHorizontal(boolean horizontal) {
        this.horizontal = horizontal;
    }

    /**
     * @return Returns the ship's type.
     */
    public String getShipType() {
        return "empty Sea";
    }

    /**
     * Checking to see if we can place the ship in that position.
     *
     * @param horizontal The ship's orientation.
     * @param ocean      Access to the ship's array.
     * @return Returns true if it is okay to put a ship with such parameters,
     * and returns false otherwise.
     */
    public boolean okToPlaceShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        if (row < 0 || row > ocean.SIZE - 1 || column < 0 || column > ocean.SIZE - 1) {
            return false;
        }
        if (horizontal && (column + length) >= ocean.SIZE) {
            return false;
        }
        if (!horizontal && (row + length) >= ocean.SIZE) {
            return false;
        }

        // Checks whether the ship can be placed so that it does not touch with those already located.
        int addRow = horizontal ? 1 : length;
        int addColumn = horizontal ? length : 1;
        for (int i = Math.max(0, row - 1); i <= Math.min(ocean.SIZE - 1, row + addRow); i++) {
            for (int j = Math.max(0, column - 1); j <= Math.min(ocean.SIZE - 1, column + addColumn); j++) {
                if (ocean.isOccupied(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * "Puts" the ship in the ocean.
     *
     * @param horizontal The ship's orientation.
     * @param ocean      Access to the ship's array.
     */
    public void placeShipAt(int row, int column, boolean horizontal, Ocean ocean) {
        setBowRow(row);
        setBowColumn(column);
        setHorizontal(horizontal);
        if (horizontal) {
            for (int i = column; i < column + getLength(); i++) {
                ocean.getShipsArray()[row][i] = this;
            }
        } else {
            for (int i = row; i < row + getLength(); i++) {
                ocean.getShipsArray()[i][column] = this;
            }
        }
    }

    /**
     * If a part of the ship occupies the given row and column, and the ship hasn't been sunk, mark
     * that part of the ship as "hit" (in the hit array, 0 indicates the bow) and return true,
     * otherwise return false.
     *
     * @return false - sunk ship (can't shoot at this cell)
     * true - shot at this cell is done
     */
    public boolean shootAt(int row, int column) {
        if (isSunk()) {
            return false;
        }
        if (horizontal) {
            hit[column - bowColumn] = true;
        } else {
            hit[row - bowRow] = true;
        }
        return true;
    }

    public boolean isAlreadyFired(int row, int column) {
        return horizontal ? hit[column - bowColumn] : hit[row - bowRow];
    }

    /**
     * @return Returns true if every part of the ship has been hit, false otherwise.
     */
    public boolean isSunk() {
        for (int i = 0; i < getLength(); i++) {
            if (!hit[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * @return Returns a single-character String to use in the Ocean's print method.
     */
    @Override
    public String toString() {
        if (isSunk()) {
            return "x";
        }
        return "S";
    }
}
