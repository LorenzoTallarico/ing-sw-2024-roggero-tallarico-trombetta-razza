package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.ClientRmi;
import it.polimi.ingsw.networking.VirtualView;
import it.polimi.ingsw.util.Print;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;


public class PlayerTest {

    @Test
    @DisplayName("Integrity Test")
    void integrityTest() throws RemoteException {
        Game testGame = Game.getInstance();
        Player p = new Player("gigi");
        ArrayList<Player> playersList = new ArrayList<>();
        playersList.add(p);
        VirtualView cli = new ClientRmi(null);
        ArrayList<VirtualView> clients = new ArrayList<>();
        clients.add(cli);
        testGame.addPlayers(playersList, clients);

        p.setColor(Color.YELLOW);
        assertEquals(Color.YELLOW, p.getColor());
        System.out.println("Color yellow: " + p.getColor().toString());
        p.setColor(Color.RED);
        assertEquals(Color.RED, p.getColor());
        System.out.println("Color red: " + p.getColor().toString());
        p.setColor(Color.GREEN);
        assertEquals(Color.GREEN, p.getColor());
        System.out.println("Color green: " + p.getColor().toString());
        p.setColor(Color.BLUE);
        assertEquals(Color.BLUE, p.getColor());
        System.out.println("Color blue: " + p.getColor().toString());

        assertEquals("gigi", p.getName());
        assertFalse(p.isWinner());
        assertEquals(0, p.getPoints());

        AchievementCard achi1 = new AchievementCard(3, Resource.BUTTERFLY, "ConcreteStrategyDiagonal", Item.SCROLL, "090");
        AchievementCard achi2 = new AchievementCard(2, Resource.WOLF, "ConcreteStrategyMixed", Item.PLUME, "099");
        ArrayList<AchievementCard> achiList = new ArrayList<AchievementCard>();
        achiList.add(achi1);
        achiList.add(achi2);

        p.setSecretAchievement(achiList);
        assertEquals(achiList, p.getSecretAchievement());


        //to reset singleton
        testGame.end();
        testGame.nextState();

    }


    @Test
    @DisplayName("Card Placement Test")
    void placementTest() throws RemoteException {

        Game testGame = Game.getInstance();
        Player p = new Player("gigi");

        ArrayList<Player> playersList = new ArrayList<>();
        playersList.add(p);
        VirtualView cli = new ClientRmi(null);
        ArrayList<VirtualView> clients = new ArrayList<>();
        clients.add(cli);
        testGame.addPlayers(playersList, clients);


        Corner[] frontAng = new Corner[4];
        Corner[] backAng = new Corner[4];
        for(int i=0; i<4; i++){
            Corner c = new Corner(CornerType.FREE, null, null);
            backAng[i] = c;
        }
        Corner c0 = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        Corner c1 = new Corner(CornerType.DEAD, null, null);
        Corner c2 = new Corner(CornerType.FREE, null, null);
        Corner c3 = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        frontAng[0]= c0;
        frontAng[1]= c1;
        frontAng[2]= c2;
        frontAng[3]= c3;

        Card card1 = new ResourceCard(2, Resource.LEAF, frontAng, backAng, "000");
        Card card2 = new ResourceCard(2, Resource.WOLF, frontAng, backAng, "000");
        Card card3 = new ResourceCard(2, Resource.MUSHROOM, frontAng, backAng, "000");
        Card card4 = new ResourceCard(2, Resource.BUTTERFLY, frontAng, backAng, "000");
        Card card5 = new ResourceCard(2, Resource.BUTTERFLY, frontAng, backAng, "000");



        p.addCard(card1);
        p.addCard(card2);
        p.addCard(card3);
        assert(p.getHand().contains(card1));
        assert(p.getHand().contains(card2));
        assert(p.getHand().contains(card3));
        System.out.println("Cards in player's hand");
        for(Card c : p.getHand()){
            Print.cardPrinter(c);
        }




        ArrayList<Card> phand = new ArrayList<Card>();
        phand.add(card1);
        phand.add(card2);
        phand.add(card3);
        p.setHand(phand);
        assert(p.getHand().contains(card1));
        assert(p.getHand().contains(card2));
        assert(p.getHand().contains(card3));


        for(Card c : p.getHand())
            Print.cardPrinter(c, true);



        //Creation starter card #81
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
        backAngS[0]= c0s;
        backAngS[1]= c1s;
        backAngS[2]= c2s;
        backAngS[3]= c3s;
        Card startCard = new StarterCard(Resource.BUTTERFLY, null, null, frontAngS, backAngS, "081");
        System.out.println("Print Starter Card:");
        Print.cardPrinter(startCard, false);

        /*          Front StarterCard                Back StarterCard
         *        _____________________            ______________________
         *       | free           LEAF |          | MUSHROOM       LEAF  |
         *       |                     |          |                      |
         *       |      BUTTERFLY      |          |                      |
         *       |                     |          |                      |
         *       | BUTTERLY     free   |          | BUTTERLY       WOLF  |
         *       |_____________________|          |______________________|
         *
         */


        System.out.println("\n\n");
        // startCard can't be placed because is already assigned
        assertFalse(startCard.isFront());
        assertFalse(p.placeable(startCard, 40, 40));
        p.getArea().setSpace(startCard,40, 40);
        System.out.println("Print the starter card");
        Print.cardPrinter(startCard, false);
        assertEquals(startCard, p.getArea().getSpace(40,40).getCard());
        assertTrue(p.getArea().getSpace(40,40).getCard().getBackCorners()[0].isVisible());
        assertFalse(p.getArea().getSpace(40, 40).isFree());
        System.out.println("Print player's playground");
        Print.playgroundPrinter(p.getArea());


        assertEquals(p.getHand().get(0), card1);
        p.getHand().get(0).setFront(true);
        assertTrue(p.getHand().get(0).isFront());

        //placement in all the dead spaces around starterCard
        assertFalse(p.place(0,  false, 41, 40));
        assertFalse(p.place(0,  false, 40, 41));
        assertFalse(p.place(0,  false, 39, 40));
        assertFalse(p.place(0,  false, 40, 39));


        //out of bounds
        assertFalse(p.place(0, false, 36, 36));


        /*          Front card1   (top right from starter)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |        LEAF         |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */
        //placing starter card in top right position
        assertTrue(p.place(0, true, 39, 41));    //starterCard top right corner
        System.out.println("Prints card in position [39][41]:");
        Print.cardPrinter(p.getArea().getSpace(39,41).getCard(), true);
        assertEquals(card1, p.getArea().getSpace(39,41).getCard());
        assertFalse(p.getHand().contains(card1));
        //checks if the card's corner is correctly covered in top right position
        assertFalse(p.getArea().getSpace(40,40).getCard().getBackCorners()[0].isVisible());

        //checks if space is correctly occupied
        assertFalse(p.getArea().getSpace(39, 41).isFree());
        System.out.println("Print player's playground");
        Print.playgroundPrinter(p.getArea());


        /*          Front card2 (top left from starter)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |        WOLF         |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */
        //placing starter card in top left position
        p.getHand().get(p.getHand().indexOf(card2)).setFront(true);
        assertTrue(p.place(p.getHand().indexOf(card2), true, 39, 39));    //starterCard top left corner
        System.out.println("Prints card in position [39][39]:");
        Print.cardPrinter(p.getArea().getSpace(39,39).getCard(), true);
        assertEquals(card2, p.getArea().getSpace(39,39).getCard());
        assertFalse(p.getHand().contains(card2));
        System.out.println("Print player's playground");
        Print.playgroundPrinter(p.getArea());



        /*          Front card3  (dead corner of card2)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      MUSHROOM       |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */


        //trying to place top left from card3 (not possible due to card2's dead corner)
        p.getHand().get(p.getHand().indexOf(card3)).setFront(true);
        assertFalse(p.place(p.getHand().indexOf(card3), true, 40, 42));
        assertNotEquals(card3, p.getArea().getSpace(40,42).getCard());
        assertTrue(p.getArea().getSpace(40,42).isFree());
        assertTrue(p.getHand().contains(card3));

        //intermediate check for dead spaces
        assertFalse(p.place(p.getHand().indexOf(card3), true, 40, 39));
        assertFalse(p.place(p.getHand().indexOf(card3), true, 39, 40));
        //out of bounds
        assertFalse(p.place(p.getHand().indexOf(card3), true, 37, 37));


        // now placing card3 in 40, 38
        assertTrue(p.place(p.getHand().indexOf(card3), true, 40, 38));
        System.out.println("Prints card in position [40][38]:");
        Print.cardPrinter(p.getArea().getSpace(40,38).getCard(), true);
        assertEquals(card3, p.getArea().getSpace(40,38).getCard());
        assertFalse(p.getArea().getSpace(40,38).isFree());
        assertFalse(p.getHand().contains(card3));
        System.out.println("Print player's playground");
        Print.playgroundPrinter(p.getArea());



        /*          Front card4 (on card3's dead corner)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      BUTTERFLY      |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */
        // trying to place another card4 between card3 and starter (not possible)
        p.addCard(card4);
        p.getHand().get(p.getHand().indexOf(card4)).setFront(true);
        assertFalse(p.place(p.getHand().indexOf(card4), true, 41, 39));
        assertNotEquals(card4, p.getArea().getSpace(41,39).getCard());
        assertTrue(p.getArea().getSpace(41,39).isFree());
        assertTrue(p.getHand().contains(card4));
        //now placing it bottom left from card3
        assertTrue(p.place(p.getHand().indexOf(card4), true, 41, 37));
        Print.cardPrinter(p.getArea().getSpace(41,37).getCard(), true);
        assertEquals(card4, p.getArea().getSpace(41,37).getCard());
        assertEquals(card4, p.getArea().getSpace(41, 37).getCard());
        assertFalse(p.getArea().getSpace(41,37).isFree());
        assertFalse(p.getHand().contains(card4));
        System.out.println("Print player's playground");
        Print.playgroundPrinter(p.getArea());



        /*          Front card5
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      BUTTERFLY      |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */

        //check starterCard's corners
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[0].getType().toString());
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[1].getType().toString());
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[2].getType().toString());
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[3].getType().toString());

        p.addCard(card5);
        p.getHand().get(p.getHand().indexOf(card5)).setFront(true);
        assertTrue(p.place(p.getHand().indexOf(card5), true, 41, 41));
        Print.cardPrinter(p.getArea().getSpace(41,41).getCard(), true);
        assertEquals(card5, p.getArea().getSpace(41,41).getCard());
        assertEquals(card5, p.getArea().getSpace(41,41).getCard());
        assertFalse(p.getArea().getSpace(41,41).isFree());
        assertFalse(p.getHand().contains(card5));
        System.out.println("Print player's playground");
        Print.playgroundPrinter(p.getArea());


        //this should be the playground:

/*    0 ......37.......38...........39...........40............41....................79
      .
      .
      .
      .
      .
      39                           Card1                      Card2
      .
      40              Card3                    Starter
      .
      41     Card4                                            Card5
      .
      .
      .
      .
      .
      .
      .
     79

*/



        //to reset singleton
        testGame.end();
        testGame.nextState();


    }
}
