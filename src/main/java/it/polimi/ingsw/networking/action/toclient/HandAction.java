package it.polimi.ingsw.networking.action.toclient;

import java.util.ArrayList;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class HandAction extends Action {

    private final ArrayList<Card> hand;

    public HandAction(String recipient, ArrayList<Card> hand) {
        super(ActionType.HAND, null, recipient);
        this.hand = hand;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }
}


