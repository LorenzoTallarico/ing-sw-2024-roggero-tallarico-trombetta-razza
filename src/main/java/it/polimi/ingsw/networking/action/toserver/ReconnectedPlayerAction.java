package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.rmi.VirtualView;

public class ReconnectedPlayerAction extends Action {

    private String nick;
    private VirtualView oldVirtualView;
    private VirtualView newVirtualview;

    public ReconnectedPlayerAction(String nickname, VirtualView old, VirtualView newClient){
        super(ActionType.RECONNECTEDPLAYER, nickname, null);
        this.nick = nickname;
        this.oldVirtualView = old;
        this.newVirtualview = newClient;
    }


    public String getNick() {
        return nick;
    }

    public VirtualView getOldVirtualView() {
        return oldVirtualView;
    }

    public VirtualView getNewVirtualview() {
        return newVirtualview;
    }
}
