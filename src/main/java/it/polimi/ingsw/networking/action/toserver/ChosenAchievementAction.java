package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.model.AchievementCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

public class ChosenAchievementAction extends Action {

    private final AchievementCard card;

    public ChosenAchievementAction(String author, AchievementCard card) {
        super(ActionType.CHOSENACHIEVEMENT, author, null);
        this.card = card;
    }

    public AchievementCard getAchievement() {return card;}

}
