package it.polimi.ingsw.action;

public class ChosenSideStarterCardAction extends Action {
    private final boolean front;

    public ChosenSideStarterCardAction(String author, boolean front) {
        super(ActionType.CHOSENSIDESTARTERCARD, author, null);
        this.front = front;
    }
}
