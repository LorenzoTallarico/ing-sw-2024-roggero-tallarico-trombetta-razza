package it.polimi.ingsw.networking.action;

import java.io.Serializable;

// Se finite tutte, da mettere abstract e togliere obj
/**
 * Represents a generic action that can be sent over the network.
 * Actions include various types defined by ActionType.
 * This class implements Serializable to enable serialization of action objects.
 */
public class Action implements Serializable {
    /**
     * The type of action, defined by ActionType.
     */
    private final ActionType type;
    /**
     * The author of the action, typically the player who initiates the action.
     */
    private final String author;
    /**
     * The recipient of the action, if applicable.
     */
    private final String recipient;
    /**
     * Constructs a new Action with the specified type, author, and recipient.
     *
     * @param type the type of the action, defined by ActionType
     * @param author the author of the action (the player who initiated the action)
     * @param recipient the recipient of the action, if applicable
     */
    public Action(ActionType type, String author, String recipient) {
        this.type = type;
        if(author != null)
            this.author = author;
        else
            this.author = "";
        if(recipient != null)
            this.recipient = recipient;
        else
            this.recipient = "";
    }
    /**
     * Gets the type of the action.
     *
     * @return the type of the action, defined by ActionType
     */
    public ActionType getType() {
        return type;
    }
    /**
     * Gets the recipient of the action.
     *
     * @return the recipient of the action, if applicable
     */
    public String getAuthor() {
        return author;
    }
    /**
     * Gets the recipient of the action.
     *
     * @return the recipient of the action, if applicable
     */
    public String getRecipient() {
        return recipient;
    }
}
