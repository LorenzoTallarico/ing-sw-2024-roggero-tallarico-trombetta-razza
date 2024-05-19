package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.*;

public class StartAction extends Action {

    private final int playerNumber;

    public StartAction(String author, int playerNumber) {
        super(ActionType.START, author, null);
        this.playerNumber = playerNumber;
    }

    public int getPlayerNumber() {
        return playerNumber;
    }
}