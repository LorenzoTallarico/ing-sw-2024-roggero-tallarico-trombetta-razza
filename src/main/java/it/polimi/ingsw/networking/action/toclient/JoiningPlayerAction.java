package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class JoiningPlayerAction extends Action {

    private final String player;
    private final int currentPlayersNumber;
    private final int gameSize;

    public JoiningPlayerAction(String player, int currentPlayersNumber, int gameSize) {
        super(ActionType.JOININGPLAYER, null, null);
        this.player = player;
        this.currentPlayersNumber = currentPlayersNumber;
        this.gameSize = gameSize;
    }

    public String getPlayer() {
        return player;
    }

    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }

    public int getGameSize() {
        return gameSize;
    }

}
