package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class PlacedCardAction extends Action {

    private final Card card;
    private final int row;
    private final int col;
    private final Player player;
    private final int score;

    public PlacedCardAction(String recipient, Player p, Card card, int row, int column, int score) {
        super(ActionType.PLACEDCARD, null, recipient);
        this.card = card;
        this.player = p;
        this.row = row;
        this.col = column;
        this.score = score;
    }

    public Card getCard() {
        return card;
    }

    public int getColumn() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public Player getPlayer() {
        return player;
    }

    public int getScore() {
        return score;
    }
}
