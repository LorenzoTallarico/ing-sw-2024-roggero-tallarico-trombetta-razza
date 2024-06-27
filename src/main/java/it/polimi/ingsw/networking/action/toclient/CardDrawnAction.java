package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

/**
 * notifies the player of having successfully drawn a card
 */
public class CardDrawnAction extends Action {
    /**
     * player who draws the card
     */
    private final Player player;
    /**
     * card drawn
     */
    private final Card card;
    /**
     * gold cards on the table
     */
    private final  ArrayList<GoldCard> commonGold;
    /**
     * resource of the card on top of gold deck
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
     * constructor of CardDrwanAction
     * @param recipient recipient of the action
     * @param player player who draws the card
     * @param card card drawn
     * @param commonGold gold cards on the table
     * @param goldDeck resource of the card on top of gold deck
     * @param commonResource resource cards on the table
     * @param resourceDeck resource of the card on top of resource deck
     */
    public CardDrawnAction(String recipient, Player player, Card card, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        super(ActionType.CARDDRAWN, null, recipient);
        this.player = player;
        this.card = card;
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
    }

    /**
     * gets the player who draws the card
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * gets the card drawn
     * @return card
     */
    public Card getCard() {
        return card;
    }

    /**
     * gets gold cards on the table
     * @return ArrayList of gold cards
     */
    public ArrayList<GoldCard> getCommonGold() {
        return commonGold;
    }

    /**
     * gets resource of card on top of the gold deck
     * @return resource
     */
    public Resource getGoldDeck() {
        return goldDeck;
    }

    /**
     * gets resource cards on the table
     * @return arrayList of resource cards
     */
    public ArrayList<ResourceCard> getCommonResource() {
        return commonResource;
    }

    /**
     * gets resource of card on top of resource deck
     * @return resource
     */
    public Resource getResourceDeck() {
        return resourceDeck;
    }
}
