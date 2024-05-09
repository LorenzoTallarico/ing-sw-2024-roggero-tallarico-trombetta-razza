package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.model.Card;


public class CardDrawnAction extends Action {

    private final Card card;
    private final Player player;

    public CardDrawnAction(String recipient, Card card, Player player) {
        super(ActionType.CARDDRAWN, null, recipient);
        this.card = card;
        this.player = player;
    }

    public Card getCard() {
        return this.card;
    }

    public Player getPlayer() {
        return player;
    }

}
