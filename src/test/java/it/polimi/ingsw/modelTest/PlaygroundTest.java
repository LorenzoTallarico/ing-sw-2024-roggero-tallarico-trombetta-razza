package it.polimi.ingsw.modelTest;

import static org.junit.jupiter.api.Assertions.*;

import com.google.gson.Gson;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;


public class PlaygroundTest {

    private ResourceCard[] getOrderedResourceDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            return tempResource;
        } catch (IOException e) {
            return null;
        }
    }

    private GoldCard[] getOrderedGoldDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            GoldCard[] tempGold = gson.fromJson(reader, GoldCard[].class);
            return tempGold;
        } catch (IOException e) {
            return null;
        }
    }

    private StarterCard[] getOrderedStarterDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/StarterCards.json")) {
            StarterCard[] tempStarter = gson.fromJson(reader, StarterCard[].class);
            return tempStarter;
        } catch (IOException e) {
            return null;
        }
    }

    @Test
    void creationTest() {
        Playground table = new Playground();
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
        assertEquals(table.getEastBound(), 40);
        assertEquals(table.getNorthBound(), 40);
        assertEquals(table.getWestBound(), 40);
        assertEquals(table.getSouthBound(), 40);
    }

    @Test
    void setSpaceTest() {
        StarterCard[] str = getOrderedStarterDeck();
        GoldCard[] gld = getOrderedGoldDeck();
        ResourceCard[] rsr = getOrderedResourceDeck();
        assert str != null;
        assert gld != null;
        assert rsr != null;

        Playground table1 = new Playground();
        assertEquals(table1.countResources(Resource.WOLF), 0);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 0);
        assertEquals(table1.countResources(Resource.MUSHROOM), 0);
        assertEquals(table1.countResources(Resource.LEAF), 0);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        str[0].setFront(true);
        assertEquals(table1.setSpace(str[0], 40, 40), 0);
        assertEquals(str[0].getID(), table1.getSpace(40,40).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 0);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 2);
        assertEquals(table1.countResources(Resource.MUSHROOM), 0);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        rsr[0].setFront(false);
        assertEquals(table1.setSpace(rsr[0], 41, 39), 0);
        assertEquals(rsr[0].getID(), table1.getSpace(41,39).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 0);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table1.countResources(Resource.MUSHROOM), 1);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        rsr[9].setFront(true);
        assertEquals(table1.setSpace(rsr[9], 41, 41), 1);
        assertEquals(rsr[9].getID(), table1.getSpace(41,41).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 0);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table1.countResources(Resource.MUSHROOM), 2);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        rsr[4].setFront(true);
        assertEquals(table1.setSpace(rsr[4], 43, 43), 0);
        assertEquals(rsr[4].getID(), table1.getSpace(43,43).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 0);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table1.countResources(Resource.MUSHROOM), 3);
        assertEquals(table1.countResources(Resource.LEAF), 2);
        assertEquals(table1.countItems(Item.PLUME), 1);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        rsr[27].setFront(false);
        assertEquals(table1.setSpace(rsr[27], 39, 41), 0);
        assertEquals(rsr[27].getID(), table1.getSpace(39,41).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 1);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table1.countResources(Resource.MUSHROOM), 3);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 1);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        gld[0].setFront(true);
        assertEquals(table1.setSpace(gld[0], 38, 42), 2);
        assertEquals(gld[0].getID(), table1.getSpace(38,42).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 1);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table1.countResources(Resource.MUSHROOM), 3);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 2);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        assertEquals(table1.getEastBound(), 43);
        assertEquals(table1.getNorthBound(), 38);
        assertEquals(table1.getWestBound(), 39);
        assertEquals(table1.getSouthBound(), 43);

        Playground table2 = new Playground();
        str = getOrderedStarterDeck();
        gld = getOrderedGoldDeck();
        rsr = getOrderedResourceDeck();
        assert str != null;
        assert gld != null;
        assert rsr != null;

        table2.setSpace(rsr[0], 39, 39);
        table2.setSpace(rsr[1], 41, 39);
        table2.setSpace(rsr[2], 41, 41);
        table2.setSpace(rsr[20], 39, 41);
        gld[3].setFront(true);
        assertEquals(table2.setSpace(gld[3], 40, 40), 8);
        table2.setSpace(rsr[39], 39, 43);
        gld[5].setFront(true);
        assertEquals(table2.setSpace(gld[5], 40, 42), 6);
        assertEquals(table2.setSpace(gld[5], 38, 40), 4);
        assertEquals(table2.setSpace(gld[5], 37, 39), 2);

        Playground table3 = new Playground();
        str = getOrderedStarterDeck();
        gld = getOrderedGoldDeck();
        rsr = getOrderedResourceDeck();
        assert str != null;
        assert gld != null;
        assert rsr != null;

        rsr[24].setFront(true);
        table3.setSpace(rsr[24], 40, 40);
        assertEquals(1, table3.countItems(Item.JAR));
        table3.setSpace(rsr[0], 41, 39);
        assertEquals(0, table3.countItems(Item.JAR));

    }

    @Test
    void removeCardFromPlaygroundTest() {
        StarterCard[] str = getOrderedStarterDeck();
        GoldCard[] gld = getOrderedGoldDeck();
        ResourceCard[] rsr = getOrderedResourceDeck();
        assert str != null;
        assert gld != null;
        assert rsr != null;

        Playground table1 = new Playground();
        assertEquals(table1.countResources(Resource.WOLF), 0);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 0);
        assertEquals(table1.countResources(Resource.MUSHROOM), 0);
        assertEquals(table1.countResources(Resource.LEAF), 0);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        str[5].setFront(true);
        assertEquals(table1.setSpace(str[5], 0, 0), 0);
        assertEquals(str[5].getID(), table1.getSpace(40,40).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 1);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 0);
        assertEquals(table1.countResources(Resource.MUSHROOM), 1);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        rsr[39].setFront(true);
        assertEquals(table1.setSpace(rsr[39], 39, 41), 1);
        assertEquals(rsr[39].getID(), table1.getSpace(39,41).getCard().getID());
        assertEquals(table1.countResources(Resource.WOLF), 1);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table1.countResources(Resource.MUSHROOM), 1);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        assertFalse(table1.getSpace(40, 40).getCard().getFrontCorners()[0].isVisible());

        table1.setPlaygroundBeforePlace( 39, 41, rsr[39]);
        table1.removeLastOrderedCoord();

        assertEquals(table1.countResources(Resource.WOLF), 1);
        assertEquals(table1.countResources(Resource.BUTTERFLY), 0);
        assertEquals(table1.countResources(Resource.MUSHROOM), 1);
        assertEquals(table1.countResources(Resource.LEAF), 1);
        assertEquals(table1.countItems(Item.PLUME), 0);
        assertEquals(table1.countItems(Item.JAR), 0);
        assertEquals(table1.countItems(Item.SCROLL), 0);

        assertTrue(table1.getSpace(40, 40).getCard().getFrontCorners()[0].isVisible());
        assertTrue(table1.getSpace(39, 41).isFree());

        assertNull(table1.getOrderedCoords().get(rsr[39]));


        str = getOrderedStarterDeck();
        gld = getOrderedGoldDeck();
        rsr = getOrderedResourceDeck();
        assert str != null;
        assert gld != null;
        assert rsr != null;

        Playground table2 = new Playground();

        str[0].setFront(true);
        assertEquals(table2.setSpace(str[0], 40, 40), 0);
        assertEquals(str[0].getID(), table2.getSpace(40,40).getCard().getID());
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 2);
        assertEquals(table2.countResources(Resource.MUSHROOM), 0);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 0);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        rsr[0].setFront(false);
        assertEquals(table2.setSpace(rsr[0], 41, 39), 0);
        assertEquals(rsr[0].getID(), table2.getSpace(41,39).getCard().getID());
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 1);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 0);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        rsr[9].setFront(true);
        assertEquals(table2.setSpace(rsr[9], 41, 41), 1);
        assertEquals(rsr[9].getID(), table2.getSpace(41,41).getCard().getID());
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 2);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 0);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        rsr[4].setFront(true);
        assertEquals(table2.setSpace(rsr[4], 43, 43), 0);
        assertEquals(rsr[4].getID(), table2.getSpace(43,43).getCard().getID());
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 3);
        assertEquals(table2.countResources(Resource.LEAF), 2);
        assertEquals(table2.countItems(Item.PLUME), 1);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        rsr[27].setFront(false);
        assertEquals(table2.setSpace(rsr[27], 39, 41), 0);
        assertEquals(rsr[27].getID(), table2.getSpace(39,41).getCard().getID());
        assertEquals(table2.countResources(Resource.WOLF), 1);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 3);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 1);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        gld[0].setFront(true);
        assertEquals(table2.setSpace(gld[0], 38, 42), 2);
        assertEquals(gld[0].getID(), table2.getSpace(38,42).getCard().getID());
        assertEquals(table2.countResources(Resource.WOLF), 1);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 3);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 2);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        assertEquals(table2.getEastBound(), 43);
        assertEquals(table2.getNorthBound(), 38);
        assertEquals(table2.getWestBound(), 39);
        assertEquals(table2.getSouthBound(), 43);

        rsr[16].setFront(true);
        assertEquals(0, table2.setSpace(rsr[16], 39, 39));
        assertEquals(rsr[16].getID(), table2.getSpace(39,39).getCard().getID());
        assertEquals(2, table2.countResources(Resource.WOLF));
        assertEquals(1,table2.countResources(Resource.BUTTERFLY));
        assertEquals(3, table2.countResources(Resource.MUSHROOM));
        assertEquals(2, table2.countResources(Resource.LEAF));
        assertEquals(2, table2.countItems(Item.PLUME));
        assertEquals(0, table2.countItems(Item.JAR));
        assertEquals(1, table2.countItems(Item.SCROLL));

        rsr[15].setFront(true);
        assertEquals(0, table2.setSpace(rsr[15], 38, 38));
        assertEquals(rsr[15].getID(), table2.getSpace(38,38).getCard().getID());
        assertEquals(2, table2.countResources(Resource.WOLF));
        assertEquals(1,table2.countResources(Resource.BUTTERFLY));
        assertEquals(4, table2.countResources(Resource.MUSHROOM));
        assertEquals(3, table2.countResources(Resource.LEAF));
        assertEquals(2, table2.countItems(Item.PLUME));
        assertEquals(1, table2.countItems(Item.JAR));
        assertEquals(0, table2.countItems(Item.SCROLL));

        rsr[14].setFront(true);
        assertEquals(0, table2.setSpace(rsr[14], 37, 37));
        assertEquals(rsr[14].getID(), table2.getSpace(37,37).getCard().getID());
        assertEquals(2, table2.countResources(Resource.WOLF));
        assertEquals(2,table2.countResources(Resource.BUTTERFLY));
        assertEquals(3, table2.countResources(Resource.MUSHROOM));
        assertEquals(4, table2.countResources(Resource.LEAF));
        assertEquals(3, table2.countItems(Item.PLUME));
        assertEquals(1, table2.countItems(Item.JAR));
        assertEquals(0, table2.countItems(Item.SCROLL));

//removing

        table2.setPlaygroundBeforePlace(37, 37, rsr[14]);
        assertEquals(2, table2.countResources(Resource.WOLF));
        assertEquals(1,table2.countResources(Resource.BUTTERFLY));
        assertEquals(4, table2.countResources(Resource.MUSHROOM));
        assertEquals(3, table2.countResources(Resource.LEAF));
        assertEquals(2, table2.countItems(Item.PLUME));
        assertEquals(1, table2.countItems(Item.JAR));
        assertEquals(0, table2.countItems(Item.SCROLL));

        table2.setPlaygroundBeforePlace(38, 38, rsr[15]);
        assertEquals(2, table2.countResources(Resource.WOLF));
        assertEquals(1,table2.countResources(Resource.BUTTERFLY));
        assertEquals(3, table2.countResources(Resource.MUSHROOM));
        assertEquals(2, table2.countResources(Resource.LEAF));
        assertEquals(2, table2.countItems(Item.PLUME));
        assertEquals(0, table2.countItems(Item.JAR));
        assertEquals(1, table2.countItems(Item.SCROLL));

        table2.setPlaygroundBeforePlace(39, 39, rsr[16]);
        assertEquals(1, table2.countResources(Resource.WOLF));
        assertEquals(1,table2.countResources(Resource.BUTTERFLY));
        assertEquals(3, table2.countResources(Resource.MUSHROOM));
        assertEquals(1, table2.countResources(Resource.LEAF));
        assertEquals(2, table2.countItems(Item.PLUME));
        assertEquals(0, table2.countItems(Item.JAR));
        assertEquals(0, table2.countItems(Item.SCROLL));


        table2.setPlaygroundBeforePlace(38, 42, gld[0]);
        assertEquals(table2.countResources(Resource.WOLF), 1);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 3);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 1);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        table2.setPlaygroundBeforePlace(39, 41, rsr[27]);
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 3);
        assertEquals(table2.countResources(Resource.LEAF), 2);
        assertEquals(table2.countItems(Item.PLUME), 1);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        table2.setPlaygroundBeforePlace(43, 43, rsr[4]);
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 2);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 0);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        table2.setPlaygroundBeforePlace(41, 41, rsr[9]);
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 1);
        assertEquals(table2.countResources(Resource.MUSHROOM), 1);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 0);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);

        table2.setPlaygroundBeforePlace(41, 39, rsr[0]);
        assertEquals(table2.countResources(Resource.WOLF), 0);
        assertEquals(table2.countResources(Resource.BUTTERFLY), 2);
        assertEquals(table2.countResources(Resource.MUSHROOM), 0);
        assertEquals(table2.countResources(Resource.LEAF), 1);
        assertEquals(table2.countItems(Item.PLUME), 0);
        assertEquals(table2.countItems(Item.JAR), 0);
        assertEquals(table2.countItems(Item.SCROLL), 0);


        str = getOrderedStarterDeck();
        gld = getOrderedGoldDeck();
        rsr = getOrderedResourceDeck();
        assert str != null;
        assert gld != null;
        assert rsr != null;

        Playground table3 = new Playground();
        for(int i = 0; i < rsr.length; i++)
            rsr[i].setFront(true);
        table3.setSpace(rsr[4], 41, 39);
        table3.setSpace(rsr[5], 41, 41);
        table3.setSpace(rsr[6], 39, 41);
        table3.setSpace(rsr[25], 39, 39);
        assertEquals(2, table3.countResources(Resource.WOLF));
        assertEquals(1, table3.countResources(Resource.BUTTERFLY));
        assertEquals(3, table3.countResources(Resource.MUSHROOM));
        assertEquals(2, table3.countResources(Resource.LEAF));
        assertEquals(1, table3.countItems(Item.PLUME));
        assertEquals(1, table3.countItems(Item.JAR));
        assertEquals(2, table3.countItems(Item.SCROLL));

        table3.setSpace(gld[0], 40, 40);
        assertEquals(2, table3.countResources(Resource.WOLF));
        assertEquals(1, table3.countResources(Resource.BUTTERFLY));
        assertEquals(4, table3.countResources(Resource.MUSHROOM));
        assertEquals(2, table3.countResources(Resource.LEAF));
        assertEquals(0, table3.countItems(Item.PLUME));
        assertEquals(0, table3.countItems(Item.JAR));
        assertEquals(0, table3.countItems(Item.SCROLL));

        table3.setPlaygroundBeforePlace(40, 40, gld[0]);
        assertEquals(2, table3.countResources(Resource.WOLF));
        assertEquals(1, table3.countResources(Resource.BUTTERFLY));
        assertEquals(3, table3.countResources(Resource.MUSHROOM));
        assertEquals(2, table3.countResources(Resource.LEAF));
        assertEquals(1, table3.countItems(Item.PLUME));
        assertEquals(1, table3.countItems(Item.JAR));
        assertEquals(2, table3.countItems(Item.SCROLL));
    }

    @Test
    void spaceTest() {
        Space space = new Space();
        assertNull(space.getCard());
        space.setDead(true);
        assertTrue(space.isDead());
        space.setDead(false);
        assertFalse(space.isDead());
    }



}
