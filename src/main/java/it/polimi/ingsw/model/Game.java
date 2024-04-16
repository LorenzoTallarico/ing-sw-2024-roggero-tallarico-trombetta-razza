package it.polimi.ingsw.model;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;

import com.google.gson.*;

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


    /* ########## INIZIO ATTRIBUTI DA RIMUOVERE, UTILI SOLO AL TESTING DEL NETOWRK ############# */
    private Integer state = 0;
    /* ########## FINE ATTRIBUTI DA RIMUOVERE ############# */


    private Game() {
        players = new ArrayList<Player>();
        resourceDeck = new ArrayList<ResourceCard>();
        createGoldDeck();
        createAchievementDeck();
        createResourceDeck();
        createStarterDeck();
        gameState = GameState.LOBBY;
    }

    //Meglio magari con un metodo che va a creare tutto il necessario(?)
//    private Game() {

//    }

    //GETTER
    //Gestione evoluzione delle carte sul tavolo
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

    public ResourceCard popResourceCard() {
        ResourceCard resource = null;
        try {
            resource = resourceDeck.get(0);
            resourceDeck.remove(0);
        } catch (IndexOutOfBoundsException e) {
            System.err.println("Errore: Impossibile rimuovere la carta delle risorse. Deck vuoto.");
        }
        return resource;
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

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gs) {
        gameState = gs;
    }

    public int getCurrPlayer() {
        return currPlayer;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }


    //METHODS

    public void start() {
        gameState=GameState.SELECTACHIEVEMENT;
    }

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
        if(currPlayer >= playersNumber)
            currPlayer = 0;
            if (nextState){
                nextState();
            }
    }

    public void nextState(){
        switch(gameState){
            case INIT:
                gameState=GameState.READY;
                break;
            case READY:
                gameState=GameState.SELECTACHIEVEMENT;
                break;
            case SELECTACHIEVEMENT:
                gameState=GameState.GAME;
                break;
            case GAME:
                gameState=GameState.LASTROUND;
                break;
            case LASTROUND:
                gameState=GameState.FINALSCORE;
                break;
            case FINALSCORE:
                gameState=GameState.END;
                break;
            case END:
                break;
        }
    }

    public void end() {
        gameState=GameState.END;
    }

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
            for(int j = 0; j < tempAchievement.length; j++)
                achievementDeck.add(new AchievementCard(tempAchievement[j].getPoints(), tempAchievement[j].getResource(), tempAchievement[j].getStrategyType(), tempAchievement[j].getItem()));
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
    public Card draw(int index) { //Da sistemare
        if(index>=0 && index<=5) {
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
                    return drawCard;
                }
            }
            else{
                index-=3;
                if (index == 2) {
                    return popGoldCard();
                } else {
                    if (commonGold.size() > index) {
                        drawCard = commonGold.get(index);
                        commonGold.remove(index);
                        if (!goldDeck.isEmpty())
                            commonGold.add(index, popGoldCard());
                    }
                    return drawCard;
                }
            }
        }
        return null;//outOfBound
    }

    /**
     *
     * @param players
     */
    public void addPlayers(ArrayList<Player> players) {
        if(Game.players.isEmpty()){
            assignColors(players);
            Collections.shuffle(players);
            Game.players.addAll(players);
            this.playersNumber = players.size();
        }
        init();
    }

    private void init(){
        gameState = GameState.INIT;
        createHands();
        currPlayer=0;
        gameState= GameState.READY;
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







}
