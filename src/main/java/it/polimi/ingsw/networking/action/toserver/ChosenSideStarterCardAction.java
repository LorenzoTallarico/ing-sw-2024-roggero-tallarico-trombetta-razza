package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChosenSideStarterCardAction extends Action {

    private final boolean side;

    public ChosenSideStarterCardAction(String author, boolean side) {
        super(ActionType.CHOSENSIDESTARTERCARD, author, null);
        this.side = side;
    }

    public boolean getSide() {
        return side;
    }

}
