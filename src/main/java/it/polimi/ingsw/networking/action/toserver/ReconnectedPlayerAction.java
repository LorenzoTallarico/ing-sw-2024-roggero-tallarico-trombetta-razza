package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.VirtualView;
/**
 * Represents an action indicating that a player has reconnected to the game server.
 * This action is sent to the server with the nickname of the reconnected player and both the old and new VirtualViews.
 */
public class ReconnectedPlayerAction extends Action {
    /**
     * The nickname of the reconnected player.
     */
    private String nick;
    /**
     * The old VirtualView associated with the reconnected player.
     */
    private VirtualView oldVirtualView;
    /**
     * The new VirtualView associated with the reconnected player.
     */
    private VirtualView newVirtualview;
    /**
     * Constructs a new ReconnectedPlayerAction with the specified nickname and VirtualViews.
     *
     * @param nickname the nickname of the reconnected player
     * @param old the old VirtualView of the reconnected player
     * @param newClient the new VirtualView of the reconnected player
     */
    public ReconnectedPlayerAction(String nickname, VirtualView old, VirtualView newClient){
        super(ActionType.RECONNECTEDPLAYER, nickname, null);
        this.nick = nickname;
        this.oldVirtualView = old;
        this.newVirtualview = newClient;
    }

    /**
     * Gets the nickname of the reconnected player.
     *
     * @return the nickname of the reconnected player
     */
    public String getNick() {
        return nick;
    }
    /**
     * Gets the old VirtualView associated with the reconnected player.
     *
     * @return the old VirtualView of the reconnected player
     */

    public VirtualView getOldVirtualView() {
        return oldVirtualView;
    }
    /**
     * Gets the new VirtualView associated with the reconnected player.
     *
     * @return the new VirtualView of the reconnected player
     */
    public VirtualView getNewVirtualview() {
        return newVirtualview;
    }
}
