package test;

import battleship.Destroyer;
import battleship.Submarine;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SubmarineTest {

    @org.junit.jupiter.api.Test
    void getShipType() {
        Submarine submarine = new Submarine();
        assertEquals(submarine.getShipType(), "submarine");
    }

    @Test
    void getLength() {
        Submarine submarine = new Submarine();
        assertEquals(submarine.getLength(), 1);
    }
}