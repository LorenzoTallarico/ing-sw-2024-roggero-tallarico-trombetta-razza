package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.AchievementCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

public class ChooseableAchievementsAction extends Action {

    private final ArrayList<AchievementCard> achievements;
    private final ArrayList<AchievementCard> commonGoals;

    public ChooseableAchievementsAction(String recipient, ArrayList<AchievementCard> achievements, ArrayList<AchievementCard> commonGoals) {
        super(ActionType.CHOOSEABLEACHIEVEMENTS, null, recipient);
        this.achievements = achievements;
        this.commonGoals = commonGoals;
    }

    public ArrayList<AchievementCard> getAchievements() {
        return achievements;
    }

    public ArrayList<AchievementCard> getCommonGoals() {
        return commonGoals;
    }

}
