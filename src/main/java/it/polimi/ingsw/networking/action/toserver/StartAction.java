package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.*;

public class StartAction extends Action {


    public StartAction(String author) {
        super(ActionType.START, author, null);
    }

}