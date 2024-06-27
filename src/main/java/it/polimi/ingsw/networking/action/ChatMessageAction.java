package it.polimi.ingsw.networking.action;

import it.polimi.ingsw.model.Message;
/**
 * Represents an action for sending a chat message between players.
 * This action includes the author of the message, recipient (if any), and the message content.
 */
public class ChatMessageAction extends Action {
    /**
     * The message being sent in the chat.
     */
    private final Message message;
    /**
     * Constructs a new ChatMessageAction with the specified author, recipient, and message content.
     *
     * @param author the author of the message (sender)
     * @param recipient the recipient of the message (receiver)
     * @param message the message content being sent
     */
    public ChatMessageAction(String author, String recipient, Message message) {
        super(ActionType.CHATMESSAGE, author, recipient);
        this.message = message;
    }
    /**
     * Gets the message content of this chat action.
     *
     * @return the message being sent
     */
    public Message getMessage() {
        return message;
    }

}
