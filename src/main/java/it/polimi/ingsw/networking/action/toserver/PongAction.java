package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

/**
 * Represents a response action indicating a successful ping (pong).
 * This action is sent to the server with the nickname of the responding client.
 */
public class PongAction extends Action {

    /**
     * Constructs a new PongAction indicating a successful ping response.
     *
     * @param nick the nickname of the client sending the pong response
     */
    public PongAction(String nick) {
        super(ActionType.PONG, nick, null);
    }

}
