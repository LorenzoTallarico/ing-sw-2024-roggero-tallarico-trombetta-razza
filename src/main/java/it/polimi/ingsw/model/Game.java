package it.polimi.ingsw.model;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import com.google.gson.*;


public class Game {
    private ArrayList<Player> players;
    private ArrayList<ResourceCard> resourceDeck;
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<AchievementCard> achievementDeck;
    private boolean started;
    private boolean ended;
    private int currPlayer;
    private ArrayList<ResourceCard> commonResource;
    private ArrayList<GoldCard> commonGold;
    private ArrayList<AchievementCard> commonAchievement;
    private int playersNumber;

    public Game(ArrayList<Player> players) {
        createGoldDeck();
        //TO DO
        //createResourceDeck(), achievement, starter
        // STARTERCARD CLASS DOES NOT EXIST!!!
        //TO DO
        //Something to figure out for common gold, common resource, common...
        this.players = players;
        playersNumber = players.size();
        started = false;
        ended = false;
    }

    public void start(){
        started = true;
    }

    public void nextPlayer(){
        currPlayer++;
        if(currPlayer > playersNumber)
            currPlayer = 0;
    }

    public void end(){
        ended = true;
    }

    private void createGoldDeck(){
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            GoldCard[] tempGold = gson.fromJson(reader, GoldCard[].class);
            goldDeck = new ArrayList<GoldCard>();
            for(int i = 0; i < tempGold.length; i++)
                goldDeck.add(tempGold[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
