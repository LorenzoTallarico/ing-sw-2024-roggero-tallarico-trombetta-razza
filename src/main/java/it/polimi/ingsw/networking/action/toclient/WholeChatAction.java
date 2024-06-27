package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;
/**
 * Represents an action that sends the entire chat history to a client.
 * This action contains a list of messages comprising the entire chat history.
 */
public class WholeChatAction extends Action {
    /**
     * The list of messages representing the entire chat history.
     */
    private final ArrayList<Message> messages;
    /**
     * Constructs a new WholeChatAction with the specified recipient and list of messages.
     *
     * @param recipient the recipient of the action
     * @param messages the list of messages representing the entire chat history
     */
    public WholeChatAction(String recipient, ArrayList<Message> messages) {
        super(ActionType.WHOLECHAT, null, recipient);
        this.messages = messages;
    }
    /**
     * Gets the list of messages representing the entire chat history.
     *
     * @return the list of messages representing the entire chat history
     */
    public ArrayList<Message> getMessages() {
        return this.messages;
    }
}
