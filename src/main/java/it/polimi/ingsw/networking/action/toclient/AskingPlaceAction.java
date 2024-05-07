package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.*;

public class AskingPlaceAction extends Action {

    private final Player player;

    public AskingPlaceAction(Player player) {
        super(ActionType.ASKINGPLACE, null, player.getName());
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

}
