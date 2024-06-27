package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has disconnected.
 * This action contains the nickname of the disconnected player and the number of players currently online.
 */
public class DisconnectedPlayerAction extends Action {
    /**
     * The nickname of the disconnected player.
     */
    private String nickname;
    /**
     * The number of players currently online.
     */
    private int numberOnline;
    /**
     * Constructs a new DisconnectedPlayerAction.
     *
     * @param nick the nickname of the disconnected player
     * @param playersOnline the number of players currently online
     */
    public DisconnectedPlayerAction(String nick, int playersOnline) {
        super(ActionType.DISCONNECTEDPLAYER, null, null);
        this.nickname = nick;
        this.numberOnline = playersOnline;
    }
    /**
     * Gets the nickname of the disconnected player.
     *
     * @return the nickname of the disconnected player
     */
    public String getNickname() {
        return nickname;
    }
    /**
     * Gets the number of players currently online.
     *
     * @return the number of players currently online
     */
    public int getNumberOnline() {
        return numberOnline;
    }
}
