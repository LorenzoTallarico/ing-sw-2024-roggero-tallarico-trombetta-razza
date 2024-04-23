package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerTest {


    //assert
    @Test
    @DisplayName("Integrity Test")
    void integrityTest(){
        Player p = new Player("gigi", false);
        p.setColor(Color.YELLOW);
        assertEquals(Color.YELLOW, p.getColor());
        System.out.println("Colore yellow: " + p.getColor().toString());
        p.setColor(Color.RED);
        assertEquals(Color.RED, p.getColor());
        System.out.println("Colore red: " + p.getColor().toString());
        p.setColor(Color.GREEN);
        assertEquals(Color.GREEN, p.getColor());
        System.out.println("Colore green: " + p.getColor().toString());
        p.setColor(Color.BLUE);
        assertEquals(Color.BLUE, p.getColor());
        System.out.println("Colore blue: " + p.getColor().toString());

        assertEquals("gigi", p.getName());

        assertFalse(p.isWinner());

        assertEquals(0, p.getPoints());

        AchievementCard achi1 = new AchievementCard(3, Resource.BUTTERFLY, "ConcreteStrategyDiagonal", Item.SCROLL);
        AchievementCard achi2 = new AchievementCard(2, Resource.WOLF, "ConcreteStrategyMixed", Item.PLUME);
        ArrayList<AchievementCard> achiList = new ArrayList<AchievementCard>();
        achiList.add(achi1);
        achiList.add(achi2);

        p.setSecretAchievement(achiList);
        assertEquals(achiList, p.getSecretAchievement());

        //ResourceCard c1 = new ResourceCard()





    }


    @Test
    @DisplayName("Card Placement Test")
    void placementTest(){



    }
}
