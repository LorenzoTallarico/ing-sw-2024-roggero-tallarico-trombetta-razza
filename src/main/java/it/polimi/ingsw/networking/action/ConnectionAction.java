package it.polimi.ingsw.networking.action;

public class ConnectionAction extends Action{

    public ConnectionAction(String author,String recipient) {
        super(ActionType.CONNECTION, author , recipient);
    }

    public ConnectionAction(String recipient) {
        super(ActionType.CONNECTION, null , recipient);
    }

}
