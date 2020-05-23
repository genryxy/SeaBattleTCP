package battleship;

import java.util.Random;

public class Ocean {
    public final int SIZE = 10;

    private Ship[][] ships = new Ship[SIZE][SIZE];
    private int shotsFired;
    private int hitCount;
    private int shipsSunk;
    private int shipWrecked;
    private String typeSunkShip = "empty";
    private boolean isHit = false;

    /**
     * It's a constructor of the class.
     */
    public Ocean() {
        shotsFired = 0;
        hitCount = 0;
        shipsSunk = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                ships[i][j] = new EmptySea();
                ships[i][j].setBowRow(i);
                ships[i][j].setBowColumn(j);
            }
        }
    }

    /**
     * Place all ten ships randomly on the (initially empty) ocean.
     */
    public void placeAllShipsRandomly() {
        Random rnd = new Random();
        int x;
        int y;
        boolean isHorizontal;
        Ship[] ships = {new Battleship(), new Cruiser(), new Cruiser(), new Destroyer(), new Destroyer(),
                new Destroyer(), new Submarine(), new Submarine(), new Submarine(), new Submarine()};
        for (Ship ship : ships) {
            // Search free space for our ship.
            while (!ship.okToPlaceShipAt(x = rnd.nextInt(SIZE), y = rnd.nextInt(SIZE),
                    isHorizontal = rnd.nextBoolean(), this)) {
            }
            ship.placeShipAt(x, y, isHorizontal, this);
        }
    }

    /**
     * @param row    The value of the row.
     * @param column The value of the column.
     * @return Returns true if the given location contains a ship, false if it does not.
     */
    public boolean isOccupied(int row, int column) {
        return !(getShipsArray()[row][column].getShipType().equals("empty Sea"));
    }

    /**
     * If a location contains a "real" ship, shootAt should return true every time the user shoots
     * at that same location. Once a ship has been "sunk", additional shots at its location
     * should return false.
     *
     * @param row    The value of the row.
     * @param column The value of the column.
     * @return Returns true if the given location contains a "real" ship,
     * still afloat, (not an EmptySea), false if it does not.     *
     */
    public boolean shootAt(int row, int column) {
        shotsFired++;
        Ship ship = ships[row][column];
        if (ship.shootAt(row, column)) {
            checkWreckedShip(ship);
            hitCount++;
            isHit = true;
            if (ship.isSunk()) {
                shipWrecked--;
                shipsSunk++;
                typeSunkShip = ship.getShipType();
                // We will help player by marking the area around the sunken ship.
                int addRow = ship.isHorizontal() ? 1 : ship.getLength();
                int addColumn = ship.isHorizontal() ? ship.getLength() : 1;
                for (int i = Math.max(0, ship.getBowRow() - 1); i <= Math.min(SIZE - 1, ship.getBowRow() + addRow); i++) {
                    for (int j = Math.max(0, ship.getBowColumn() - 1);
                         j <= Math.min(SIZE - 1, ship.getBowColumn() + addColumn); j++) {
                        ships[i][j].shootAt(row, column);
                    }
                }
            }
            return true;
        }
        return false;
    }

    /**
     * We check if there are any hits on the ship (is wrecked?).
     *
     * @param ship Checking ship.
     */
    private void checkWreckedShip(Ship ship) {
        int numberHits = 0;
        for (Boolean hit : ship.getHit()) {
            if (hit) {
                numberHits++;
            }
        }
        // We should to count this ship only once.
        if (numberHits == 1) {
            shipWrecked++;
        }
    }

    /**
     * @return Returns the number of shots fired (in this game).
     */
    public int getShotsFired() {
        return shotsFired;
    }

    /**
     * @return Returns the number of hits recorded (in this game). All
     * hits are counted, not just the first time a given square is hit.
     */
    public int getHitCount() {
        return hitCount;
    }

    /**
     * @return Returns the number of ships sunk (in this game).
     */
    public int getShipsSunk() {
        return shipsSunk;
    }

    /**
     * @return Returns true if all ships have been sunk, otherwise false.
     */
    public boolean isGameOver() {
        return shipsSunk >= 10;
    }

    /**
     * @return Returns the 10x10 array of ships.
     */
    public Ship[][] getShipsArray() {
        return ships;
    }

    /**
     * @return Returns the number of wrecked ships (in this game).
     */
    public int getShipWrecked() {
        return shipWrecked;
    }

    /**
     * @return String with result of the latest shot (miss or hit? is sunk?).
     */
    public String getInfoAboutShot() {
        StringBuilder ans = new StringBuilder();
        if (isHit) {
            isHit = false;
            if (!typeSunkShip.equals("empty")) {
                ans.append("Sank a ").append(typeSunkShip).append("\n");
                typeSunkShip = "empty";
            } else {
                ans.append("hit\n");
            }
        } else if (shotsFired > 0) {
            ans.append("miss\n");
        }
        if (isGameOver()) {
            ans.append("The game is over!\n");
        }
        return ans.toString();
    }
}
