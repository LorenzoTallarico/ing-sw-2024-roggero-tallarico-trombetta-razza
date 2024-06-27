package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has joined the game.
 * This action contains the player's name, the current number of players, and the total game size.
 */
public class JoiningPlayerAction extends Action {
    /**
     * The name of the player who joined.
     */
    private final String player;
    /**
     * The current number of players in the game.
     */
    private final int currentPlayersNumber;
    /**
     * The total size of the game.
     */
    private final int gameSize;
    /**
     * Constructs a new JoiningPlayerAction with the specified player, current number of players, and game size.
     *
     * @param player the name of the player who joined
     * @param currentPlayersNumber the current number of players in the game
     * @param gameSize the total size of the game
     */
    public JoiningPlayerAction(String player, int currentPlayersNumber, int gameSize) {
        super(ActionType.JOININGPLAYER, null, null);
        this.player = player;
        this.currentPlayersNumber = currentPlayersNumber;
        this.gameSize = gameSize;
    }
    /**
     * Gets the name of the player who joined.
     *
     * @return the name of the player who joined
     */
    public String getPlayer() {
        return player;
    }
    /**
     * Gets the current number of players in the game.
     *
     * @return the current number of players in the game
     */
    public int getCurrentPlayersNumber() {
        return currentPlayersNumber;
    }
    /**
     * Gets the total size of the game.
     *
     * @return the total size of the game
     */
    public int getGameSize() {
        return gameSize;
    }

}
