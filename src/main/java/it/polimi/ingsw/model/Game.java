package it.polimi.ingsw.model;
import java.util.ArrayList;

public class Game {
    private ArrayList<Player> players = new ArrayList<Player>();
    private ArrayList<ResourceCard> resourceDeck= new ArrayList<ResourceCard>();
    private ArrayList<GoldCard> goldDeck= new ArrayList<GoldCard>();
    private ArrayList<AchievementCard> achievementDeck = new ArrayList<AchievementCard>();
    private boolean started;
    private boolean ended;
    private int currPlayer;
    private ArrayList<ResourceCard> commonResource = new ArrayList<ResourceCard>();
    private ArrayList<GoldCard> commonGold = new ArrayList<GoldCard>();
    private ArrayList<AchievementCard> commonAchievement = new ArrayList<AchievementCard>();
    private int playersNumber;

    public Game(ArrayList<AchievementCard> achievementDeck, ArrayList<ResourceCard> commonResource, ArrayList<GoldCard> commonGold, ArrayList<AchievementCard> commonAchievement, int playersNumber) {
        this.achievementDeck = achievementDeck;
        this.commonResource = commonResource;
        this.commonGold = commonGold;
        this.commonAchievement = commonAchievement;
        this.playersNumber = playersNumber;
    }

    public void shuffle(){

    }

    public void createHands(){

    }

    public void chooseOrder(){

    }

    public void start(){

    }

    public void nextPlayer(){

    }

    public void end(){

    }

}
