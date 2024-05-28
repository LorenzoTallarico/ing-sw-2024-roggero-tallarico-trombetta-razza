package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class PongAction extends Action {

    public PongAction(String nick) {
        super(ActionType.DISCONNECTEDPLAYER, nick, null);
    }

}
