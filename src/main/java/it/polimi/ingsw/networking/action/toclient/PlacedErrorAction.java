package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating an error occurred while placing a card.
 * This action contains an error message specific to card placement issues.
 */
public class PlacedErrorAction extends Action {
    /**
     * The error message indicating why the card could not be placed.
     */
    private final String error;

    /**
     * Constructs a new PlacedErrorAction with the specified recipient's nickname.
     * The error message is pre-defined and indicates the card placement error.
     *
     * @param nickname the nickname of the recipient
     */
    public PlacedErrorAction(String nickname){
        super(ActionType.PLACEDCARDERROR, null, nickname);
        this.error = "> This card can't be placed there, try a different position and/or side.\n" +
                     "> Enter \"place\" to place a card.";
    }

    /**
     * Gets the error message.
     *
     * @return the error message
     */
    public String getError() {
        return this.error;
    }

}
