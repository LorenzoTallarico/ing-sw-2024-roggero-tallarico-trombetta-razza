package it.polimi.ingsw.networking.action;
/**
 * Represents an action indicating a connection event between clients.
 * This action is used to notify the server and possibly other clients about a connection.
 */
public class ConnectionAction extends Action{
    /**
     * Constructs a new ConnectionAction with the specified author and recipient.
     *
     * @param author the author of the connection action (sender), can be null
     * @param recipient the recipient of the connection action (receiver), can be null
     */
    public ConnectionAction(String author,String recipient) {
        super(ActionType.CONNECTION, author , recipient);
    }
    /**
     * Constructs a new ConnectionAction with the specified recipient.
     * This constructor is typically used when the author of the connection action is not specified.
     *
     * @param recipient the recipient of the connection action (receiver), can be null
     */
    public ConnectionAction(String recipient) {
        super(ActionType.CONNECTION, null , recipient);
    }

}
