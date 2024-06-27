package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.GoldCard;
import it.polimi.ingsw.model.Resource;
import it.polimi.ingsw.model.ResourceCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

/**
 * Notifies the player to draw a card
 */
public class AskingDrawAction extends Action {

    /**
     * Gold cards on the table
     */
    private final  ArrayList<GoldCard> commonGold;
    /**
     * Resource of the card on top of the gold deck
     */
    private final Resource goldDeck;
    /**
     * Resource cards on the table
     */
    private final  ArrayList<ResourceCard> commonResource;
    /**
     * Resource of the card on top of the resource deck
     */
    private final Resource resourceDeck;

    /**
     * Constructor of the class, initializes a new AskingDrawAction
     * @param recipient player who have to draw the card
     * @param commonGold
     * @param goldDeck
     * @param commonResource Resource cards on the table
     * @param resourceDeck
     */
    public AskingDrawAction(String recipient, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        super(ActionType.ASKINGDRAW, null, recipient);
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
    }

    /**
     * Gets the gold cards on the table
     * @return arraylist of goldCards
     */
    public ArrayList<GoldCard> getCommonGold() {
        return commonGold;
    }

    public Resource getGoldDeck() {
        return goldDeck;
    }

    public ArrayList<ResourceCard> getCommonResource() {
        return commonResource;
    }

    public Resource getResourceDeck() {
        return resourceDeck;
    }
}
