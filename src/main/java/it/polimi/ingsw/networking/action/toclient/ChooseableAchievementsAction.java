package it.polimi.ingsw.networking.action.toclient;

import it.polimi.ingsw.model.AchievementCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;

import java.util.ArrayList;

/**
 * notifies the player of possible achievement cards to be chosen
 */
public class ChooseableAchievementsAction extends Action {
    /**
     * achievement cards to be possibly chosen
     */
    private final ArrayList<AchievementCard> achievements;
    /**
     * common achievements
     */
    private final ArrayList<AchievementCard> commonGoals;

    /**
     * constructor of ChooseableAchievementAction class
     * @param recipient recipient of the action
     * @param achievements achievements to be possibly choosed
     * @param commonGoals common achievements
     */
    public ChooseableAchievementsAction(String recipient, ArrayList<AchievementCard> achievements, ArrayList<AchievementCard> commonGoals) {
        super(ActionType.CHOOSEABLEACHIEVEMENTS, null, recipient);
        this.achievements = achievements;
        this.commonGoals = commonGoals;
    }

    /**
     * gets the achievements to be possibly choosed
     * @return ArrayList of achievementCards
     */
    public ArrayList<AchievementCard> getAchievements() {
        return achievements;
    }

    /**
     * gets the common achievementCards
     * @return ArrayList of achievement cards
     */
    public ArrayList<AchievementCard> getCommonGoals() {
        return commonGoals;
    }

}
