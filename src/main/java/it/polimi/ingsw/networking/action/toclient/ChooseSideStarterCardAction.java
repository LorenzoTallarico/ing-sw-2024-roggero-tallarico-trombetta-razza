package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.StarterCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChooseSideStarterCardAction extends Action {

    private final StarterCard card;

    public ChooseSideStarterCardAction(String recipient, StarterCard card) {
        super(ActionType.CHOOSESIDESTARTERCARD, null, recipient);
        this.card = card;
    }

    public StarterCard getCard() {
        return card;
    }

}
