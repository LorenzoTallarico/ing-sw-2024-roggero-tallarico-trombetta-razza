package it.polimi.ingsw.model;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.*;


public class Game {
    private static Game istance;
    private static ArrayList<Player> players;
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

    private Game(ArrayList<Player> players) {
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        this.players = players;
        playersNumber = players.size();
        started = false;
        ended = false;
    }

    public static Game getIstance(){
        if(istance == null){
            istance = new Game(players);
        }
        return istance;
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
        Collections.shuffle(goldDeck);
        commonGold = new ArrayList<GoldCard>();
        for(int i = 0; i < 2; i++) {
            commonGold.add(goldDeck.get(0));
            goldDeck.remove(0);
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
        Collections.shuffle(resourceDeck);
        commonResource = new ArrayList<ResourceCard>();
        for(int i = 0; i < 2; i++) {
            commonResource.add(resourceDeck.get(0));
            resourceDeck.remove(0);
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
        Collections.shuffle(achievementDeck);
        commonAchievement = new ArrayList<AchievementCard>();
        for(int i = 0; i < 2; i++) {
            commonAchievement.add(achievementDeck.get(0));
            achievementDeck.remove(0);
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
        Collections.shuffle(starterDeck);
    }

    /**
     *{@summary this function creates an array list named hand with
     * a pseudo-pop and add the new hand to player, for each player in game
     * use it only after create and shuffle gold and resource decks}
     * 2 resource and 1 gold in hand + 2 secretAchievement;
     **/
    private void createHands(){

        for(int i=0; i<playersNumber; i++) {
            ArrayList<Card> hand = new ArrayList<Card>();
            ArrayList<AchievementCard> secretAchievement = new ArrayList<AchievementCard>();

            hand.add(resourceDeck.get(0));
            resourceDeck.remove(0);
            hand.add(resourceDeck.get(0));
            resourceDeck.remove(0);
            hand.add(goldDeck.get(0));
            goldDeck.remove(0);
            players.get(i).setHand(hand);

            secretAchievement.add(achievementDeck.get(0));
            achievementDeck.remove(0);
            secretAchievement.add(achievementDeck.get(0));
            achievementDeck.remove(0);
            players.get(i).setSecretAchievement(secretAchievement);
        }
    }



}
