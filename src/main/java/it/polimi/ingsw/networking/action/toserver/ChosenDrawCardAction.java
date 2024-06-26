package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChosenDrawCardAction extends Action {

    private final int index;
    /*
    1: gold card -> commonGold.get(0)
    2: gold card -> commonGold.get(1)
    3: resource card -> commonResource.get(0)
    4: resource card -> commonResource.get(1)
    5: gold deck -> goldDeck.pop()
    6: resource deck -> resourceDeck.pop();
     */
    public ChosenDrawCardAction(String author, int index) {
        super(ActionType.CHOSENDRAWCARD, author, null);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
