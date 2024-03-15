package it.polimi.ingsw.model;
import java.util.ArrayList;


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

    public Game(ArrayList<ResourceCard> resourceDeck, ArrayList<GoldCard> goldDeck, ArrayList<AchievementCard> achievementDeck, ArrayList<ResourceCard> commonResource, ArrayList<GoldCard> commonGold, ArrayList<AchievementCard> commonAchievement, ArrayList<Player> players) {
        this.resourceDeck = resourceDeck;
        this.goldDeck = goldDeck;
        this.achievementDeck = achievementDeck;
        this.commonResource = commonResource;
        this.commonGold = commonGold;
        this.commonAchievement = commonAchievement;
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


}
