package test;

import battleship.Cruiser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CruiserTest {

    @Test
    void getShipType() {
        Cruiser cruiser = new Cruiser();
        assertEquals(cruiser.getShipType(), "cruiser");
    }

    @Test
    void getLength() {
        Cruiser cruiser = new Cruiser();
        assertEquals(cruiser.getLength(), 3);
    }
}