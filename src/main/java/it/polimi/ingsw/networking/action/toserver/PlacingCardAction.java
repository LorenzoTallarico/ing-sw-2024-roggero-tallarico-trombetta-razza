package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;


public class PlacingCardAction extends Action {

    private final int cardIndex;
    private final int row;
    private final int column;
    private final boolean side;

    public PlacingCardAction(int cardIndex, boolean side, int row, int column, String author) {
        super(ActionType.PLACINGCARD, author, null);
        this.cardIndex = cardIndex;
        this.row = row;
        this.column = column;
        this.side = side;
    }

    public int getCardIndex() {
        return cardIndex;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    public boolean getSide() {
        return side;
    }
}
