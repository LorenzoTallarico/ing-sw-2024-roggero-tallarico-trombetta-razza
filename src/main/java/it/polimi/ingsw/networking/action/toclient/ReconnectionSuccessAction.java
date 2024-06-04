package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ReconnectionSuccessAction extends Action {

    private String nickname;
    private Game game;

    public ReconnectionSuccessAction(String nick, Game g){
        super(ActionType.RECONNECTEDPLAYER, nick, null);
        this.nickname = nick;
        this.game = g;
    }

    public String getNickname() {
        return nickname;
    }

    public Game getGame() {
        return game;
    }

}
