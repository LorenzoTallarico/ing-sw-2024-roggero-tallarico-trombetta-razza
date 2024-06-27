package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

/**
 * action to start the game
 */
public class AskingStartAction extends Action {
    /**
     * number of players needed to start the game
     */
    private final int playerNumber;

    /**
     * constructor of AskingStartAction class
     * @param recipient recipient of the action
     * @param playerNumber number of players
     */
    public AskingStartAction(String recipient, int playerNumber) {
        super(ActionType.ASKINGSTART, null, recipient);
        this.playerNumber = playerNumber;
    }

    /**
     * gets the number of players
     * @return number of players
     */
    public int getPlayerNumber() {
        return playerNumber;
    }
}