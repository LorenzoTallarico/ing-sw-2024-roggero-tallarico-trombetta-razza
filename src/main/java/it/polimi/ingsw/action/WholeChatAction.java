package it.polimi.ingsw.action;

import it.polimi.ingsw.model.Message;

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
