package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class AskingPlayersNumberAction extends Action {

    public AskingPlayersNumberAction(String recipient) {
        super(ActionType.ASKINGPLAYERSNUMBER, null, recipient);
    }

}
