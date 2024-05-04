package it.polimi.ingsw.action;

import it.polimi.ingsw.model.AchievementCard;

public class ChosenAchievementAction extends Action {

    private final AchievementCard card;

    public ChosenAchievementAction(String author, AchievementCard card) {
        super(ActionType.CHOSENACHIEVEMENT, author, null);
        this.card = card;
    }

    public AchievementCard getAchievement() {return card;}

}
