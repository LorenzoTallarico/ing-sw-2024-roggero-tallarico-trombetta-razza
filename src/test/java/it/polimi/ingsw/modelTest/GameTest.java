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

        Player fake1= new Player("Marco");
        players.add(fake1);
        Player fake2= new Player("Luca");
        players.add(fake2);
        Player fake3= new Player("Andrea");
        players.add(fake3);
        Player fake4= new Player("Paolo");
        players.add(fake4);
        Game testGame= Game.getInstance(players);
        // Verifica che ogni giocatore abbia un colore assegnato
        for (Player player : testGame.getPlayers()) {
            assertNotEquals(Color.NONE, player.getColor(), "Il colore del giocatore non è stato assegnato correttamente");
        }

        // Verifica che i colori dei giocatori siano diversi
        for (int i = 0; i < testGame.getPlayersNumber(); i++) {
            for (int j = i + 1; j < testGame.getPlayersNumber(); j++) {
                assertNotEquals(testGame.getPlayers().get(i).getColor(), testGame.getPlayers().get(j).getColor(), "I colori dei giocatori non sono diversi");
            }
        }
    }


    /*void testStart(){

    }*/
}
