package test;

import battleship.Battleship;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BattleshipTest {

    @Test
    void getShipType() {
        Battleship battleship = new Battleship();
        assertEquals(battleship.getShipType(), "battleship");
    }

    @Test
    void getLength() {
        Battleship battleship = new Battleship();
        assertEquals(battleship.getLength(), 4);
    }
}