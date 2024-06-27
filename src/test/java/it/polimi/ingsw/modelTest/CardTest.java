package it.polimi.ingsw.modelTest;

import static org.junit.jupiter.api.Assertions.*;


import com.google.gson.Gson;
import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.Test;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

public class CardTest {

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
    void resourceCardTest() {
        ResourceCard[] rsr = getOrderedResourceDeck();
        String result = "";
        for(int i = 0; i < rsr.length; i++) {
            switch (rsr[i].getResource()) {
                case MUSHROOM:
                    result = "001";
                    break;
                case LEAF:
                    result = "011";
                    break;
                case WOLF:
                    result = "021";
                    break;
                case BUTTERFLY:
                    result = "031";
                    break;
            }
            assertEquals(result, rsr[i].getSideID());
        }

        for(int i = 0; i < rsr.length; i++) {
            rsr[i].setFront(true);
            assertEquals((i+1 < 10 ? "00" : "0") + (i+1), rsr[i].getSideID());
        }

        assertFalse(rsr[9].equals(rsr[0]));
    }


    @Test
    void goldCardTest() {
        GoldCard[] gld = getOrderedGoldDeck();
        String result = "";
        for(int i = 0; i < gld.length; i++) {
            switch (gld[i].getResource()) {
                case MUSHROOM:
                    result = "041";
                    break;
                case LEAF:
                    result = "051";
                    break;
                case WOLF:
                    result = "061";
                    break;
                case BUTTERFLY:
                    result = "071";
                    break;
            }
            assertEquals(result, gld[i].getSideID());
        }

        for(int i = 0; i < gld.length; i++) {
            gld[i].setFront(true);
            assertEquals((i+41 < 10 ? "00" : "0") + (i+41), gld[i].getSideID());
        }

        assertFalse(gld[9].equals(gld[0]));
        Corner c = new Corner(CornerType.FREE, null, null);
        GoldCard g = new GoldCard(3, Resource.BUTTERFLY, new Corner[]{c, c, c, c}, new Corner[]{c, c, c, c}, new int[]{0, 1, 2, 3}, Item.JAR, ReqPoint.ITEM, "");
        assertEquals(3, g.getPoints());
        assertEquals(Resource.BUTTERFLY, g.getResource());
        assertEquals(c, g.getFrontCorners()[0]);
        assertEquals(c, g.getFrontCorners()[1]);
        assertEquals(c, g.getFrontCorners()[2]);
        assertEquals(c, g.getFrontCorners()[3]);
        assertEquals(c, g.getBackCorners()[0]);
        assertEquals(c, g.getBackCorners()[1]);
        assertEquals(c, g.getBackCorners()[2]);
        assertEquals(c, g.getBackCorners()[3]);
        assertEquals(0, g.countResource(Resource.WOLF));
        assertEquals(1, g.countResource(Resource.BUTTERFLY));
        assertEquals(2, g.countResource(Resource.LEAF));
        assertEquals(3, g.countResource(Resource.MUSHROOM));
        assertEquals(Item.JAR, g.getItem());
        assertEquals(ReqPoint.ITEM, g.getPointsType());
        assertEquals("", g.getID());
    }
    
}
