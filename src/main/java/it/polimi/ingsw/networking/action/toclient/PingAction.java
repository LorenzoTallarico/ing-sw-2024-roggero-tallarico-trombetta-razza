package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class PingAction extends Action {

    public PingAction() {
        super(ActionType.PING, null, null);
    }
}
