package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a card has been placed by a player.
 * This action contains the card, the position (row and column) where it was placed, the player who placed it, and the player's score.
 */
public class PlacedCardAction extends Action {
    /**
     * The card that was placed.
     */
    private final Card card;
    /**
     * The row where the card was placed.
     */
    private final int row;
    /**
     * The column where the card was placed.
     */
    private final int col;
    /**
     * The player who placed the card.
     */
    private final Player player;
    /**
     * The score of the player after placing the card.
     */
    private final int score;
    /**
     * Constructs a new PlacedCardAction with the specified recipient, player, card, position, and score.
     *
     * @param recipient the recipient of the action
     * @param p the player who placed the card
     * @param card the card that was placed
     * @param row the row where the card was placed
     * @param column the column where the card was placed
     * @param score the score of the player after placing the card
     */
    public PlacedCardAction(String recipient, Player p, Card card, int row, int column, int score) {
        super(ActionType.PLACEDCARD, null, recipient);
        this.card = card;
        this.player = p;
        this.row = row;
        this.col = column;
        this.score = score;
    }
    /**
     * Gets the card that was placed.
     *
     * @return the card that was placed
     */
    public Card getCard() {
        return card;
    }
    /**
     * Gets the column where the card was placed.
     *
     * @return the column where the card was placed
     */
    public int getColumn() {
        return col;
    }
    /**
     * Gets the row where the card was placed.
     *
     * @return the row where the card was placed
     */
    public int getRow() {
        return row;
    }
    /**
     * Gets the player who placed the card.
     *
     * @return the player who placed the card
     */
    public Player getPlayer() {
        return player;
    }
    /**
     * Gets the score of the player after placing the card.
     *
     * @return the score of the player after placing the card
     */
    public int getScore() {
        return score;
    }
}
