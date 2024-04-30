package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerTest {


    @BeforeEach
    // da mettere qui l'inizializzazione delle carte e del player


    //assert
    @Test
    @DisplayName("Integrity Test")
    void integrityTest(){
        Player noArgs = new Player();
        //to check if everything is correctly initialized in "noArgs"


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
    }


    @Test
    @DisplayName("Card Placement Test")
    void placementTest(){
        Player p = new Player("gigi", false);

        Corner[] frontAng = new Corner[4];
        Corner[] backAng = new Corner[4];
        for(int i=0; i<4; i++){
            Corner c = new Corner(CornerType.FREE, null, null);
            backAng[i] = c;
        }
        Corner c0 = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        Corner c1 = new Corner(CornerType.DEAD, null, null);
        Corner c2 = new Corner(CornerType.ITEM, null, Item.SCROLL);
        Corner c3 = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        frontAng[0]= c0;
        frontAng[1]= c1;
        frontAng[2]= c2;
        frontAng[3]= c3;

        Card card1 = new ResourceCard(2, Resource.LEAF, frontAng, backAng);
        Card card2 = new ResourceCard(2, Resource.WOLF, frontAng, backAng);
        Card card3 = new ResourceCard(2, Resource.MUSHROOM, frontAng, backAng);



        p.addCard(card1);
        p.addCard(card2);
        p.addCard(card3);
        assert(p.getHand().contains(card1));
        assert(p.getHand().contains(card2));
        assert(p.getHand().contains(card3));

        p.getHand().remove(card1);
        p.getHand().remove(card2);
        p.getHand().remove(card3);
        assertTrue(p.getHand().isEmpty());

        ArrayList<Card> phand = new ArrayList<Card>();
        phand.add(card1);
        phand.add(card2);
        phand.add(card3);
        p.setHand(phand);
        assert(p.getHand().contains(card1));
        assert(p.getHand().contains(card2));
        assert(p.getHand().contains(card3));



        /*          Front card1
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |        LEAF         |
         *       |                     |
         *       | SCROLL           \  |
         *       |_____________________|
         *
         * */

        /*          Front card2
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |        WOLF         |
         *       |                     |
         *       | SCROLL           \  |
         *       |_____________________|
         *
         * */


        /*          Front card3
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      MUSHROOM       |
         *       |                     |
         *       | SCROLL           \  |
         *       |_____________________|
         *
         * */



        //Creazione starter card 81
        Corner[] frontAngS = new Corner[4];
        Corner c0s = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        Corner c1s = new Corner(CornerType.FREE, null, null);
        Corner c2s = new Corner(CornerType.RESOURCE, Resource.BUTTERFLY, null);
        Corner c3s = new Corner(CornerType.FREE, null, null);
        frontAngS[0]= c0s;
        frontAngS[1]= c1s;
        frontAngS[2]= c2s;
        frontAngS[3]= c3s;
        Corner[] backAngS = new Corner[4];
        c0s = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        c1s = new Corner(CornerType.RESOURCE, Resource.WOLF, null);
        c2s = new Corner(CornerType.RESOURCE, Resource.BUTTERFLY, null);
        c3s = new Corner(CornerType.RESOURCE, Resource.MUSHROOM, null);
        backAngS[0]= c0;
        backAngS[1]= c1;
        backAngS[2]= c2;
        backAngS[3]= c3;
        Card startCard = new StarterCard(Resource.BUTTERFLY, null, null, frontAngS, backAngS);

        /*          Front StarterCard                Back StarterCard
         *        _____________________            ______________________
         *       | free           LEAF |          | MUSHROOM       LEAF  |
         *       |                     |          |                      |
         *       |      BUTTERFLY      |          |                      |
         *       |                     |          |                      |
         *       | BUTTERLY            |          | BUTTERLY       WOLF  |
         *       |_____________________|          |______________________|
         *
         * */


        assertTrue(p.placeable(card1, 40, 40));


        p.place(p.getHand().get(0), 40, 40);










    }
}