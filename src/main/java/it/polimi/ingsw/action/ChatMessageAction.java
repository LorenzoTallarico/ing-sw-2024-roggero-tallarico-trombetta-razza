package it.polimi.ingsw.action;

import it.polimi.ingsw.model.Message;

public class ChatMessageAction extends Action {

    private final Message message;

    public ChatMessageAction(String author, String recipient, Message message) {
        super(ActionType.CHATMESSAGE, author, recipient);
        this.message = message;
    }

    public Message getMessage() {
        return message;
    }

}
