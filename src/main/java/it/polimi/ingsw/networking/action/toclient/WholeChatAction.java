package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class WholeChatAction extends Action {

    private final ArrayList<Message> messages;

    public WholeChatAction(String recipient, ArrayList<Message> messages) {
        super(ActionType.WHOLECHAT, null, recipient);
        this.messages = messages;
    }

    public ArrayList<Message> getMessages() {
        return this.messages;
    }
}
