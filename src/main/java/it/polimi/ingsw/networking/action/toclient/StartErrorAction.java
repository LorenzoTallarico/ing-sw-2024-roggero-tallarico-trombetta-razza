package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating an error when attempting to start the game.
 * This action contains an error message specific to game start issues.
 */
public class StartErrorAction extends Action {
    /**
     * The error message indicating why the game cannot be started.
     */
    private final String error;

    /**
     * Constructs a new StartErrorAction with the specified recipient's nickname.
     * The error message is pre-defined and indicates the reason why the game cannot be started.
     *
     * @param nickname the nickname of the recipient
     */
    public StartErrorAction(String nickname){
        super(ActionType.STARTERROR, null, nickname);
        this.error = "> You can't start the game now, the number of online players is not between 2 and 4.\n";
    }
    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getError() {
        return this.error;
    }
}
