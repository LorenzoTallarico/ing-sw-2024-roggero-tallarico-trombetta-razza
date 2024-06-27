package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;
/**
 * Represents an action indicating the winners of the game.
 * This action contains a list of players who are the winners.
 */
public class WinnersAction extends Action {
    /**
     * The list of players who are the winners.
     */
    private final ArrayList<Player> players;
    /**
     * Constructs a new WinnersAction with the specified list of players.
     *
     * @param players the list of players who are the winners
     */
    public WinnersAction(ArrayList<Player> players) {
        super(ActionType.WINNERS, null, null);
        this.players = players;
    }
    /**
     * Gets the list of players who are the winners.
     *
     * @return the list of players who are the winners
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }
}
