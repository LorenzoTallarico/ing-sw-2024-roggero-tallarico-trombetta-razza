package it.polimi.ingsw.action;

import it.polimi.ingsw.model.Card;

import java.util.ArrayList;

public class ChooseableAchievementsAction extends Action{

    private final ArrayList<Card> achievements;

    public ChooseableAchievementsAction(String recipient, ArrayList<Card> achievements) {
        super(ActionType.CHOOSEABLEACHIEVEMENTS, null, recipient);
        this.achievements= achievements;
    }

    public ArrayList<Card> getAchievements() {
        return achievements;
    }
}
