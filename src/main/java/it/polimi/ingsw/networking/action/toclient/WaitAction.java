package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class WaitAction extends Action {

    private String nickname;

    public WaitAction(String nickname){
        super(ActionType.WAIT, null, null);
        this.nickname = nickname;
    }

    public String getNickname(){
        return this.nickname;
    }

}
