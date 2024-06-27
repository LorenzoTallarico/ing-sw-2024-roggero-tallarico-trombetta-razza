package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

/**
 * notifies the player of the number of current players
 */
public class AskingPlayersNumberAction extends Action {
    /**
     * constructor of the class, initializes a new AskingPlayersNumberAction
     * @param recipient recipient of the action
     */
    public AskingPlayersNumberAction(String recipient) {
        super(ActionType.ASKINGPLAYERSNUMBER, null, recipient);
    }

}
