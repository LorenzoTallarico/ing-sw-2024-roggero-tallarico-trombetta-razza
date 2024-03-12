package it.polimi.ingsw.model;
import java.util.ArrayList;

public class Game {
    ArrayList<Player> players = new ArrayList<Player>();
    ArrayList<ResourceCard> resourceDeck= new ArrayList<ResourceCard>();
    ArrayList<GoldCard> goldDeck= new ArrayList<GoldCard>();
    Arraylist<AchievementCard> achievementDeck = new ArrayList<AchievementCard>();
    boolean started;
    boolean ended;
    int currPlayer;
    ArrayList<ResourceCard> commonResource = new ArrayList<ResourceCard>();
    ArrayList<GoldCard> commonGold = new ArrayList<GoldCard>();
    ArrayList<AchievementCard> commonAchievement = new ArrayList<AchievementCard>();
    int playersNumber;

    public Game(Arraylist<AchievementCard> achievementDeck, ArrayList<ResourceCard> commonResource, ArrayList<GoldCard> commonGold, ArrayList<AchievementCard> commonAchievement, int playersNumber) {
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
