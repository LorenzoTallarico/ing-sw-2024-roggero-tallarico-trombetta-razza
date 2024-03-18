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
    private ArrayList<StarterCard> starterDeck;
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
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            GoldCard[] tempGold = gson.fromJson(reader, GoldCard[].class);
            goldDeck = new ArrayList<GoldCard>();
            for(int i = 0; i < tempGold.length; i++)
                goldDeck.add(tempGold[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createResourceDeck(){
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            resourceDeck = new ArrayList<ResourceCard>();
            for(int i = 0; i < tempResource.length; i++)
                resourceDeck.add(tempResource[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createAchievementDeck(){
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            achievementDeck = new ArrayList<AchievementCard>();
            for(int i = 0; i < tempAchievement.length; i++)
                achievementDeck.add(tempAchievement[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createStarterDeck(){
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/StarterCards.json")) {
            StarterCard[] tempStarter = gson.fromJson(reader, StarterCard[].class);
            starterDeck = new ArrayList<StarterCard>();
            for(int i = 0; i < tempStarter.length; i++)
                starterDeck.add(tempStarter[i]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }




}
