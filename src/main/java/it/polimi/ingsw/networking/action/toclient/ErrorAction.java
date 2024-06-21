package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ErrorAction extends Action {
    private final String message;
    private final boolean endConnection;

    public ErrorAction(String recipient, String message) {
        super(ActionType.ERROR, null, recipient);
        this.message = message;
        endConnection = false;
    }
    public ErrorAction(String recipient, String message, boolean endConnection) {
        super(ActionType.ERROR, null, recipient);
        this.message = message;
        this.endConnection = endConnection;
    }

    public String getMessage() {
        return message;
    }
    public boolean getEndConnection() {
        return endConnection;
    }
}
