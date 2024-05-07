package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChosenDrawCardAction extends Action {

    private final int index; //da gestire poi il cast a seconda della carta

    public ChosenDrawCardAction(String author, int index) {
        super(ActionType.CHOSENDRAWCARD, author, null);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
