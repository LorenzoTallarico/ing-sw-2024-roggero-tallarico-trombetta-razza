package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.model.AchievementCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class SetNicknameAction extends Action {

    private final String nickname;
    private final boolean gui;

    public SetNicknameAction( String nickname, boolean gui) {
        super(ActionType.SETNICKNAME, null, null);
        this.nickname = nickname;
        this.gui = gui;
    }

    public String getNickname() {return nickname;}

    public boolean getGui() {
        return gui;
    }
}