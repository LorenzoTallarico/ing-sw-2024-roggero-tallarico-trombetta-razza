package it.polimi.ingsw.modelTest;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import it.polimi.ingsw.model.Color;
import it.polimi.ingsw.model.Playground;
import it.polimi.ingsw.model.Space;


public class PlaygroundTest {

    @Test
    void creationTest() {
        Playground table = new Playground();
        //pace spaceFree = new Space(true, false);
        //Space spaceDead = new Space(false, true);
        for (int x = 0; x < 81; x++) {
            for (int y = 0; y < 81; y++) {
                if ((x + y) % 2 == 0) {
                    assertEquals(table.getSpace(x, y).isFree(), true, "Casella Free non inizializzata correttamente (Campo: free)");
                    assertEquals(table.getSpace(x, y).isDead(), false, "Casella Dead non inizializzata correttamente (Campo: dead)");
                } else {
                    assertEquals(table.getSpace(x, y).isFree(), false, "Casella Free non inizializzata correttamente (Campo: free)");
                    assertEquals(table.getSpace(x, y).isDead(), true, "Casella Dead non inizializzata correttamente (Campo: dead)");
                }
            }

        }

    }


}
