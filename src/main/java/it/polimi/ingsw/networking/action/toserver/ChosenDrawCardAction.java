package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChosenDrawCardAction extends Action {

    private final Card card; //da gestire poi il cast a seconda della carta

    public ChosenDrawCardAction(String author, Card card) {
        super(ActionType.CHOSENDRAWCARD, author, null);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
