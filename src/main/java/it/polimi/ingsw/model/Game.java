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

// Da capire se risulta utile per gson o per Client/Server interaction il 'Serializable'
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

//Constructor
    private Game() {
        players = new ArrayList<Player>();
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        gameState = GameState.LOBBY;
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
        players = new ArrayList<Player>();
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        gameState = GameState.LOBBY;
    }


//GET

    public ArrayList<GoldCard> getCommonGold(){
        return this.commonGold;
    }

    public ArrayList<ResourceCard> getCommonResource(){
        return this.commonResource;
    }

    public Listener getListener(){
        return this.bigListener;
    }

    public ArrayList<ResourceCard> getResourceDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/ResourceCards.json")) {
            ResourceCard[] tempResource = gson.fromJson(reader, ResourceCard[].class);
            resourceDeck = new ArrayList<ResourceCard>();
            Collections.addAll(resourceDeck, tempResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resourceDeck;
    }

    public ArrayList<GoldCard> getGoldDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/GoldCards.json")) {
            GoldCard[] tempResource = gson.fromJson(reader, GoldCard[].class);
            goldDeck = new ArrayList<GoldCard>();
            Collections.addAll(goldDeck, tempResource);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return goldDeck;
    }

    public ArrayList<AchievementCard> getAchievementDeck() {
        Gson gson = new Gson();
        try (Reader reader = new FileReader("src/main/resources/AchievementCards.json")) {
            AchievementCard[] tempAchievement = gson.fromJson(reader, AchievementCard[].class);
            achievementDeck = new ArrayList<AchievementCard>();
            for (AchievementCard achievementCard : tempAchievement)
                achievementDeck.add(new AchievementCard(achievementCard.getPoints(), achievementCard.getResource(), achievementCard.getStrategyType(), achievementCard.getItem()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return achievementDeck;
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
        }
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }

//POP Method
    public AchievementCard popAchievementCard() {
    AchievementCard secretAchievement = null;
    try {
        secretAchievement = achievementDeck.get(0);
        achievementDeck.remove(0);
    } catch (IndexOutOfBoundsException e) {
        // Gestione dell'eccezione: l'indice non esiste
        System.err.println("Errore: Impossibile rimuovere la carta dell'obiettivo. Deck vuoto o indice non valido.");
    }
    return secretAchievement;
}

    public GoldCard popGoldCard() {
        GoldCard card = null;
        try {
            card = goldDeck.get(0);
            goldDeck.remove(0);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta d'oro. Deck vuoto.");
        }
        return card;
    }

    public ResourceCard popResourceCard() {
        if (resourceDeck.isEmpty()) {
            System.err.println("Errore: Impossibile rimuovere la carta delle risorse. Deck vuoto.");
            return null;
        } else
            return resourceDeck.remove(0);
    }

    public StarterCard popStarterCard() {
        StarterCard starter = null;
        try {
            starter = starterDeck.get(0);
            starterDeck.remove(0);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta di avvio. Deck vuoto.");
        }
        return starter;
    }

    /*
    public AchievementCard popAchievementCard(int i) {
        AchievementCard secretAchievement = null;
        try {
            secretAchievement = achievementDeck.get(i);
            achievementDeck.remove(i);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta dell'obiettivo. Deck vuoto o indice non valido.");
        }
        return secretAchievement;
    }

    public ResourceCard popResourceCard(int i) {
        ResourceCard resource = null;
        try {
            resource = resourceDeck.get(i);
            resourceDeck.remove(i);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta delle risorse. Deck vuoto o indice non valido.");
        }
        return resource;
    }

    public StarterCard popStarterCard(int i) {
        StarterCard starter = null;
        try {
            starter = starterDeck.get(i);
            starterDeck.remove(i);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta di avvio. Deck vuoto o indice non valido.");
        }
        return starter;
    }

    public GoldCard popGoldCard(int i) {
        GoldCard card = null;
        try {
            card = goldDeck.get(i);
            goldDeck.remove(i);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta d'oro. Deck vuoto o indice non valido.");
        }
        return card;
    }
    */

    //INIT GAME Methods

    //NB: nei metodi di creazione dei deck non stiamo effettivamente instanziando alcuna carta(?) stiamo solo prendendo informazioni dal json
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
            for (AchievementCard achievementCard : tempAchievement)
                achievementDeck.add(new AchievementCard(achievementCard.getPoints(), achievementCard.getResource(), achievementCard.getStrategyType(), achievementCard.getItem()));
        } catch (IOException e) {
            e.printStackTrace();
        }
        Collections.shuffle(achievementDeck);
        commonAchievement = new ArrayList<AchievementCard>();
        for(int i = 0; i < 2; i++)
            commonAchievement.add(popAchievementCard());
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
                player.getArea().getSpace(40, 40).getCard().setFront(front);
                bigListener.notifyAchievementChoice(playerName, player.getSecretAchievement(), commonAchievement);
                // notify a tutti della starter card?
                //bigListener.updateArea(playername, player.getArea);
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
            ArrayList<Card> hand = new ArrayList<Card>();
            ArrayList<AchievementCard> secretAchievement = new ArrayList<AchievementCard>();
            for(int j = 0; j < 2; j++)
                hand.add(popResourceCard());
            hand.add(popGoldCard());
            players.get(i).setHand(hand);
            for(int j = 0; j < 2; j++)
                secretAchievement.add(popAchievementCard());
            players.get(i).setSecretAchievement(secretAchievement);
            players.get(i).getArea().setSpace(popStarterCard(), 40, 40);
        }
        bigListener.notifyStarterCard(players);
        bigListener.notifyHands(players);
    }


    //Methods

    public void nextPlayer() {
        currPlayer++;
        if(currPlayer >= playersNumber)
            currPlayer = 0;
    }

    /**
     * @overload
     * @param nextState
     */
    public void nextPlayer(boolean nextState) {
        currPlayer++;
        if(currPlayer >= playersNumber) {
            currPlayer = 0;
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
     *
     * @param index indice per selezione: convenzione 0 prima risorsa tavolo, 1 seconda risorsa tavolo, 2 mazzo risorse, 3 prima gold tavolo 4 seconda gold tavolo 5 mazzo gold
     * @return Card or null if indexOutOfBound or position empty
     */
    public Card draw(String name, int index) throws RemoteException {
        Player tempPlayer = null;
        for(Player plyr : players) {
            if(plyr.getName().equalsIgnoreCase(name))
                tempPlayer = plyr;
        }
        if(index >= 0 && index <= 5) {
            Card drawCard = null;
            if (index < 3) {
                if (index == 2) {
                    return popResourceCard();
                } else {
                    if (commonResource.size() > index) {
                        drawCard = commonResource.get(index);
                        commonResource.remove(index);
                        if (!resourceDeck.isEmpty())
                            commonResource.add(index, popResourceCard());
                    }
                    System.out.println("-- Game.draw() -- Nel model, sto chiamando la fine del metodo prima del listener");
                    bigListener.notifyDrawCompleted(name, drawCard, tempPlayer);
                    return drawCard;
                }
            } else{
                index -= 3;
                if(index == 2) {
                    return popGoldCard();
                } else {
                    if(commonGold.size() > index) {
                        drawCard = commonGold.get(index);
                        commonGold.remove(index);
                        if(!goldDeck.isEmpty())
                            commonGold.add(index, popGoldCard());
                    }
                    System.out.println("-- Game.draw() -- Nel model, sto chiamando la fine del metodo prima del listener");
                    bigListener.notifyDrawCompleted(name, drawCard, tempPlayer);
                    return drawCard;
                }
            }
        }
        return null; // out of bounds
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

    public void calculateEndPoints() {
        for (Player player : players) {
            player.addPoints(player.getSecretAchievement().get(0).calculatePoints());
            player.addPoints(commonAchievement.get(0).calculatePoints());
            player.addPoints(commonAchievement.get(1).calculatePoints());
        }
    }









    //**********  METODI PER TESTARE SOCKET ***********
    Integer state = 0;

    public boolean add(Integer number) {
        this.state += number;
        return true;
    }

    public Integer get() {
        return this.state;
    }

}
