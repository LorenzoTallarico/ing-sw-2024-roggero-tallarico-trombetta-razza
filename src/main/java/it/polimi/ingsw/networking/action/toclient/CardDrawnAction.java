package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.model.Card;


public class CardDrawnAction extends Action {

    private final Player player;
    private final Card card;

    public CardDrawnAction(String recipient, Player player, Card card) {
        super(ActionType.CARDDRAWN, null, recipient);
        this.player = player;
        this.card = card;
    }

    public Player getPlayer() {
        return player;
    }

    public Card getCard() {
        return card;
    }
}
