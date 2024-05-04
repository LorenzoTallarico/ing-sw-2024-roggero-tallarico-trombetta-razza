package it.polimi.ingsw.action;

import it.polimi.ingsw.model.AchievementCard;
import it.polimi.ingsw.model.Card;

public class ChosenDrawCardAction extends Action{

    private final Card card; //da gestire poi il cast a seconda della carta

    public ChosenDrawCardAction(String author, Card card) {
        super(ActionType.CHOSENDRAWCARD, author, null);
        this.card = card;
    }

    public Card getCard() {
        return card;
    }
}
