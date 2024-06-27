package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;
/**
 * Represents an action indicating a successful reconnection.
 * This action contains the current game state, including players, common cards, and resource decks.
 */
public class ReconnectionSuccessAction extends Action {
    /**
     * The list of players in the game.
     */
    private final ArrayList<Player> players;
    /**
     * gold cards on the table
     */
    private final  ArrayList<GoldCard> commonGold;
    /**
     * resource of the card on top of the gold deck
     */
    private final Resource goldDeck;
    /**
     * resource cards on the table
     */
    private final  ArrayList<ResourceCard> commonResource;
    /**
     * resource of the card on top of resource deck
     */
    private final Resource resourceDeck;
    /**
     * achievement cards on the table
     */
    private final ArrayList<AchievementCard> commonAchievement;
    /**
     * Constructs a new ReconnectionSuccessAction with the specified recipient and game state information.
     *
     * @param recipient the recipient of the action
     * @param players the list of players in the game
     * @param commonGold gold cards on the table
     * @param goldDeck resource of the card on top of the gold deck
     * @param commonResource resource cards on the table
     * @param resourceDeck resource of the card on top of resource deck
     * @param commonAchievement achievement cards on the table
     */
    public ReconnectionSuccessAction(String recipient, ArrayList<Player> players, ArrayList<GoldCard> commonGold, Resource goldDeck,
                                     ArrayList<ResourceCard> commonResource, Resource resourceDeck, ArrayList<AchievementCard> commonAchievement) {
        super(ActionType.RECONNECTIONSUCCESS, null, recipient);

        this.players = players;
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
        this.commonAchievement = commonAchievement;
    }

    /**
     * gets gold cards on the table
     * @return ArrayList of goldCards
     */
    public ArrayList<GoldCard> getCommonGold() {
        return commonGold;
    }

    /**
     * gets resource of the card on top of the gold deck
     * @return resource
     */
    public Resource getGoldDeck() {
        return goldDeck;
    }

    /**
     * gets resource cards on the table
     * @return ArrayList of resource cards
     */
    public ArrayList<ResourceCard> getCommonResource() {
        return commonResource;
    }

    /**
     * gets resource of the card on top of the resource deck
     * @return resource
     */
    public Resource getResourceDeck() {
        return resourceDeck;
    }

    /**
     * gets achievement cards on the table
     * @return ArrayList of achievementCards
     */
    public ArrayList<AchievementCard> getCommonAchievement() {
        return commonAchievement;
    }
    /**
     * Gets the list of players in the game.
     *
     * @return the list of players in the game
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }
}
