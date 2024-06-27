package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has initiated the start of the game.
 * This action is sent to the server with the author of the action.
 */
public class StartAction extends Action {

    /**
     * Constructs a new StartAction indicating the start of the game.
     *
     * @param author the author of the action (the player who initiated the game start)
     */
    public StartAction(String author) {
        super(ActionType.START, author, null);
    }

}