package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating a request to initiate a chat.
 * This action is sent to the server with the author of the request.
 */
public class AskingChatAction extends Action {
    /**
     * Constructs a new AskingChatAction with the specified author of the chat request.
     *
     * @param author the author of the chat request
     */
    public AskingChatAction(String author) {
        super(ActionType.ASKINGCHAT, author, null);
    }
}
