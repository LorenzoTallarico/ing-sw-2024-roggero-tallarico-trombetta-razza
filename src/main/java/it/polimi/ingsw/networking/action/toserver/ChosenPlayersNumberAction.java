package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has chosen the number of players for the game.
 * This action is sent to the server with the chosen number of players.
 */
public class ChosenPlayersNumberAction extends Action {
    /**
     * The number of players chosen for the game.
     */
    private final int playersNumber;
    /**
     * Constructs a new ChosenPlayersNumberAction with the specified number of players.
     *
     * @param playersNumber the number of players chosen for the game
     */
    public ChosenPlayersNumberAction(int playersNumber){
        super(ActionType.CHOSENPLAYERSNUMBER, null, null);
        this.playersNumber = playersNumber;
    }
    /**
     * Gets the number of players chosen for the game.
     *
     * @return the number of players chosen for the game
     */
    public int getPlayersNumber() {
        return playersNumber;
    }
}
