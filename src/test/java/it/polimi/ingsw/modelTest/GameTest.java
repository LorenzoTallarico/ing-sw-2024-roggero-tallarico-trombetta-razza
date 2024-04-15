package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.Game;
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
    }


    /*void testStart(){

    }*/
}
