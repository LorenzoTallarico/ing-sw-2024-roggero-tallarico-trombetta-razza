package it.polimi.ingsw.action;

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
