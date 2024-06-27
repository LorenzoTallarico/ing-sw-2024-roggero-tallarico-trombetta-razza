package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

/**
 * notifies the player to place a card
 */
public class AskingPlaceAction extends Action {
    /**
     * player who is asked to place the card
     */
    private final Player player;

    /**
     *
     * @param player player who has to place the card
     */
    public AskingPlaceAction(Player player) {
        super(ActionType.ASKINGPLACE, null, player.getName());
        this.player = player;
    }

    /**
     * gets player who has to place the card
     * @return player
     */
    public Player getPlayer() {
        return player;
    }

}
