package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has chosen to draw a card.
 * This action is sent to the server with the author of the action and the index specifying which card to draw.
 */
public class ChosenDrawCardAction extends Action {
    /**
     * specifies the index of the card to draw
     */
    private final int index;
    /*
    1: gold card -> commonGold.get(0)
    2: gold card -> commonGold.get(1)
    3: resource card -> commonResource.get(0)
    4: resource card -> commonResource.get(1)
    5: gold deck -> goldDeck.pop()
    6: resource deck -> resourceDeck.pop();
     */

    /**
     * Constructs a new ChosenDrawCardAction with the specified author and index.
     *
     * @param author the author of the action (the player who chose to draw a card)
     * @param index the index specifying which card to draw
     */
    public ChosenDrawCardAction(String author, int index) {
        super(ActionType.CHOSENDRAWCARD, author, null);
        this.index = index;
    }
    /**
     * Gets the index specifying which card to draw.
     *
     * @return the index specifying which card to draw
     */
    public int getIndex() {
        return index;
    }

}
