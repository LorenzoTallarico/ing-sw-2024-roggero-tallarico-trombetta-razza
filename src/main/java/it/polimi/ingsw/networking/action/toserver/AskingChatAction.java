package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class AskingChatAction extends Action {

    public AskingChatAction(String author) {
        super(ActionType.ASKINGCHAT, author, null);
    }
}
