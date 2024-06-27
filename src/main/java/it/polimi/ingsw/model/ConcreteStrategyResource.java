package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Concrete strategy implementation for calculating points based on a pattern that counts the resources in a player's area.
 * Implements the Strategy interface.
 */
public class ConcreteStrategyResource implements Strategy, Serializable {

    /**
     * Default constructor for ConcreteStrategyResource.
     */
    public ConcreteStrategyResource() {    }

    /**
     * Executes the strategy to calculate points based on a pattern that counts the resources in a player's area.
     * @param resource this is the resource whose number is counted to determine number of points
     * @param player the player who owns the card
     * @param item it's useless
     * @return number of points added to the player due to the achievement card
     */
    public int execute(Resource resource, Player player,Item item) {
        int cardPoints = player.getArea().countResources(resource);
        return (cardPoints/3)*2;
    }

}
