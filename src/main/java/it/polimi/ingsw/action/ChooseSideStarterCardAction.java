package it.polimi.ingsw.action;

import it.polimi.ingsw.model.StarterCard;

public class ChooseSideStarterCardAction extends Action {

    private final StarterCard card;

    public ChooseSideStarterCardAction(String recipient, StarterCard card) {
        super(ActionType.CHOOSESIDESTARTERCARD, null, recipient);
        this.card = card;
    }

    public StarterCard getCard() {
        return this.card;
    }

}
