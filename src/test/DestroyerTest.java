package test;

import battleship.Destroyer;
import battleship.Ship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DestroyerTest {

    @Test
    void getLength() {
        Destroyer destroyer = new Destroyer();
        assertEquals(destroyer.getLength(), 2);
    }

    @Test
    void getBowRow() {
        Destroyer destroyer = new Destroyer();
        destroyer.setBowRow(2);
        assertEquals(destroyer.getBowRow(), 2);
        destroyer.setBowRow(5);
        assertEquals(destroyer.getBowRow(), 5);
    }

    @Test
    void getBowColumn() {
        Destroyer destroyer = new Destroyer();
        destroyer.setBowColumn(2);
        assertEquals(destroyer.getBowColumn(), 2);
        destroyer.setBowColumn(5);
        assertEquals(destroyer.getBowColumn(), 5);
    }

    @Test
    void isHorizontal() {
        Destroyer destroyer = new Destroyer();
        assertNotNull(destroyer);
        destroyer.setHorizontal(true);
        assertTrue(destroyer.isHorizontal());
        destroyer.setHorizontal(false);
        assertFalse(destroyer.isHorizontal());
    }

    @Test
    void getHit() {
        Destroyer destroyer = getInstance();
        assertEquals(destroyer.getBowColumn(), 2);
        assertEquals(destroyer.getBowRow(), 2);
        assertTrue(destroyer.isHorizontal());

        assertNotNull(destroyer.getHit());
        assertEquals(destroyer.getHit().length, 4);
        assertArrayEquals(destroyer.getHit(), new boolean[]{false, false, false, false});
        destroyer.shootAt(2, 3);
        assertArrayEquals(destroyer.getHit(), new boolean[]{false, true, false, false});
        destroyer.shootAt(2, 2);
        assertArrayEquals(destroyer.getHit(), new boolean[]{true, true, false, false});
    }

    @Test
    void getShipType() {
        Destroyer destroyer = new Destroyer();
        assertNotNull(destroyer);
        assertEquals(destroyer.getShipType(), "destroyer");
    }

    @Test
    void shootAt() {
        Destroyer destroyer = getInstance();
        assertTrue(destroyer.shootAt(2, 2));
        // Because ship isn't sunk.
        assertTrue(destroyer.shootAt(2, 2));
        assertTrue(destroyer.shootAt(2, 3));
        // Because ship is sunk.
        assertFalse(destroyer.shootAt(2, 3));
    }

    @Test
    void isAlreadyFired() {
        Destroyer destroyer = getInstance();
        assertNotNull(destroyer.getHit());
        destroyer.shootAt(2, 3);
        assertArrayEquals(destroyer.getHit(), new boolean[]{false, true, false, false});
        assertTrue(destroyer.isAlreadyFired(2, 3));
        assertFalse(destroyer.isAlreadyFired(2, 2));
    }

    @Test
    void isSunk() {
        Destroyer destroyer = new Destroyer();

        destroyer.setBowColumn(1);
        destroyer.setBowRow(1);
        destroyer.setHorizontal(true);
        assertEquals(destroyer.getBowColumn(), 1);
        assertEquals(destroyer.getBowRow(), 1);
        assertTrue(destroyer.isHorizontal());

        assertFalse(destroyer.isSunk());
        destroyer.shootAt(1, 1);
        destroyer.shootAt(1, 2);
        assertTrue(destroyer.isSunk());
    }

    @Test
    void testGetShipType() {
        Ship ship = new Ship();
        assertNotNull(ship);
        assertEquals(ship.getShipType(), "empty Sea");
    }

    private Destroyer getInstance() {
        Destroyer destroyer = new Destroyer();
        destroyer.setBowColumn(2);
        destroyer.setBowRow(2);
        destroyer.setHorizontal(true);
        return destroyer;
    }
}