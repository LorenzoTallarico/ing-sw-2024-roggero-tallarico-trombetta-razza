package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player needs to wait.
 * This action contains the nickname of the player who needs to wait.
 */
public class WaitAction extends Action {
    /**
     * The nickname of the player who needs to wait.
     */
    private String nickname;
    /**
     * Constructs a new WaitAction with the specified player's nickname.
     *
     * @param nickname the nickname of the player who needs to wait
     */
    public WaitAction(String nickname){
        super(ActionType.WAIT, null, null);
        this.nickname = nickname;
    }
    /**
     * Gets the nickname of the player who needs to wait.
     *
     * @return the nickname of the player who needs to wait
     */
    public String getNickname(){
        return this.nickname;
    }

}
