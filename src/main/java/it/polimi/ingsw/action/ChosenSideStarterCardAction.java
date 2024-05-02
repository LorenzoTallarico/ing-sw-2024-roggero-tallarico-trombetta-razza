package it.polimi.ingsw.action;

public class ChosenSideStarterCardAction extends Action {
    int index;

    public ChosenSideStarterCardAction(String author, int index) {
        super(ActionType.CHOSENSIDESTARTERCARD, author, null);
        this.index = index;
    }
}
