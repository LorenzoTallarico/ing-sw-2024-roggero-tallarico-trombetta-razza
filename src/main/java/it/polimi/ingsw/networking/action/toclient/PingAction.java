package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents a ping action used to check the connectivity between the client and server.
 */
public class PingAction extends Action {
    /**
     * Constructs a new PingAction.
     */
    public PingAction() {
        super(ActionType.PING, null, null);
    }
}
