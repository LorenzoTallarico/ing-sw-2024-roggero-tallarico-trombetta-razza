package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has chosen a side for a starter card.
 * This action is sent to the server with the author of the action and the chosen side.
 */
public class ChosenSideStarterCardAction extends Action {
    /**
     * The chosen side for the starter card:
     * - true for front side
     * - false for back side
     */
    private final boolean side;
    /**
     * Constructs a new ChosenSideStarterCardAction with the specified author and chosen side.
     *
     * @param author the author of the action (the player who chose the side)
     * @param side the chosen side for the starter card (true for side A, false for side B)
     */
    public ChosenSideStarterCardAction(String author, boolean side) {
        super(ActionType.CHOSENSIDESTARTERCARD, author, null);
        this.side = side;
    }
    /**
     * Gets the chosen side for the starter card.
     *
     * @return true if the front side is chosen, false if the back side is chosen
     */
    public boolean getSide() {
        return side;
    }

}
