package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.StarterCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChooseSideStarterCardAction extends Action {

    private final StarterCard card;
    private final Player player;

    public ChooseSideStarterCardAction(String recipient, StarterCard card, Player player) {
        super(ActionType.CHOOSESIDESTARTERCARD, null, recipient);
        this.card = card;
        this.player = player;
    }

    public StarterCard getCard() {
        return card;
    }

    public Player getPlayer() {
        return player;
    }
}
