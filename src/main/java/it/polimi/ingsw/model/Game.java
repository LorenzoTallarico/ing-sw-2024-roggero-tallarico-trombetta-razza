package it.polimi.ingsw.model;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.*;


public class Game {
    private static Game instance;
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
    private final int playersNumber;

    private Game(ArrayList<Player> players) {
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        assignColors(players);
        Collections.shuffle(players);
        Game.players = players;
        playersNumber = players.size();
        started = false;
        ended = false;
    }

    //GETTER

    /**
     *
     * @return Game.instance
     */
    public static Game getInstance() {
        return instance;
    }

    /**
     * //Overloading
     * @param players
     * @return Game.instance
     */
    public static Game getInstance(ArrayList<Player> players) {
        if(instance == null) {
            instance = new Game(players);
        }
        return instance;
    }

    public static ArrayList<Player> getPlayers() {
        return players;
    }

    public boolean isStarted() {
        return started;
    }

    public boolean isEnded() {
        return ended;
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

    //METHODS

    public void start() {
        started = true;
    }

    public void nextPlayer() {
        currPlayer++;
        if(currPlayer >= playersNumber)
            currPlayer = 0;
    }

    public void end() {
        ended = true;
    }

    private void createGoldDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            GoldCard[] tempGold = gson.fromJson(reader, GoldCard[].class);
            goldDeck = new ArrayList<GoldCard>();
            Collections.addAll(goldDeck, tempGold);
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

    private void createResourceDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            resourceDeck = new ArrayList<ResourceCard>();
            Collections.addAll(resourceDeck, tempResource);
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

    private void createAchievementDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            achievementDeck = new ArrayList<AchievementCard>();
            Collections.addAll(achievementDeck, tempAchievement);
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

    private void createStarterDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/StarterCards.json")) {
            StarterCard[] tempStarter = gson.fromJson(reader, StarterCard[].class);
            starterDeck = new ArrayList<StarterCard>();
            Collections.addAll(starterDeck, tempStarter);
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
    private void createHands() {

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

    /**
     *
     * @param index indice per selezione: convenzione 0 prima risorsa tavolo, 1 seconda risorsa tavolo, 2 mazzo risorse, 3 prima gold tavolo 4 seconda gold tavolo 5 mazzo gold
     * @return Card or null if indexOutOfBound or position empty
     */
    private Card draw(int index) {
        if(index>=0 && index<=5) {
            Card drawCard;
            if(index<3) {
                if(resourceDeck.get(index)!=null) {
                    drawCard=resourceDeck.get(index);
                    resourceDeck.remove(index);
                    return drawCard;
                } else
                    return null;
            } else {
                index -= 3;
                if(goldDeck.get(index) != null) {
                    drawCard = goldDeck.get(index);
                    goldDeck.remove(index);
                    return drawCard;
                } else
                    return null;
            }
        }
        return null; //outOfBound
    }

    /**
     *
     * @param players : ArrayList of Player
     * {@summary Assign a random color at player that has Color.NONE as its color attribute}
     */
    private void assignColors(ArrayList<Player> players) {
        boolean find;
        for(int i = 0; i < players.size(); i++) {
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if (players.get(j).getColor().equals(Color.RED)) {
                        find = true;
                    }
                    if(!find) {
                        players.get(i).setColor(Color.RED);
                    }
                }
            }
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if(players.get(j).getColor().equals(Color.YELLOW)) {
                        find = true;
                    }
                    if(!find) {
                        players.get(i).setColor(Color.YELLOW);
                    }
                }
            }
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if(players.get(j).getColor().equals(Color.BLUE)) {
                        find = true;
                    }
                    if(!find) {
                        players.get(i).setColor(Color.BLUE);
                    }
                }
            }
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if(players.get(j).getColor().equals(Color.GREEN)) {
                        find = true;
                    }
                    if(!find) {
                        players.get(i).setColor(Color.GREEN);
                    }
                }
            }
        }
    }

}
