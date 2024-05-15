package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class ChooseSideStarterCardAction extends Action {

    private final StarterCard card;
    private final Player player;
    private final  ArrayList<GoldCard> commonGold;
    private final Resource goldDeck;
    private final  ArrayList<ResourceCard> commonResource;
    private final Resource resourceDeck;

    public ChooseSideStarterCardAction(String recipient, StarterCard card, Player player, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) {
        super(ActionType.CHOOSESIDESTARTERCARD, null, recipient);
        this.card = card;
        this.player = player;
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
    }

    public StarterCard getCard() {
        return card;
    }

    public Player getPlayer() {
        return player;
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
