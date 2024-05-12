package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.util.Print;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.rmi.RemoteException;
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
    void placementTest() throws RemoteException {
        //per inizializzare il game (non serve)
//        Game testGame = Game.getInstance();


        Player p = new Player("gigi", false);

        Corner[] frontAng = new Corner[4];
        Corner[] backAng = new Corner[4];
        for(int i=0; i<4; i++){
            Corner c = new Corner(CornerType.FREE, null, null);
            backAng[i] = c;
        }
        Corner c0 = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        Corner c1 = new Corner(CornerType.DEAD, null, null);
        //era:         Corner c2 = new Corner(CornerType.ITEM, null, Item.SCROLL);
        Corner c2 = new Corner(CornerType.FREE, null, null);
        Corner c3 = new Corner(CornerType.RESOURCE, Resource.LEAF, null);
        frontAng[0]= c0;
        frontAng[1]= c1;
        frontAng[2]= c2;
        frontAng[3]= c3;

        Card card1 = new ResourceCard(2, Resource.LEAF, frontAng, backAng);
        Card card2 = new ResourceCard(2, Resource.WOLF, frontAng, backAng);
        Card card3 = new ResourceCard(2, Resource.MUSHROOM, frontAng, backAng);
        Card card4 = new ResourceCard(2, Resource.BUTTERFLY, frontAng, backAng);
        Card card5 = new ResourceCard(2, Resource.BUTTERFLY, frontAng, backAng);



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


        //stampiamo le carte create
        Print pr = new Print();
        for(Card c : p.getHand())
            pr.cardPrinter(c, true);




        //Creazione starter card numero 81
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
        Card startCard = new StarterCard(Resource.BUTTERFLY, null, null, frontAngS, backAngS);
        p.addCard(startCard);
        System.out.println("Stampa carta Starter:");
        pr.cardPrinter(startCard, false);
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
        //carta startCard posizionata con il back visibile
        assertFalse(startCard.isFront());
        assertTrue(p.placeable(startCard, 40, 40));
        assertTrue(p.place(p.getHand().indexOf(startCard), false, 40, 40));
        System.out.println("Stampa la carta posizionata in [40][40]:");
        pr.cardPrinter(startCard, false);
        assertEquals(startCard, p.getArea().getSpace(40,40).getCard());
        assertTrue(p.getArea().getSpace(40,40).getCard().getBackCorners()[0].isVisible());
        assertFalse(p.getArea().getSpace(40, 40).isFree());


        assertEquals(p.getHand().get(0), card1);
        p.getHand().get(0).setFront(true);
        assertTrue(p.getHand().get(0).isFront());

        //posizionamento in tutti gli spazi dead attorno alla carta
        assertFalse(p.place(0,  false, 41, 40));
        assertFalse(p.place(0,  false, 40, 41));
        assertFalse(p.place(0,  false, 39, 40));
        assertFalse(p.place(0,  false, 40, 39));


        //fuori dai bound
        assertFalse(p.place(0, false, 36, 36));


        /*          Front card1   (in alto a dx a start)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |        LEAF         |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */
        //(metto card1 in alto a dx rispetto a startCard
        //posizionamento in tutti gli spazi verso corner della carta
        assertTrue(p.place(0, true, 39, 41));    //corner alto dx di starterCard
        System.out.println("Stampa la carta posizionata in [39][41]:");
        pr.cardPrinter(p.getArea().getSpace(39,41).getCard(), true);
        assertEquals(card1, p.getArea().getSpace(39,41).getCard());
        assertFalse(p.getHand().contains(card1));
        //controllo se è stato coperto correttamente l'angolo in alto a dx della startCard
        assertFalse(p.getArea().getSpace(40,40).getCard().getBackCorners()[0].isVisible());

        //controllo se lo spazio è stato effettivamente occupato
        assertFalse(p.getArea().getSpace(39, 41).isFree());

        /*          Front card2 (in alto a sx a start)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |        WOLF         |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */
        //(metto card2 in alto a sx rispetto a startCard)
        p.getHand().get(p.getHand().indexOf(card2)).setFront(true);
        assertTrue(p.place(p.getHand().indexOf(card2), true, 39, 39));    //corner alto sx di starterCard
        System.out.println("Stampa la carta posizionata in [39][39]:");
        pr.cardPrinter(p.getArea().getSpace(39,39).getCard(), true);
        assertEquals(card2, p.getArea().getSpace(39,39).getCard());
        assertFalse(p.getHand().contains(card2));



        /*          Front card3  (sul corner dead di card2, dopo a sx di starter sotto a sx di card1)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      MUSHROOM       |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */


        //provo a posizionare card3 in alto a sx rispetto a card2 (non è possibile perché il corner di card2 è dead)
        p.getHand().get(p.getHand().indexOf(card3)).setFront(true);
        assertFalse(p.place(p.getHand().indexOf(card3), true, 40, 42));
        assertNotEquals(card3, p.getArea().getSpace(40,42).getCard());
        assertTrue(p.getArea().getSpace(40,42).isFree());
        assertTrue(p.getHand().contains(card3));

        //controllo intermedio per verificare dead spaces
        assertFalse(p.place(p.getHand().indexOf(card3), true, 40, 39));
        assertFalse(p.place(p.getHand().indexOf(card3), true, 39, 40));
        //fuori dai bound
        assertFalse(p.place(p.getHand().indexOf(card3), true, 37, 37));


        //la vado invece a posizionare nella posizione simmetrica(a sx di starter, sotto a sx di card2)
        assertTrue(p.place(p.getHand().indexOf(card3), true, 40, 38));
        System.out.println("Stampa la carta posizionata in [40][38]:");
        pr.cardPrinter(p.getArea().getSpace(40,38).getCard(), true);
        assertEquals(card3, p.getArea().getSpace(40,38).getCard());
        assertFalse(p.getArea().getSpace(40,38).isFree());
        assertFalse(p.getHand().contains(card3));

///STAMPARE DA QUI IN POI


        /*          Front card4 (sul corner dead di card3)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      BUTTERFLY      |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */
        //provo un altra carta Card4 tra card3 e starter (non deve andare perché corner di Card3 dead)
        p.addCard(card4);
        p.getHand().get(p.getHand().indexOf(card4)).setFront(true);
        assertFalse(p.place(p.getHand().indexOf(card4), true, 41, 39));
        assertNotEquals(card4, p.getArea().getSpace(41,39).getCard());
        assertTrue(p.getArea().getSpace(41,39).isFree());
        assertTrue(p.getHand().contains(card4));
        //LA PIAZZO invece IN BASSO A SX A Card3
        assertTrue(p.place(p.getHand().indexOf(card4), true, 41, 37));
        pr.cardPrinter(p.getArea().getSpace(41,37).getCard(), true);
        assertEquals(card4, p.getArea().getSpace(41,37).getCard());
        assertEquals(card4, p.getArea().getSpace(41, 37).getCard());
        assertFalse(p.getArea().getSpace(41,37).isFree());
        assertFalse(p.getHand().contains(card4));



        /*          Front card5 (sul corner dead di card3)
         *        _____________________
         *       | LEAF           LEAF |
         *       |                     |
         *       |      BUTTERFLY      |
         *       |                     |
         *       | free          dead  |
         *       |_____________________|
         *
         * */

        //controlliamo i corner di starter
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[0].getType().toString());
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[1].getType().toString());
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[2].getType().toString());
        System.out.println(p.getArea().getSpace(40, 40).getCard().getBackCorners()[3].getType().toString());

        p.addCard(card5);
        p.getHand().get(p.getHand().indexOf(card5)).setFront(true);
        assertTrue(p.place(p.getHand().indexOf(card5), true, 41, 41));
        pr.cardPrinter(p.getArea().getSpace(41,41).getCard(), true);
        assertEquals(card5, p.getArea().getSpace(41,41).getCard());
        assertEquals(card5, p.getArea().getSpace(41,41).getCard());
        assertFalse(p.getArea().getSpace(41,41).isFree());
        assertFalse(p.getHand().contains(card5));


        //la situazione sul playground dovrebbe essere questa:

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
     79

*/

    }
}
