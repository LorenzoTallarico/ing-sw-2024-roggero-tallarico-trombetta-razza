package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating an error has occurred.
 * This action contains an error message and a flag indicating whether the connection should be ended.
 */
public class ErrorAction extends Action {
    /**
     * The error message.
     */
    private final String message;
    /**
     * Indicates whether the connection should be ended.
     */
    private final boolean endConnection;
    /**
     * Constructs a new ErrorAction with the specified recipient and message.
     * The connection will not be ended by default.
     *
     * @param recipient the recipient of the error action
     * @param message the error message
     */
    public ErrorAction(String recipient, String message) {
        super(ActionType.ERROR, null, recipient);
        this.message = message;
        endConnection = false;
    }
    /**
     * Constructs a new ErrorAction with the specified recipient, message, and endConnection flag.
     *
     * @param recipient the recipient of the error action
     * @param message the error message
     * @param endConnection flag indicating whether the connection should be ended
     */
    public ErrorAction(String recipient, String message, boolean endConnection) {
        super(ActionType.ERROR, null, recipient);
        this.message = message;
        this.endConnection = endConnection;
    }
    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getMessage() {
        return message;
    }
    /**
     * Gets the endConnection flag.
     *
     * @return true if the connection should be ended, false otherwise
     */
    public boolean getEndConnection() {
        return endConnection;
    }
}
