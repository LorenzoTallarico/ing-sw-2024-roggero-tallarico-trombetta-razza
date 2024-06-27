package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

/**
 * notifies the player of the need of choosing the side of his starter card
 */
public class ChooseSideStarterCardAction extends Action {
    /**
     * starter card of the player
     */
    private final StarterCard card;
    /**
     * player who needs to choose the side of the starter card
     */
    private final Player player;
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
     * resource of the card on top of the resource deck
     */
    private final Resource resourceDeck;

    /**
     * constructor of ChooseSideStartCardAction class
     * @param recipient recipient of the action
     * @param card starterCard whose side needs to be chosen
     * @param player player who needs to choose the side
     * @param commonGold gold cards on the table
     * @param goldDeck resource of the card on top of the gold deck
     * @param commonResource resource cards on the table
     * @param resourceDeck resource of the card on top of the resource deck
     */
    public ChooseSideStarterCardAction(String recipient, StarterCard card, Player player, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        super(ActionType.CHOOSESIDESTARTERCARD, null, recipient);
        this.card = card;
        this.player = player;
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
    }

    /**
     * gets the starterCard
     * @return starterCard
     */
    public StarterCard getCard() {
        return card;
    }

    /**
     * gets the player
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * gets gold cards on the table
     * @return ArrayList of goldCards
     */
    public ArrayList<GoldCard> getCommonGold() {
        return commonGold;
    }

    /**
     * gets resource of card on top of gold deck
     * @return resource
     */
    public Resource getGoldDeck() {
        return goldDeck;
    }

    /**
     * gets resource cards on the table
     * @return ArrayList of ResourceCards
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
