package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player is setting their nickname.
 * This action is sent to the server with the new nickname chosen by the player.
 */
public class SetNicknameAction extends Action {
    /**
     * The new nickname chosen by the player.
     */
    private final String nickname;
    /**
     * Constructs a new SetNicknameAction with the specified nickname.
     *
     * @param nickname the new nickname chosen by the player
     */
    public SetNicknameAction( String nickname) {
        super(ActionType.SETNICKNAME, null, null);
        this.nickname = nickname;
    }
    /**
     * Gets the new nickname chosen by the player.
     *
     * @return the new nickname chosen by the player
     */
    public String getNickname() {return nickname;}

}