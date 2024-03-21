package it.polimi.ingsw.modelTest;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Color;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

public class GameTest {

    void IntegrityTestGame(){
        ArrayList<Player> players= new ArrayList<Player>();

        Player fake1= new Player("Marco");
        players.add(fake1);
        Player fake2= new Player("Luca");
        players.add(fake2);
        Player fake3= new Player("Andrea");
        players.add(fake3);
        Player fake4= new Player("Paolo");
        players.add(fake4);
        Game testGame= new Game(players);
        boolean check;
        if(testGame.getInstance().players.get(0).getColor()!=testGame.getInstance().players.get(1).getColor() &&
                testGame.getInstance().players.get(0).getColor()!=testGame.getInstance().players.get(2).getColor() &&
                testGame.getInstance().players.get(0).getColor()!=testGame.getInstance().players.get(3).getColor()){
            check=true;
        }
        check=false;
        if(testGame.getInstance().players.get(1).getColor()!=testGame.getInstance().players.get(2).getColor() &&
                testGame.getInstance().players.get(1).getColor()!=testGame.getInstance().players.get(3).getColor()){
            check=true;
        }
        check=false;
        if(testGame.getInstance().players.get(2).getColor()!=testGame.getInstance().players.get(3).getColor()){
            check=true;
        }
        check=false;

        for(int i=0; i<testGame.getInstance().playersNumber)
    }

    void testStart(){

    }
}
