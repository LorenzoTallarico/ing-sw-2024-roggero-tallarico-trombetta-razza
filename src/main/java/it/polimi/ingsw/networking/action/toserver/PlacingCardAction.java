package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

/**
 * Represents an action indicating that a player is placing a card on the game board.
 * This action is sent to the server with the details of the card placement.
 */
public class PlacingCardAction extends Action {
    /**
     * The index of the card being placed.
     */
    private final int cardIndex;
    /**
     * The row on the game board where the card is being placed.
     */
    private final int row;
    /**
     * The column on the game board where the card is being placed.
     */
    private final int column;
    /**
     * The side of the card being placed:
     * - true for front side
     * - false for back side
     */
    private final boolean side;
    /**
     * Constructs a new PlacingCardAction with the specified details of the card placement.
     *
     * @param cardIndex the index of the card being placed
     * @param side the side of the card being placed (true for front side, false for back side)
     * @param row the row on the game board where the card is being placed
     * @param column the column on the game board where the card is being placed
     * @param author the author of the action (the player who is placing the card)
     */
    public PlacingCardAction(int cardIndex, boolean side, int row, int column, String author) {
        super(ActionType.PLACINGCARD, author, null);
        this.cardIndex = cardIndex;
        this.row = row;
        this.column = column;
        this.side = side;
    }
    /**
     * Gets the index of the card being placed.
     *
     * @return the index of the card being placed
     */
    public int getCardIndex() {
        return cardIndex;
    }
    /**
     * Gets the row on the game board where the card is being placed.
     *
     * @return the row on the game board where the card is being placed
     */
    public int getRow() {
        return row;
    }
    /**
     * Gets the column on the game board where the card is being placed.
     *
     * @return the column on the game board where the card is being placed
     */
    public int getColumn() {
        return column;
    }
    /**
     * Gets the side of the card being placed.
     *
     * @return true if the front side is being placed, false if the back side is being placed
     */
    public boolean getSide() {
        return side;
    }
}
