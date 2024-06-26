package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class SetNicknameAction extends Action {

    private final String nickname;

    public SetNicknameAction( String nickname) {
        super(ActionType.SETNICKNAME, null, null);
        this.nickname = nickname;
    }

    public String getNickname() {return nickname;}

}