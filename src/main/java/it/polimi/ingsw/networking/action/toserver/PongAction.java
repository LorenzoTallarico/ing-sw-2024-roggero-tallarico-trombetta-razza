package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class PongAction extends Action {

    private String state;

    public PongAction(String nick, String state) {
        super(ActionType.PONG, nick, null);
        this.state = state;
    }

    public String getIsInTurn() {
        return state;
    }

}
