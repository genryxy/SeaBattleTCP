package test;

import battleship.EmptySea;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EmptySeaTest {

    @Test
    void shootAt() {
        EmptySea emptySea = new EmptySea();
        emptySea.setBowRow(1);
        emptySea.setBowColumn(2);
        assertFalse(emptySea.shootAt(1, 2));
    }

    @Test
    void isSunk() {
        EmptySea emptySea = new EmptySea();
        assertFalse(emptySea.isSunk());
    }

    @Test
    void getLength() {
        EmptySea emptySea = new EmptySea();
        assertEquals(emptySea.getLength(), 1);
    }
}