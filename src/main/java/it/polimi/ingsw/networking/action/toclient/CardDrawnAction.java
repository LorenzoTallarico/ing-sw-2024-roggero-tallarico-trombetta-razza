package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;


public class CardDrawnAction extends Action {

    private final Player player;
    private final Card card;
    private final  ArrayList<GoldCard> commonGold;
    private final Resource goldDeck;
    private final  ArrayList<ResourceCard> commonResource;
    private final Resource resourceDeck;

    public CardDrawnAction(String recipient, Player player, Card card, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        super(ActionType.CARDDRAWN, null, recipient);
        this.player = player;
        this.card = card;
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }

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
