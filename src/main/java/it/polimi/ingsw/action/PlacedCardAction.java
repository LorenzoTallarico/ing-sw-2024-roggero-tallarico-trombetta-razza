package it.polimi.ingsw.action;

import it.polimi.ingsw.model.Card;

public class PlacedCardAction extends Action {

    private final Card card;
    private final int row;
    private final int col;

    public PlacedCardAction(String recipient, Card card, int row, int column) {
        super(ActionType.PLACEDCARD, null, recipient);
        this.card = card;
        this.row = row;
        this.col = column;
    }

    public Card getCard() {
        return card;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }
}
