package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.GoldCard;
import it.polimi.ingsw.model.ResourceCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class AskingDrawAction extends Action {

    private final  ArrayList<GoldCard> commonGold;
    private final boolean goldDeck;
    private final  ArrayList<ResourceCard> commonResource;
    private final boolean resourceDeck;


    public AskingDrawAction(String recipient, ArrayList<GoldCard> commonGold, boolean goldDeck, ArrayList<ResourceCard> commonResource, boolean resourceDeck) {
        super(ActionType.ASKINGDRAW, null, recipient);
        this.commonGold = commonGold;
        this.goldDeck = goldDeck;
        this.commonResource = commonResource;
        this.resourceDeck = resourceDeck;
    }

    public ArrayList<GoldCard> getCommonGold() {
        return commonGold;
    }

    public boolean getGoldDeck() {
        return goldDeck;
    }

    public ArrayList<ResourceCard> getCommonResource() {
        return commonResource;
    }

    public boolean getResourceDeck() {
        return resourceDeck;
    }
}
