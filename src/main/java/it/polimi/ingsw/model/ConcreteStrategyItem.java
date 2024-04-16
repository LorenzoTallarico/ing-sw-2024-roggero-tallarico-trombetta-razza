package it.polimi.ingsw.model;

import java.io.Serializable;

public class ConcreteStrategyItem implements Strategy, Serializable {


    public ConcreteStrategyItem() {    }
    /**
     *
     * @param resource it's useless
     * @param player this is the player who owns the card
     * @param item this is the item whose number is counted to determine number of points
     * @return number of points added to the player due to the card
     */
    public int execute(Resource resource, Player player, Item item) {
        int cardPoints = player.getArea().countItems(item);
        return (cardPoints/2)*2;
    }
}