package test;

import battleship.Cruiser;
import battleship.Ocean;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ShipTest {

    @Test
    void okToPlaceShipAt() {
        Ocean ocean = new Ocean();
        Cruiser cruiser = new Cruiser();

        assertFalse(cruiser.okToPlaceShipAt(ocean.SIZE - 1, 0, false, ocean));
        assertFalse(cruiser.okToPlaceShipAt(0, ocean.SIZE - 2, true, ocean));
        assertFalse(cruiser.okToPlaceShipAt(ocean.SIZE - 2, ocean.SIZE - 2, false, ocean));
        assertTrue(cruiser.okToPlaceShipAt(0, 0, true, ocean));
        assertTrue(cruiser.okToPlaceShipAt(0, 0, false, ocean));

        cruiser.placeShipAt(1, 1, true, ocean);
        assertFalse(cruiser.okToPlaceShipAt(0, 0, false, ocean));
        assertFalse(cruiser.okToPlaceShipAt(0, 0, true, ocean));
        assertFalse(cruiser.okToPlaceShipAt(1, 0, true, ocean));
        assertFalse(cruiser.okToPlaceShipAt(2, 0, true, ocean));
        assertFalse(cruiser.okToPlaceShipAt(0, 3, false, ocean));
        assertTrue(cruiser.okToPlaceShipAt(3, 0, false, ocean));
    }

    @Test
    void placeShipAt() {
        Ocean ocean = new Ocean();
        Cruiser cruiser = new Cruiser();
        cruiser.placeShipAt(1, 1, true, ocean);
        assertTrue(ocean.isOccupied(1, 1));
        assertTrue(ocean.isOccupied(1, 2));
        assertTrue(ocean.isOccupied(1, 3));
        assertFalse(ocean.isOccupied(1, 4));
    }
}