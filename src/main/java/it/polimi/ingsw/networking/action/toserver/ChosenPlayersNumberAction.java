package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChosenPlayersNumberAction extends Action {

    private final int playersNumber;
    public ChosenPlayersNumberAction(int playersNumber){
        super(ActionType.CHOSENPLAYERSNUMBER, null, null);
        this.playersNumber = playersNumber;
    }

    public int getPlayersNumber() {
        return playersNumber;
    }
}
