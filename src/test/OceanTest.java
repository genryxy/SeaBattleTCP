package test;

import battleship.Ocean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OceanTest {

    @Test
    void placeAllShipsRandomly() {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        int numberOccupiedCell = 0;
        int numberBattleship = 0;
        int numberCruiser = 0;
        int numberDestroyer = 0;
        int numberSubmarine = 0;

        for (int row = 0; row < ocean.SIZE; row++) {
            for (int column = 0; column < ocean.SIZE; column++) {
                if (ocean.isOccupied(row, column)) {
                    switch (ocean.getShipsArray()[row][column].getShipType()) {
                        case "battleship":
                            numberBattleship++;
                            break;
                        case "cruiser":
                            numberCruiser++;
                            break;
                        case "destroyer":
                            numberDestroyer++;
                            break;
                        case "submarine":
                            numberSubmarine++;
                            break;
                    }
                    numberOccupiedCell++;
                }
            }
        }
        assertEquals(numberOccupiedCell, 4 + 2 * 3 + 3 * 2 + 4);
        assertEquals(numberBattleship / 4, 1);
        assertEquals(numberCruiser / 3, 2);
        assertEquals(numberDestroyer / 2, 3);
        assertEquals(numberSubmarine, 4);
    }

    @Test
    void isOccupied() {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        boolean isOccupiedCell = !(ocean.getShipsArray()[0][0].getShipType().equals("empty sea"));
        assertEquals(isOccupiedCell, ocean.isOccupied(0, 0));
    }

    @Test
    void shootAt() {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        boolean isEmptyCell = !(ocean.getShipsArray()[ocean.SIZE - 1][ocean.SIZE - 1]
                .getShipType().equals("empty sea"));

        int row = 0;
        int column = 0;
        boolean isFind = false;
        for (row = 0; row < ocean.SIZE && !isFind; row++) {
            for (column = 0; column < ocean.SIZE && !isFind; column++) {
                if (ocean.isOccupied(row, column) && ocean.getShipsArray()[row][column].getShipType().equals("submarine")) {
                    isFind = true;
                }
            }
        }

        // (row - 1) because raw++ was executed
        assertTrue(ocean.shootAt(row - 1, column - 1));
        assertFalse(ocean.shootAt(row - 1, column - 1));
        assertEquals(!isEmptyCell, ocean.shootAt(ocean.SIZE - 1, ocean.SIZE - 1));
    }

    @Test
    void getShotsFired() {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        assertEquals(ocean.getShotsFired(), 0);
        for (int row = 0; row < ocean.SIZE; row++) {
            for (int column = 0; column < ocean.SIZE; column++) {
                ocean.shootAt(row, column);
            }
        }
        assertEquals(ocean.getShotsFired(), ocean.SIZE * ocean.SIZE);
    }

    @Test
    void getHitCount() {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        assertEquals(ocean.getHitCount(), 0);
        for (int row = 0; row < ocean.SIZE; row++) {
            for (int column = 0; column < ocean.SIZE; column++) {
                ocean.shootAt(row, column);
            }
        }
        assertEquals(ocean.getHitCount(), 4 + 2 * 3 + 3 * 2 + 4);
    }

    @Test
    void getShipsSunk() {
        Ocean ocean = new Ocean();
        int numberSunk = 0;
        ocean.placeAllShipsRandomly();
        assertEquals(ocean.getShipsSunk(), 0);
        for (int row = 0; row < ocean.SIZE; row++) {
            for (int column = 0; column < ocean.SIZE; column++) {
                ocean.shootAt(row, column);
                if (ocean.getShipsArray()[row][column].isSunk()) {
                    numberSunk++;
                    assertEquals(ocean.getShipsSunk(), numberSunk);
                }
            }
        }
        assertEquals(ocean.getShipsSunk(), 10);
    }

    @Test
    void isGameOver() {
        Ocean ocean = new Ocean();
        int numberSunk = 0;
        ocean.placeAllShipsRandomly();
        assertFalse(ocean.isGameOver());
        for (int row = 0; row < ocean.SIZE; row++) {
            for (int column = 0; column < ocean.SIZE; column++) {
                ocean.shootAt(row, column);
                if (ocean.getShipsArray()[row][column].isSunk()) {
                    numberSunk++;
                    assertEquals(ocean.isGameOver(), numberSunk == 10);
                }
            }
        }
        assertEquals(ocean.isGameOver(), numberSunk == 10);
    }

    @Test
    void getShipsArray() {
        Ocean ocean = new Ocean();
        assertNotNull(ocean.getShipsArray());
        assertEquals(ocean.getShipsArray().length, ocean.SIZE);
        assertEquals(ocean.getShipsArray()[0].length, ocean.SIZE);
    }

    @Test
    void getShipWrecked() {
        Ocean ocean = new Ocean();
        ocean.placeAllShipsRandomly();
        assertEquals(ocean.getShipWrecked(), 0);

        int row = 0;
        int column = 0;
        boolean isFind = false;
        for (row = 0; row < ocean.SIZE && !isFind; row++) {
            for (column = 0; column < ocean.SIZE && !isFind; column++) {
                if (ocean.isOccupied(row, column) && ocean.getShipsArray()[row][column].getShipType().equals("cruiser")) {
                    isFind = true;
                }
            }
        }

        // (row - 1) because raw++ was executed
        ocean.shootAt(row - 1, column - 1);
        assertEquals(ocean.getShipWrecked(), 1);

        if (ocean.getShipsArray()[row - 1][column - 1].isHorizontal()) {
            ocean.shootAt(row - 1, column);
            assertEquals(ocean.getShipWrecked(), 1);
            ocean.shootAt(row - 1, column + 1);

        } else {
            ocean.shootAt(row, column - 1);
            assertEquals(ocean.getShipWrecked(), 1);
            ocean.shootAt(row + 1, column - 1);
        }
        // This ship is sunk.
        assertEquals(ocean.getShipWrecked(), 0);
    }
}