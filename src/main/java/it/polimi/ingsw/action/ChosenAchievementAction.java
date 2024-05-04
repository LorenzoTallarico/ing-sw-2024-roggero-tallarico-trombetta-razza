package it.polimi.ingsw.action;

import it.polimi.ingsw.model.Card;
public class ChosenAchievementAction extends Action {

    private final Card achievement;

    public ChosenAchievementAction(String recipient, Card card) {
        super(ActionType.CHOSENACHIEVEMENT, null, recipient);
        this.achievement = card;
    }

    public Card getAchievement() {return achievement;}
}
