package it.polimi.ingsw.action;

import it.polimi.ingsw.model.Card;


public class PlacingCardAction extends Action {

    private final Card card;
    private final int row;
    private final int column;

    public PlacingCardAction(Card card, int row, int column, String author) {
        super(ActionType.PLACINGCARD, author, null);
        this.card = card;
        this.row = row;
        this.column = column;
    }

    public Card getCard() {
        return card;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

}
