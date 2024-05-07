package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.model.Card;


public class CardDrawnAction extends Action {

    private Card card;

    public CardDrawnAction(String recipient, Card card) {
        super(ActionType.CARDDRAWN, null, recipient);
        this.card = card;
    }

    public Card getCard(){
        return this.card;
    }

}
