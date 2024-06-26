package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class AskingStartAction extends Action {

    private final int playerNumber;

    public AskingStartAction(String recipient, int playerNumber) {
        super(ActionType.ASKINGSTART, null, recipient);
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}