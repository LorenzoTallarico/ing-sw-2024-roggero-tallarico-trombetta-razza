package it.polimi.ingsw.model;

import java.io.*;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import com.google.gson.*;
import it.polimi.ingsw.networking.VirtualView;
import it.polimi.ingsw.listener.Listener;

/**
 * Represents the main game instance with its state and components.
 * This class manages players, modifications, decks of cards, game state, and communication with clients.
 */
public class Game implements Serializable {

    /**
     * Singleton instance of the game.
     */
    private static Game instance;

    /**
     * List of players in the game.
     */
    private static ArrayList<Player> players;


    /**
     * Deck of resource cards.
     */
    private ArrayList<ResourceCard> resourceDeck;

    /**
     * Deck of gold cards.
     */
    private ArrayList<GoldCard> goldDeck;

    /**
     * Deck of achievement cards.
     */
    private ArrayList<AchievementCard> achievementDeck;

    /**
     * Deck of starter cards.
     */
    private ArrayList<StarterCard> starterDeck;

    /**
     * Index of the current player.
     */
    private int currPlayer;

    /**
     * Common resource cards on the table.
     */
    private ArrayList<ResourceCard> commonResource;

    /**
     * Common gold cards on the table.
     */
    private ArrayList<GoldCard> commonGold;

    /**
     * Common achievements.
     */
    private ArrayList<AchievementCard> commonAchievement;

    /**
     * Number of players in the game.
     */
    private int playersNumber;

    /**
     * Current state of the game.
     */
    private GameState gameState;

    /**
     * List of connected clients (virtual views).
     */
    private ArrayList<VirtualView> clients;

    /**
     * Listener for game events.
     */
    private it.polimi.ingsw.listener.Listener bigListener;

    /**
     * Flag indicating if the game is in a waiting state.
     */
    private boolean wait;



    /**
     * Private constructor to enforce singleton pattern.
     */
    private Game() {
        players = new ArrayList<>();
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        gameState = GameState.LOBBY;
        wait = false;
    }

    /**
     * Handles the reconnection of a player with a new VirtualView instance.
     * Updates the player's VirtualView and notifies listeners accordingly.
     *
     * @param nickname       The nickname of the player reconnecting.
     * @param oldVirtualView The old VirtualView instance associated with the player.
     * @param newVirtualView The new VirtualView instance to associate with the player.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void reconnection(String nickname, VirtualView oldVirtualView, VirtualView newVirtualView) throws RemoteException {
        for(Player p : players) {
            if(nickname.equalsIgnoreCase(p.getName())) {
                int index = clients.indexOf(oldVirtualView);
                clients.remove(index);
                clients.add(index, newVirtualView);
                p.setOnline(true);
                if(!newVirtualView.getStarter()) {
                    ArrayList<Player> player = new ArrayList<>();
                    player.add(p);
                    bigListener.notifyStarterCard(player, commonGold, commonResource, goldDeck.get(0).getResource(), resourceDeck.get(0).getResource());
                } else {
                    bigListener.notifyReconnection(nickname, players, commonGold, commonResource, commonAchievement, goldDeck.get(0).getResource(), resourceDeck.get(0).getResource());
                    if(wait){
                        wait = false;
                        nextPlayer();
                    }
                }
            }
        }
    }

    public void disconnection(String playerName) throws RemoteException {
        for(Player p: players) {
            if (p.getName().equalsIgnoreCase(playerName)) {
                p.setOnline(false);
                p.disconnection();

                for(VirtualView c: clients){
                    if(c.getNickname().equalsIgnoreCase(playerName)) {
                        c.setOnline(false);
                    }
                }
                int countOnline = 0;
                for(VirtualView c: clients){
                    if(c.getOnline())
                        countOnline++;
                }

                if(countOnline==0){
                    System.exit(0);
                }
                if(playerName.equalsIgnoreCase(players.get(currPlayer).getName())){
                    nextPlayer();
                }
                break;
            }
        }
    }
    public void nextPlayer() throws RemoteException {
        String nickLastPlayer = players.get(currPlayer).getName();
        boolean found = false;
        int index= currPlayer;
        do {
            currPlayer++;
            if (currPlayer >= playersNumber) {
                if (gameState == GameState.LASTROUND) {
                    setGameState(GameState.FINALSCORE);
                    return;
                } else
                    currPlayer = 0;
            }
            if (players.get(currPlayer).isOnline())
                found = true;
        } while (index != currPlayer && !found);

        if(players.get(currPlayer).isOnline() && index!=currPlayer) {
            bigListener.notifyToPlace(players.get(currPlayer));
        }
        if(index == currPlayer)
            wait = true;

    }




//Singleton Methods
    /**
     *
     * @return Game.instance
     */
    public static Game getInstance() {
        if(instance == null) {
            instance = new Game();
        }
        return instance;
    }

    private void dispose(){
        instance = null;
        currPlayer = 0;
        playersNumber = 0;
        players = new ArrayList<>();
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        gameState = GameState.LOBBY;
    }


//GET

//    public ArrayList<AchievementCard> getCommonAchievement() {
//        return commonAchievement;
//    }

    public ArrayList<GoldCard> getCommonGold(){
        return this.commonGold;
    }

    public ArrayList<ResourceCard> getCommonResource(){
        return this.commonResource;
    }

    public Listener getListener(){
        return this.bigListener;
    }

    public ArrayList<GoldCard> getGoldDeck() {
        return goldDeck;
    }

    public ArrayList<ResourceCard> getResourceDeck() {
        return resourceDeck;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gs) throws RemoteException {
        gameState = gs;
        if(gameState.equals(GameState.GAME)) {
            bigListener.notifyToPlace(players.get(getCurrPlayer()));
        } else if(gameState.equals(GameState.FINALSCORE))
            calculateEndPoints();
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

//POP Method
    public AchievementCard popAchievementCard() {
        AchievementCard achievement = null;
        if (!achievementDeck.isEmpty()) {
            achievement = achievementDeck.get(0);
            achievementDeck.remove(0);
        }
        return achievement;
    }

    public GoldCard popGoldCard() {
        GoldCard card = null;
        if(!goldDeck.isEmpty()) {
            card = goldDeck.get(0);
            goldDeck.remove(0);
        }
        return card;
    }

    public ResourceCard popResourceCard() {
        ResourceCard card = null;
        if(!resourceDeck.isEmpty()) {
            card = resourceDeck.get(0);
            resourceDeck.remove(0);
        }
        return card;
    }

    public StarterCard popStarterCard() {
        StarterCard starter = null;
        if(!starterDeck.isEmpty()) {
            starter = starterDeck.get(0);
            starterDeck.remove(0);
        }
        return starter;
    }


    //INIT GAME Methods

    private void createGoldDeck() {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(Game.class.getResourceAsStream("GoldCards.json")));) {
            GoldCard[] tempGold = gson.fromJson(reader, GoldCard[].class);
            goldDeck = new ArrayList<>();
            Collections.addAll(goldDeck, tempGold);
        } catch (IOException ignored) { }
        Collections.shuffle(goldDeck);
        commonGold = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            commonGold.add(popGoldCard());
    }

    private void createResourceDeck() {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(Game.class.getResourceAsStream("ResourceCards.json")));) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            resourceDeck = new ArrayList<>();
            Collections.addAll(resourceDeck, tempResource);
        } catch (IOException ignored) { }
        Collections.shuffle(resourceDeck);
        commonResource = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            commonResource.add(popResourceCard());
    }

    private void createAchievementDeck() {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(Game.class.getResourceAsStream("AchievementCards.json")));) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            achievementDeck = new ArrayList<>();
            for (AchievementCard achievementCard : tempAchievement)
                achievementDeck.add(new AchievementCard(achievementCard.getPoints(), achievementCard.getResource(), achievementCard.getStrategyType(), achievementCard.getItem(), achievementCard.getID()));
        } catch (IOException ignored) { }
        Collections.shuffle(achievementDeck);
        commonAchievement = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            commonAchievement.add(popAchievementCard());
    }

    private void createStarterDeck() {
        Gson gson = new Gson();
        try (Reader reader = new InputStreamReader(Objects.requireNonNull(Game.class.getResourceAsStream("StarterCards.json")));) {
            StarterCard[] tempStarter = gson.fromJson(reader, StarterCard[].class);
            starterDeck = new ArrayList<>();
            Collections.addAll(starterDeck, tempStarter);
        } catch (IOException ignored) { }
        Collections.shuffle(starterDeck);
    }

    /**
     * Method that given an arraylist of players, assign them a color,
     * shuffle their order and add em to the game
     * @param players arraylist of players who will play in the game
     */
    public void addPlayers(ArrayList<Player> players, ArrayList<VirtualView> clients) throws RemoteException {
        if(Game.players.isEmpty()) {
            assignColors(players);
            Collections.shuffle(players);
            Game.players.addAll(players);
            this.playersNumber = players.size();
            this.clients = clients;
        }
        init();
    }

    private void init() throws RemoteException {
        gameState = GameState.INIT;
        bigListener = new Listener(clients);
        createHands();
        currPlayer = 0;
        gameState = GameState.READY;
    }


    public void setStarterCard(String playerName, boolean front) throws RemoteException {
        for(Player player : players) {
            if(player.getName().equalsIgnoreCase(playerName)) {
                StarterCard tempSC = (StarterCard) player.getArea().getSpace(40, 40).getCard();
                tempSC.setFront(front);
                player.setArea(new Playground());
                player.getArea().setSpace(tempSC, 40, 40);
                bigListener.notifyAchievementChoice(playerName, player.getSecretAchievement(), commonAchievement);
            }
        }
    }

    /**
     *{@summary this function creates an array list named hand with
     * a pseudo-pop and add the new hand to player, for each player in game
     * use it only after create and shuffle gold and resource decks}
     * 2 resource and 1 gold in hand + 2 secretAchievement;
     **/
    private void createHands() throws RemoteException {
        for(int i = 0; i < playersNumber; i++) {
            ArrayList<Card> hand = new ArrayList<>();
            ArrayList<AchievementCard> secretAchievement = new ArrayList<>();
            for(int j = 0; j < 2; j++)
                hand.add(popResourceCard());
            hand.add(popGoldCard());
            players.get(i).setHand(hand);
            for(int j = 0; j < 2; j++)
                secretAchievement.add(popAchievementCard());
            players.get(i).setSecretAchievement(secretAchievement);
            players.get(i).getArea().setSpace(popStarterCard(), 40, 40);
        }
        bigListener.notifyStarterCard(players, commonGold, commonResource, goldDeck.get(0).getResource(), resourceDeck.get(0).getResource());
    }


    //Methods






    //NB: SOLO PER TESTING!!!!

    public void setCurrPlayer(Player p){
        this.currPlayer = players.indexOf(p);
    }


    public void nextState() {
        switch(gameState) {
            case LOBBY:
                gameState = GameState.INIT;
                break;
            case INIT:
                gameState = GameState.READY;
                break;
            case READY:
                gameState = GameState.SELECTACHIEVEMENT;
                break;
            case SELECTACHIEVEMENT:
                gameState = GameState.GAME;
                break;
            case GAME:
                gameState = GameState.LASTROUND;
                break;
            case LASTROUND:
                gameState = GameState.FINALSCORE;
                break;
            case FINALSCORE:
                gameState = GameState.END;
                break;
            case END:
                dispose();
                break;
        }
    }

    //only for testing, useful for launching the dispose method
    public void end() {
        gameState = GameState.END;
    }

    /**
     * Given the name of a player and an index representing his choice,
     * this method removes a card from the table and adds it to the hand of the player
     * @param index integer representing the choice, 1-2 gold , 3-4 resources, 5-6 decks
     * @return Card or null if indexOutOfBound or position empty
     */
    public Card draw(String name, int index) throws RemoteException {
        Player tempPlayer = null;
        //
        for(Player plyr : players) {
            if(plyr.getName().equalsIgnoreCase(name))
                tempPlayer = plyr;
        }
        if(tempPlayer == null) //error, player not existing, shouldn't happen, client can send draw action only if asked to
            return null;
        Card drawCard;
        switch(index) {
            case 1: // gold cards on the table
            case 2:
                drawCard = commonGold.get(index - 1);
                commonGold.remove(index-1);
                commonGold.add(popGoldCard());
                break;
            case 3: // resource cards on the table
            case 4:
                drawCard = commonResource.get(index - 3);
                commonResource.remove(index - 3);
                commonResource.add(popResourceCard());
                break;
            case 5: // gold card on top of the gold deck
                drawCard = popGoldCard();
                break;
            case 6: // gold card on top of the gold deck
                drawCard = popResourceCard();
                break;
            default: //error shouldn't happen, clients check index
                return null;
        }
        tempPlayer.getHand().add(drawCard);
        tempPlayer.setLastCardPlaced(null);      //player terminated his turn, last card reset (disconnections)
        tempPlayer.setPointsFromLastCard(0);     //player terminated his turn, last card's points reset (disconnections)
        bigListener.notifyDrawCompleted(tempPlayer, drawCard, getCommonGold(), getGoldDeck().get(0).getResource(),
                getCommonResource(), getResourceDeck().get(0).getResource());
        return drawCard;
    }

    /**
     * Method that randomly assign colors to players
     * @param players : ArrayList of Player
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
                }
                if(!find) {
                    players.get(i).setColor(Color.RED);
                }
            }
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if(players.get(j).getColor().equals(Color.YELLOW)) {
                        find = true;
                    }
                }
                if(!find) {
                    players.get(i).setColor(Color.YELLOW);
                }
            }
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if(players.get(j).getColor().equals(Color.BLUE)) {
                        find = true;
                    }
                }
                if(!find) {
                    players.get(i).setColor(Color.BLUE);
                }
            }
            if(players.get(i).getColor().equals(Color.NONE)) {
                find = false;
                for(int j = 0; j < players.size() && !find; j++) {
                    if(players.get(j).getColor().equals(Color.GREEN)) {
                        find = true;
                    }
                }
                if(!find) {
                    players.get(i).setColor(Color.GREEN);
                }
            }
        }
    }

    public void calculateEndPoints() throws RemoteException {
        int[] points = new int[players.size()];
        int max = 0, totalMax = 0;
        for(int i = 0; i < players.size(); i++) {
            points[i] += players.get(i).getSecretAchievement().get(0).calculatePoints(players.get(i));
            points[i] += commonAchievement.get(0).calculatePoints(players.get(i));
            points[i] += commonAchievement.get(1).calculatePoints(players.get(i));
            if(points[i] > max)
                max = points[i];
            players.get(i).addPoints(points[i]);
            if(players.get(i).getPoints() > totalMax)
                totalMax = players.get(i).getPoints();
        }
        int checkTie = 0;
        for(int i = 0; i < players.size() && checkTie < 2; i++)
            if(players.get(i).getPoints() == totalMax)
                checkTie++;
        if(checkTie > 1) { // two or more players have the same amount of points
            for(int i = 0; i < players.size(); i++)
                players.get(i).setWinner(players.get(i).getPoints() == totalMax && points[i] == max);
        } else { // there is no tie in total score
            for (Player player : players) player.setWinner(player.getPoints() == totalMax);
        }
        bigListener.notifyWinners(players);
    }


}
