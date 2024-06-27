package it.polimi.ingsw.networking.action.toclient;

import java.util.ArrayList;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action that sends a hand of cards to a recipient.
 */
public class HandAction extends Action {
    /**
     * The hand of cards.
     */
    private final ArrayList<Card> hand;
    /**
     * Constructs a new HandAction with the specified recipient and hand of cards.
     *
     * @param recipient the recipient of the hand action
     * @param hand the hand of cards to be sent
     */
    public HandAction(String recipient, ArrayList<Card> hand) {
        super(ActionType.HAND, null, recipient);
        this.hand = hand;
    }
    /**
     * Gets the hand of cards.
     *
     * @return the hand of cards
     */
    public ArrayList<Card> getHand() {
        return hand;
    }
}


