package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Concrete strategy implementation for calculating points based on a pattern that counts the items in a player's area.
 * Implements the Strategy interface.
 */
public class ConcreteStrategyItem implements Strategy, Serializable {

    /**
     * Default constructor for ConcreteStrategyItem.
     */
    public ConcreteStrategyItem() {    }

    /**
     * Executes the strategy to calculate points based on items in the player's area.
     * @param resource it's useless for this pattern
     * @param player this is the player who owns the card
     * @param item this is the item whose number is counted to determine number of points
     * @return number of points added to the player due to the card
     */
    public int execute(Resource resource, Player player, Item item) {
        int cardPoints = player.getArea().countItems(item);
        return (cardPoints/2)*2;
    }

}