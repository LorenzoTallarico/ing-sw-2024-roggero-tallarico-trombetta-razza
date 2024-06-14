package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class StartErrorAction extends Action {

    private final String error;


    public StartErrorAction(String nickname){
        super(ActionType.STARTERROR, null, nickname);
        this.error = "> You can't start the game now, the number of online players is not between 2 and 4.\n";
    }

    public String getError() {
        return this.error;
    }
}
