package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class ReconnectionSuccessAction extends Action {

    private final ArrayList<Player> players;
    private final  ArrayList<GoldCard> commonGold;
    private final Resource goldDeck;
    private final  ArrayList<ResourceCard> commonResource;
    private final Resource resourceDeck;

    public ReconnectionSuccessAction(String recipient, ArrayList<Player> players, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        super(ActionType.RECONNECTIONSUCCESS, null, recipient);

        this.players = players;
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
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

    public ArrayList<Player> getPlayers() {
        return players;
    }
}