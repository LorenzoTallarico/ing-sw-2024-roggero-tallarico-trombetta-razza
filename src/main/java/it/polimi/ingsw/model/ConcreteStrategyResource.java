package it.polimi.ingsw.model;

import java.io.Serializable;

public class ConcreteStrategyResource implements Strategy, Serializable {

    public ConcreteStrategyResource() {    }

    /**
     *
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
