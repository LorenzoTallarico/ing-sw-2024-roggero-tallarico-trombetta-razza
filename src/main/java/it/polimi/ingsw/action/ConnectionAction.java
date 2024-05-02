package it.polimi.ingsw.action;

import java.util.ArrayList;
import it.polimi.ingsw.model.Card;

public class ConnectionAction extends Action{

    public ConnectionAction(String author,String recipient) {
        super(ActionType.CONNECTION, author , recipient);
    }

    public ConnectionAction(String recipient) {
        super(ActionType.CONNECTION, null , recipient);
    }

}
