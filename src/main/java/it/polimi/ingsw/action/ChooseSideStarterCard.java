package it.polimi.ingsw.action;

public class ChooseSideStarterCard extends Action {
    int index;

    public ChooseSideStarterCard(String author, int index) {
        super(ActionType.CHOSENSIDESTARTERCARD, author, null);
        this.index = index;
    }
}
