package it.polimi.ingsw.model;

import sun.util.resources.Bundles;

public class AchievementCard extends Card{
    private Strategy strategy;

    /**
     * Constructor of the class, initializes a new achievement card.
     * It initializes the AchievementCard using the given parameters.
     * @param points integer representing the points the card gives if the requirements are fulfilled
     * @param resource The type of resource of the card
     * @param frontCorners Corners of the front side of the card
     * @param backCorners Corners of the back side of the card
     */
    public AchievementCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners, Strategy strategy){
        this.points = points;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
        this.strategy = strategy;
    }

    /**
     * Method that returns the card's achievement type
     * @return the achievement type
     */


    public int getPoints(){
        return strategy.execute();
    }
}
