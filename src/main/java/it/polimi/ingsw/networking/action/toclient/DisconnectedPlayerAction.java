package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class DisconnectedPlayerAction extends Action {

    private String nickname;
    public DisconnectedPlayerAction(String nick) {
        super(ActionType.DISCONNECTEDPLAYER, null, null);
        this.nickname = nick;
    }

    public String getNickname() {
        return nickname;
    }
}
