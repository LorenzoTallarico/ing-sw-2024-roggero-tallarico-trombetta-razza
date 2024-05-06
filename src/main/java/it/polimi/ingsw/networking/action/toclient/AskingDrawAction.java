package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.GoldCard;
import it.polimi.ingsw.model.ResourceCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class AskingDrawAction extends Action {

    private final  ArrayList<GoldCard> commonGold;
    private final boolean isCommonGoldEmpty;
    private final  ArrayList<ResourceCard> commonResource;
    private final boolean isCommonResourceEmpty;


    public AskingDrawAction(String recipient, ArrayList<GoldCard> commonGold, boolean isCommonGoldEmpty, ArrayList<ResourceCard> commonResource, boolean isCommonResourceEmpty) {
        super(ActionType.ASKINGDRAW, null, recipient);
        this.commonGold = commonGold;
        this.isCommonGoldEmpty = isCommonGoldEmpty;
        this.commonResource = commonResource;
        this.isCommonResourceEmpty = isCommonResourceEmpty;
    }

    public ArrayList<GoldCard> getCommonGold() {
        return commonGold;
    }

    public boolean isCommonGoldEmpty() {
        return isCommonGoldEmpty;
    }

    public ArrayList<ResourceCard> getCommonResource() {
        return commonResource;
    }

    public boolean isCommonResourceEmpty() {
        return isCommonResourceEmpty;
    }
}
