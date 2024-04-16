package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.GameState;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Color;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class GameTest {

    @Test
    void IntegrityTest(){
        ArrayList<Player> players= new ArrayList<Player>();

        Player fake1= new Player("Marco", false);
        players.add(fake1);
        Player fake2= new Player("Luca", false);
        players.add(fake2);
        Player fake3= new Player("Andrea", false);
        players.add(fake3);
        Player fake4= new Player("Paolo", false);
        players.add(fake4);
        Game testGame= Game.getInstance();
        testGame.addPlayers(players);
        // Verifica che ogni giocatore abbia un colore assegnato
        for (Player player : testGame.getPlayers()) {
            assertNotEquals(Color.NONE, player.getColor(), "Il colore del giocatore non è stato assegnato correttamente");
        }

        // Verifica che i colori dei giocatori siano diversi
        for (int i = 0; i < testGame.getPlayersNumber()-1; i++) {
            for (int j = i + 1; j < testGame.getPlayersNumber(); j++) {
                System.out.println(testGame.getPlayers().get(i).getColor());
                System.out.println(testGame.getPlayers().get(j).getColor());
                assertNotEquals(testGame.getPlayers().get(i).getColor(), testGame.getPlayers().get(j).getColor(), "I colori dei giocatori sono uguali");
            }
        }
        assertEquals(testGame.getGameState(), GameState.INIT);

    }

    @Test
    void nextStateTest(){
        Game testGame= Game.getInstance();
        GameState[] vetStati = new GameState[8];
        vetStati[0]= GameState.LOBBY ;
        vetStati[1]= GameState.INIT ;
        vetStati[2]= GameState.READY ;
        vetStati[3]= GameState.SELECTACHIEVEMENT ;
        vetStati[4]= GameState.GAME ;
        vetStati[5]= GameState.LASTROUND ;
        vetStati[6]= GameState.FINALSCORE ;
        vetStati[7]= GameState.END ;

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
        testGame.nextState();
        assertEquals(testGame.getGameState(), vetStati[7]);
    }

    @Test
    void nextPlayerTest(){
        Game testGame= Game.getInstance();
        GameState[] vetStati = new GameState[8];
        vetStati[0]= GameState.LOBBY ;
        vetStati[1]= GameState.INIT ;
        vetStati[2]= GameState.READY ;
        vetStati[3]= GameState.SELECTACHIEVEMENT ;
        vetStati[4]= GameState.GAME ;
        vetStati[5]= GameState.LASTROUND ;
        vetStati[6]= GameState.FINALSCORE ;
        vetStati[7]= GameState.END ;
        ArrayList<Player> players= new ArrayList<Player>();

        Player fake1= new Player("Marco", false);
        players.add(fake1);
        Player fake2= new Player("Luca", false);
        players.add(fake2);
        Player fake3= new Player("Andrea", false);
        players.add(fake3);
        Player fake4= new Player("Paolo", false);
        players.add(fake4);
        testGame.addPlayers(players);
        for(int i=2; i<vetStati.length; i++){
            for(int j=0; j<testGame.getPlayersNumber(); j++){
                assertEquals(vetStati[i], testGame.getGameState());
                int curr= testGame.getCurrPlayer();
                testGame.nextPlayer();
                assertEquals(vetStati[i], testGame.getGameState());
                if(curr==testGame.getPlayersNumber()-1)
                    assertEquals(testGame.getCurrPlayer(), 0);
                else
                    assertEquals(testGame.getCurrPlayer(), curr+1);
            }
            for(int j=0; j<testGame.getPlayersNumber(); j++){
                assertEquals(vetStati[i], testGame.getGameState());
                int curr= testGame.getCurrPlayer();
                testGame.nextPlayer(false);
                assertEquals(vetStati[i], testGame.getGameState());
                if(curr==testGame.getPlayersNumber()-1)
                    assertEquals(testGame.getCurrPlayer(), 0);
                else
                    assertEquals(testGame.getCurrPlayer(), curr+1);
            }

            for(int j=0; j<testGame.getPlayersNumber(); j++){
                assertEquals(vetStati[i], testGame.getGameState());
                int curr= testGame.getCurrPlayer();
                testGame.nextPlayer(true);
                if(j==testGame.getPlayersNumber()-1 && i<vetStati.length-1)
                    assertEquals(vetStati[i+1], testGame.getGameState());
                if(curr==testGame.getPlayersNumber()-1)
                    assertEquals(testGame.getCurrPlayer(), 0);
                else
                    assertEquals(testGame.getCurrPlayer(), curr+1);
            }
        }
    }
}
