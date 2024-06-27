package it.polimi.ingsw.networking.action.toserver;

import it.polimi.ingsw.model.AchievementCard;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
/**
 * Represents an action indicating that a player has chosen an achievement card.
 * This action is sent to the server with the author of the action and the chosen achievement card.
 */
public class ChosenAchievementAction extends Action {
    /**
     * The achievement card that has been chosen by the player.
     */
    private final AchievementCard card;
    /**
     * Constructs a new ChosenAchievementAction with the specified author and chosen achievement card.
     *
     * @param author the author of the action (the player who chose the achievement)
     * @param card the achievement card that has been chosen
     */
    public ChosenAchievementAction(String author, AchievementCard card) {
        super(ActionType.CHOSENACHIEVEMENT, author, null);
        this.card = card;
    }
    /**
     * Gets the achievement card that has been chosen by the player.
     *
     * @return the achievement card that has been chosen
     */
    public AchievementCard getAchievement() {return card;}

}
