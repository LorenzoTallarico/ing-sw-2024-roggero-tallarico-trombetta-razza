package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class DisconnectedPlayerAction extends Action {

    private String nickname;
    private int numberOnline;
    public DisconnectedPlayerAction(String nick, int playersOnline) {
        super(ActionType.DISCONNECTEDPLAYER, null, null);
        this.nickname = nick;
        this.numberOnline = playersOnline;
    }

    public String getNickname() {
        return nickname;
    }

    public int getNumberOnline() {
        return numberOnline;
    }
}
