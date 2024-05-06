package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class PlacedErrorAction extends Action {

    private final String error;


    public PlacedErrorAction(String nickname){
        super(ActionType.PLACEDCARDERROR, null, nickname);
        this.error = "> Card can't be placed in the given position.";
    }

    public String getError() {
        return this.error;
    }

}
