package it.polimi.ingsw.action;

import java.util.ArrayList;
import it.polimi.ingsw.model.Card;

public class HandAction extends Action {

    private final ArrayList<Card> hand;

    public HandAction(String author, String recipient, ArrayList<Card> hand) {
        super(ActionType.HAND, null, recipient);
        this.hand = hand;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}


