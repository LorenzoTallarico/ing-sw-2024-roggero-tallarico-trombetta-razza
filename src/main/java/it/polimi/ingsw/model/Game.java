package it.polimi.ingsw.model;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.*;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.listener.Listener;


public class Game implements Serializable {

    private static Game instance;
    private static ArrayList<Player> players;
    private ArrayList<ResourceCard> resourceDeck;
    private ArrayList<GoldCard> goldDeck;
    private ArrayList<AchievementCard> achievementDeck;
    private ArrayList<StarterCard> starterDeck;
    private int currPlayer;
    private ArrayList<ResourceCard> commonResource;
    private ArrayList<GoldCard> commonGold;
    private ArrayList<AchievementCard> commonAchievement;
    private int playersNumber;
    private GameState gameState;
    private ArrayList<VirtualView> clients;
    private it.polimi.ingsw.listener.Listener bigListener;
    private boolean wait;




    //Constructor
    private Game() {
        players = new ArrayList<>();
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        gameState = GameState.LOBBY;
        wait = false;
    }



    public void reconnection(String nickname, VirtualView oldVirtualView, VirtualView newVirtualView) throws RemoteException {
        //non servono sti due if qua sotto credo (e quindi nemmeno i parametri)

        if(clients.contains(oldVirtualView)) {
            System.out.println("Vecchia virtual view c'è e siamo x(");
        }
        if(clients.contains(newVirtualView)) {
            System.out.println("Nuova virtual view c'è e siamo a cavallo");
        }
        for(Player p : players) {
            if(nickname.equalsIgnoreCase(p.getName())) {
                int index = clients.indexOf(oldVirtualView);
                clients.remove(index);
                clients.add(index, newVirtualView);
                System.out.println("flagStarter: " + newVirtualView.getStarter());
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
                System.err.println("dentro Game disconnection");
                p.disconnection();
                p.setOnline(false);
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
                System.out.println("*****(Dentro Game -> disconnection)********Il numero di giocatori online è: " + countOnline);
                if(countOnline == 1)
                    wait = true;
                if(countOnline==0){
                    System.exit(0); //
                }
                if(playerName.equalsIgnoreCase(players.get(currPlayer).getName())){
                    nextPlayer();
                }
                break;
            }
        }
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

    public ArrayList<ResourceCard> getOrderedResourceDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            ArrayList<ResourceCard> tempDeck = new ArrayList<>();
            Collections.addAll(tempDeck, tempResource);
            return tempDeck;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<GoldCard> getOrderedGoldDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            GoldCard[] tempResource = gson.fromJson(reader, GoldCard[].class);
            ArrayList<GoldCard> tempDeck = new ArrayList<>();
            Collections.addAll(goldDeck, tempResource);
            return tempDeck;
        } catch (IOException e) {
            return null;
        }
    }

    public ArrayList<AchievementCard> getOrderedAchievementDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            ArrayList<AchievementCard> tempDeck = new ArrayList<>();
            for (AchievementCard achievementCard : tempAchievement)
                tempDeck.add(new AchievementCard(achievementCard.getPoints(), achievementCard.getResource(), achievementCard.getStrategyType(), achievementCard.getItem(), achievementCard.getID()));
            return tempDeck;
        } catch (IOException e) {
            return null;
        }
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
            System.out.println("> Game started, first player is " + players.get(getCurrPlayer()).getName() + ".");
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
    if(!achievementDeck.isEmpty()) {
        achievement = achievementDeck.get(0);
        achievementDeck.remove(0);
    } else {
        System.err.println("! achievement deck null or empty");
    }
    return achievement;
}

    public GoldCard popGoldCard() {
        GoldCard card = null;
        if(!goldDeck.isEmpty()) {
            card = goldDeck.get(0);
            goldDeck.remove(0);
        } else {
            System.err.println("! gold deck null or empty");
        }
        return card;
    }

    public ResourceCard popResourceCard() {
        ResourceCard card = null;
        if(!resourceDeck.isEmpty()) {
            card = resourceDeck.get(0);
            resourceDeck.remove(0);
        } else {
            System.err.println("! resource deck null or empty");
        }
        return card;
    }

    public StarterCard popStarterCard() {
        StarterCard starter = null;
        if(!starterDeck.isEmpty()) {
            starter = starterDeck.get(0);
            starterDeck.remove(0);
        } else {
            System.err.println("! starter deck null or empty");
        }
        return starter;
    }


    //INIT GAME Methods

    private void createGoldDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            GoldCard[] tempGold = gson.fromJson(reader, GoldCard[].class);
            goldDeck = new ArrayList<>();
            Collections.addAll(goldDeck, tempGold);
        } catch (IOException e) {
            System.err.println("> Error: JSON files not found.");
        }
        Collections.shuffle(goldDeck);
        commonGold = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            commonGold.add(popGoldCard());
    }

    private void createResourceDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            resourceDeck = new ArrayList<>();
            Collections.addAll(resourceDeck, tempResource);
        } catch (IOException e) {
            System.err.println("> Error: JSON files not found.");
        }
        Collections.shuffle(resourceDeck);
        commonResource = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            commonResource.add(popResourceCard());
    }

    private void createAchievementDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            achievementDeck = new ArrayList<>();
            for (AchievementCard achievementCard : tempAchievement)
                achievementDeck.add(new AchievementCard(achievementCard.getPoints(), achievementCard.getResource(), achievementCard.getStrategyType(), achievementCard.getItem(), achievementCard.getID()));
        } catch (IOException e) {
            System.err.println("> Error: JSON files not found.");
        }
        Collections.shuffle(achievementDeck);
        commonAchievement = new ArrayList<>();
        for(int i = 0; i < 2; i++)
            commonAchievement.add(popAchievementCard());
    }

    private void createStarterDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/StarterCards.json")) {
            StarterCard[] tempStarter = gson.fromJson(reader, StarterCard[].class);
            starterDeck = new ArrayList<>();
            Collections.addAll(starterDeck, tempStarter);
        } catch (IOException e) {
            System.err.println("> Error: JSON files not found.");
        }
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

    public void nextPlayer() throws RemoteException {
        String nickLastPlayer = players.get(currPlayer).getName();
        boolean found = false;

        if(!wait) {
            do {   //player offline skips turn
                currPlayer++;
                if (currPlayer >= playersNumber) {
                    if (gameState == GameState.LASTROUND) {
                        setGameState(GameState.FINALSCORE);
                        return;
                    } else
                        currPlayer = 0;
                }
//                if (clients.get(currPlayer).getOnline())
//                    found = true;
                if (players.get(currPlayer).isOnline())
                    found = true;
                System.out.println("onlineCurr: " + clients.get(currPlayer).getOnline() + " p: " + players.get(currPlayer).getName() + clients.get(currPlayer).getNickname());

            } while (/*clients.get(currPlayer).getOnline() == false ||*/ players.get(currPlayer).getName().equals(nickLastPlayer) || !found);
            bigListener.notifyToPlace(players.get(currPlayer));
        }
    }




    //NB: SOLO PER TESTING!!!!
    public void nextPlayer(boolean nextState) throws RemoteException {
        currPlayer++;
        if(currPlayer >= playersNumber) {
            currPlayer = 0;
            if(!clients.get(currPlayer).getOnline()) {
                currPlayer++;
                System.out.println(" ---------------------->>>>>>    nextplayer(nexstate) dentro if offline");
            }
            if(nextState) {
                if(gameState == GameState.GAME) {
                    for(Player p : players) {
                        if(p.getPoints() >= 20) {
                            nextState();
                            return;
                        }
                    }
                }
                else
                    nextState();
            }
        }
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
        bigListener.notifyDrawCompleted(tempPlayer, drawCard);
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









    //**********  METODI PER TESTARE SOCKET (da togliere)  ***********
    Integer state = 0;

    public boolean add(Integer number) {
        this.state += number;
        return true;
    }

    public Integer get() {
        return this.state;
    }

}
