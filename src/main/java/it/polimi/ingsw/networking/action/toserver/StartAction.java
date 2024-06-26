package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class StartAction extends Action {


    public StartAction(String author) {
        super(ActionType.START, author, null);
    }

}