package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.*;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;

public class GameTest {

    /*
    NB to Dispose Singleton use these commands at the end of method:
        testGame.end();
        testGame.nextState();
     */

    @Test
    void IntegrityTest() {
        ArrayList<Player> players = new ArrayList<Player>();

        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        Player fake2 = new Player("Luca", false);
        players.add(fake2);
        Player fake3 = new Player("Andrea", false);
        players.add(fake3);
        Player fake4 = new Player("Paolo", false);
        players.add(fake4);
        Game testGame = Game.getInstance();
        testGame.addPlayers(players);
        // Verifica che ogni giocatore abbia un colore assegnato
        for (Player player : testGame.getPlayers()) {
            assertNotEquals(Color.NONE, player.getColor(), "Il colore del giocatore non è stato assegnato correttamente");
        }

        // Verifica che i colori dei giocatori siano diversi
        for (int i = 0; i < testGame.getPlayersNumber() - 1; i++) {
            for (int j = i + 1; j < testGame.getPlayersNumber(); j++) {
                System.out.println(testGame.getPlayers().get(i).getColor());
                System.out.println(testGame.getPlayers().get(j).getColor());
                assertNotEquals(testGame.getPlayers().get(i).getColor(), testGame.getPlayers().get(j).getColor(), "I colori dei giocatori sono uguali");
            }
        }
        assertEquals(testGame.getGameState(), GameState.READY);
        //To reset instance
        while (!testGame.getGameState().equals(GameState.END)) {
            testGame.nextState();
        }
        testGame.nextState();
        assertEquals(testGame.getGameState(), GameState.LOBBY);
        assertTrue(testGame.getPlayers().isEmpty());
        assertEquals(testGame.getPlayersNumber(), 0);
        assertEquals(testGame.getCurrPlayer(), 0);
    }

    @Test
    void nextStateTest() {
        Game testGame = Game.getInstance();
        GameState[] vetStati = new GameState[8];
        vetStati[0] = GameState.LOBBY;
        vetStati[1] = GameState.INIT;
        vetStati[2] = GameState.READY;
        vetStati[3] = GameState.SELECTACHIEVEMENT;
        vetStati[4] = GameState.GAME;
        vetStati[5] = GameState.LASTROUND;
        vetStati[6] = GameState.FINALSCORE;
        vetStati[7] = GameState.END;

        assertEquals(testGame.getGameState(), vetStati[0]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[1]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[2]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[3]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[4]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[5]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[6]);
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[7]);
        //Dispose GameTest
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[0]);
        assertTrue(testGame.getPlayers().isEmpty());
        assertEquals(testGame.getPlayersNumber(), 0);
        assertEquals(testGame.getCurrPlayer(), 0);

    }

    @Test
    void nextPlayerTest() {
        Game testGame = Game.getInstance();
        GameState[] vetStati = new GameState[8];
        vetStati[0] = GameState.LOBBY;
        vetStati[1] = GameState.INIT;
        vetStati[2] = GameState.READY;
        vetStati[3] = GameState.SELECTACHIEVEMENT;
        vetStati[4] = GameState.GAME;
        vetStati[5] = GameState.LASTROUND;
        vetStati[6] = GameState.FINALSCORE;
        vetStati[7] = GameState.END;
        ArrayList<Player> players = new ArrayList<Player>();

        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        Player fake2 = new Player("Luca", false);
        players.add(fake2);
        Player fake3 = new Player("Andrea", false);
        players.add(fake3);
        Player fake4 = new Player("Paolo", false);
        players.add(fake4);
        testGame.addPlayers(players);
        testGame.getPlayers().get(0).addPoints(20);
        for (int i = 2; i < vetStati.length; i++) {
            for (int j = 0; j < testGame.getPlayersNumber(); j++) {
                assertEquals(vetStati[i], testGame.getGameState());
                int curr = testGame.getCurrPlayer();
                testGame.nextPlayer();
                assertEquals(vetStati[i], testGame.getGameState());
                if (curr == testGame.getPlayersNumber() - 1)
                    assertEquals(testGame.getCurrPlayer(), 0);
                else
                    assertEquals(testGame.getCurrPlayer(), curr + 1);
            }
            for (int j = 0; j < testGame.getPlayersNumber(); j++) {
                assertEquals(vetStati[i], testGame.getGameState());
                int curr = testGame.getCurrPlayer();
                testGame.nextPlayer(false);
                assertEquals(vetStati[i], testGame.getGameState());
                if (curr == testGame.getPlayersNumber() - 1)
                    assertEquals(testGame.getCurrPlayer(), 0);
                else
                    assertEquals(testGame.getCurrPlayer(), curr + 1);
            }

            for (int j = 0; j < testGame.getPlayersNumber(); j++) {
                assertEquals(vetStati[i], testGame.getGameState());
                int curr = testGame.getCurrPlayer();
                if (testGame.getGameState().equals(GameState.END) && j == testGame.getPlayersNumber() - 1) {
                    testGame.nextPlayer(true);
                } else {
                    testGame.nextPlayer(true);
                    if (j == testGame.getPlayersNumber() - 1 && i < vetStati.length - 1)
                        assertEquals(vetStati[i + 1], testGame.getGameState());
                    if (curr == testGame.getPlayersNumber() - 1)
                        assertEquals(testGame.getCurrPlayer(), 0);
                    else
                        assertEquals(testGame.getCurrPlayer(), curr + 1);
                }

            }
        }
        testGame.end();
        testGame.nextState();
    }

    @Test
    void handsTest() {
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        Player fake2 = new Player("Luca", false);
        players.add(fake2);
        Player fake3 = new Player("Andrea", false);
        players.add(fake3);
        Player fake4 = new Player("Paolo", false);
        players.add(fake4);
        testGame.addPlayers(players);

        // in questo for controllo che ogni giocatore abbia effettivamente due carte achievement in secretAchievement, due carte risorsa e una oro in hand
        for (int i = 0; i < testGame.getPlayersNumber(); i++) {
            assertEquals(players.get(i).getHand().size(), 3);
            assertEquals(players.get(i).getSecretAchievement().size(), 2);
            assertTrue(players.get(i).getSecretAchievement().get(0) instanceof AchievementCard);
            assertTrue(players.get(i).getSecretAchievement().get(1) instanceof AchievementCard);
            assertTrue(players.get(i).getHand().get(0) instanceof ResourceCard);
            assertTrue(players.get(i).getHand().get(1) instanceof ResourceCard);
            assertTrue(players.get(i).getHand().get(2) instanceof GoldCard);
            //in questo for controllo che non ci siano giocatori che hanno ricevuto la stessa identica carta
            for (int j = 0; j < testGame.getPlayersNumber() && j != i; j++) {
                assertFalse(players.get(i).getSecretAchievement().get(0).equals(players.get(j).getSecretAchievement().get(0)));
                assertFalse(players.get(i).getSecretAchievement().get(0).equals(players.get(j).getSecretAchievement().get(1)));
                assertFalse(players.get(i).getSecretAchievement().get(1).equals(players.get(j).getSecretAchievement().get(0)));
                assertFalse(players.get(i).getSecretAchievement().get(1).equals(players.get(j).getSecretAchievement().get(1)));
                assertFalse(players.get(i).getHand().get(0).equals(players.get(j).getHand().get(0)));
                assertFalse(players.get(i).getHand().get(0).equals(players.get(j).getHand().get(1)));
                assertFalse(players.get(i).getHand().get(0).equals(players.get(j).getHand().get(2)));
                assertFalse(players.get(i).getHand().get(1).equals(players.get(j).getHand().get(0)));
                assertFalse(players.get(i).getHand().get(1).equals(players.get(j).getHand().get(1)));
                assertFalse(players.get(i).getHand().get(1).equals(players.get(j).getHand().get(2)));
                assertFalse(players.get(i).getHand().get(2).equals(players.get(j).getHand().get(0)));
                assertFalse(players.get(i).getHand().get(2).equals(players.get(j).getHand().get(1)));
                assertFalse(players.get(i).getHand().get(2).equals(players.get(j).getHand().get(2)));
            }
        }
    }

    @Test
    void diagonalTest() {
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        testGame.addPlayers(players);
        ArrayList<Card> hand = new ArrayList<Card>(testGame.getResourceDeck());
        ArrayList<AchievementCard> hand2 = new ArrayList<AchievementCard>(testGame.getAchievementDeck());
        fake1.setHand(hand);
        Card tempCard;
        AchievementCard tempAchievement;
        tempCard = hand.get(0);
        hand.remove(0);
        //il player ha un mazzo con tutte le carte risorsa e oro (per comodità), vado a prendere 3 carte mushroom e le posiziono in diagonale
        while (tempCard.getResource() != Resource.MUSHROOM || tempCard.getFrontCorners()[0].getType() == CornerType.DEAD) {
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard, 40, 40);
        tempCard = hand.get(0);
        hand.remove(0);
        while (tempCard.getResource() != Resource.MUSHROOM || tempCard.getFrontCorners()[0].getType() == CornerType.DEAD) {
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard, 39, 41);
        tempCard = hand.get(0);
        hand.remove(0);
        while (tempCard.getResource() != Resource.MUSHROOM || tempCard.getFrontCorners()[0].getType() == CornerType.DEAD) {
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard, 38, 42);
        // assegno a tempAchievement la carta achievement relativa alla diagonale di mushroom
        tempAchievement = hand2.get(0);
        hand2.remove(0);
        while (tempAchievement.getResource() != Resource.MUSHROOM || !tempAchievement.getStrategyType().equals("ConcreteStrategyDiagonal")) {
            tempAchievement = hand2.get(0);
            hand2.remove(0);
        }
        tempAchievement.setPlayer(fake1);
        tempCard = hand.get(0);
        hand.remove(0);
        while (tempCard.getResource() != Resource.MUSHROOM || tempCard.getFrontCorners()[0].getType() == CornerType.DEAD) {
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard, 37, 43);
        tempCard = hand.get(0);
        hand.remove(0);
        while (tempCard.getResource() != Resource.MUSHROOM || tempCard.getFrontCorners()[0].getType() == CornerType.DEAD) {
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard, 36, 44);
        tempCard = hand.get(0);
        hand.remove(0);
        while (tempCard.getResource() != Resource.MUSHROOM || tempCard.getFrontCorners()[0].getType() == CornerType.DEAD) {
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard, 35, 45);
        fake1.addPoints(tempAchievement.calculatePoints());
        // verifico che dopo aver posizionato 6 carte mushroom in diagonale, siano stati ottenuti 4 punti
        assertEquals(fake1.getPoints(), 4);
    }
    @Test
    void lShapeTest(){
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        testGame.addPlayers(players);
        ArrayList<Card> hand = new ArrayList<Card>(testGame.getResourceDeck());
        ArrayList<AchievementCard> hand2 = new ArrayList<AchievementCard>(testGame.getAchievementDeck());
        fake1.setHand(hand);
        Card tempCard;
        AchievementCard tempAchievement, tempAchievement2;
        tempCard = hand.get(0);
        hand.remove(0);
        fake1.getArea().setSpace(tempCard,40,40);
        tempCard = hand.get(0);
        hand.remove(0);
        fake1.getArea().setSpace(tempCard,41,39);
        tempCard = hand.get(0);
        hand.remove(0);
        fake1.getArea().setSpace(tempCard,42,40);
        while(tempCard.getResource() != Resource.LEAF){
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard,43,41);
        //fake1.addPoints(tempAchievement.calculatePoints());
        //assertEquals(fake1.getPoints(),3);
        tempCard = hand.get(0);
        hand.remove(0);
        fake1.getArea().setSpace(tempCard,44,40);
        tempCard = hand.get(0);
        hand.remove(0);
        fake1.getArea().setSpace(tempCard,45,41);
        tempCard = hand.get(0);
        hand.remove(0);
        fake1.getArea().setSpace(tempCard,46,40);
        while(tempCard.getResource() != Resource.BUTTERFLY){
            tempCard = hand.get(0);
            hand.remove(0);
        }
        fake1.getArea().setSpace(tempCard,47,39);
        tempAchievement = hand2.get(0);
        hand2.remove(0);
        while(!tempAchievement.getStrategyType().equals("ConcreteStrategyLshape")){
            tempAchievement = hand2.get(0);
            hand2.remove(0);
        }
        tempAchievement.setPlayer(fake1);
        fake1.addPoints(tempAchievement.calculatePoints());
        tempAchievement2 = hand2.get(0);
        hand2.remove(0);
        while(!tempAchievement2.getStrategyType().equals("ConcreteStrategyLshape")){
            tempAchievement2 = hand2.get(0);
            hand2.remove(0);
        }
        tempAchievement2.setPlayer(fake1);
        fake1.addPoints(tempAchievement2.calculatePoints());
        assertEquals(fake1.getPoints(),6);
    }

    @Test
    void itemTest(){
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        testGame.addPlayers(players);
        ArrayList<Card> hand = new ArrayList<Card>(testGame.getResourceDeck());
        ArrayList<AchievementCard> hand2 = new ArrayList<AchievementCard>(testGame.getAchievementDeck());
        fake1.setHand(hand);
        Card tempCard;
        AchievementCard tempAchievement;
        tempCard = hand.get(4);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,40,40);
        tempCard = hand.get(14);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,41,41);
        tempCard = hand.get(26);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,42,42);
        tempCard = hand.get(34);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,43,43);
        tempAchievement = hand2.get(15);
        tempAchievement.setPlayer(fake1);
        fake1.addPoints(tempAchievement.calculatePoints());
        assertEquals(fake1.getPoints(),4);
    }
    @Test
    void resourceTest(){
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        testGame.addPlayers(players);
        ArrayList<Card> hand = new ArrayList<Card>(testGame.getResourceDeck());
        ArrayList<AchievementCard> hand2 = new ArrayList<AchievementCard>(testGame.getAchievementDeck());
        fake1.setHand(hand);
        Card tempCard;
        AchievementCard tempAchievement;
        tempCard = hand.get(0);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,40,40);
        tempCard = hand.get(1);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,39,41);
        tempCard = hand.get(2);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,40,42);
        tempAchievement = hand2.get(8);
        tempAchievement.setPlayer(fake1);
        fake1.addPoints(tempAchievement.calculatePoints());
        assertEquals(fake1.getPoints(),4);
    }
    @Test
    void mixedTest(){
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        testGame.addPlayers(players);
        ArrayList<Card> hand = new ArrayList<Card>(testGame.getResourceDeck());
        ArrayList<AchievementCard> hand2 = new ArrayList<AchievementCard>(testGame.getAchievementDeck());
        fake1.setHand(hand);
        Card tempCard;
        AchievementCard tempAchievement;
        tempCard = hand.get(4);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,40,40);
        tempCard = hand.get(5);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,41,41);
        tempCard = hand.get(6);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,40,42);
        tempCard = hand.get(14);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,41,43);
        tempCard = hand.get(15);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,40,44);
        tempCard = hand.get(16);
        tempCard.setFront(true);
        fake1.getArea().setSpace(tempCard,39,45);
        tempAchievement = hand2.get(12);
        tempAchievement.setPlayer(fake1);
        fake1.addPoints(tempAchievement.calculatePoints());
        assertEquals(fake1.getPoints(),6);
    }

    @Test
    void drawTest(){
        Game testGame = Game.getInstance();
        ArrayList<Player> players = new ArrayList<Player>();
        Player fake1 = new Player("Marco", false);
        players.add(fake1);
        testGame.addPlayers(players);
        Card card;
        int sizeResources = testGame.getResourceDeck().size();
        int sizeGold = testGame.getGoldDeck().size();
        assertNull(testGame.draw(-1));
        assertNull(testGame.draw(6));
        card = testGame.getResourceDeck().get(0);
        assertEquals(card, testGame.draw(2));
        assertInstanceOf(ResourceCard.class, card);
        assertInstanceOf(ResourceCard.class, testGame.draw(0));
        assertInstanceOf(ResourceCard.class, testGame.draw(1));
        assertEquals(sizeResources-3, testGame.getResourceDeck().size());
        card = testGame.getGoldDeck().get(0);
        assertEquals(card, testGame.draw(5));
        assertInstanceOf(GoldCard.class, card);
        assertInstanceOf(GoldCard.class, testGame.draw(3));
        assertInstanceOf(GoldCard.class, testGame.draw(4));
        assertEquals(sizeGold-3, testGame.getGoldDeck().size());
        testGame.end();
        testGame.nextState();
    }
}