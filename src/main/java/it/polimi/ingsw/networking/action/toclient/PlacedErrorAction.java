package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class PlacedErrorAction extends Action {

    private final String error;


    public PlacedErrorAction(String nickname){
        super(ActionType.PLACEDCARDERROR, null, nickname);
        this.error = "> This card can't be placed there, try a different position and/or side.\n" +
                     "> Enter \"place\" to place a card.";
    }

    public String getError() {
        return this.error;
    }

}
