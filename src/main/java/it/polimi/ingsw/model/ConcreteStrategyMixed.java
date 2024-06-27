package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Concrete strategy implementation for calculating points based on a mixed pattern in a player's area.
 * Implements the Strategy interface.
 */
public class ConcreteStrategyMixed implements Strategy, Serializable {

    /**
     * Default constructor for ConcreteStrategyMixed.
     */
    public ConcreteStrategyMixed() {    }

    /**
     * Executes the strategy to calculate points based on a mixed pattern in the player's area.
     * @param resource it's useless for this pattern
     * @param player the player who owns the card
     * @param item it's useless for this pattern
     * @return number of points added to the player due to the achievement card
     */
    public int execute(Resource resource,Player player, Item item) {
        int jarCounter = player.getArea().countItems(Item.JAR);
        int scrollCounter = player.getArea().countItems(Item.SCROLL);
        int plumeCounter = player.getArea().countItems(Item.PLUME);
        return Math.min(jarCounter,Math.min(scrollCounter,plumeCounter))*3; // ritorna il piu piccolo tra i tre contatori
    }
}
