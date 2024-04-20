package it.polimi.ingsw.model;

import java.io.Serializable;

public class ResourceCard extends Card implements Serializable {

    public ResourceCard() {
        front = false;
        points = -1;
        resource = null;
        frontCorners = null;
        backCorners = null;
    }

    /**
     * Constructor of the class, initializes the resource cards with the given parameters
     * @param points integer representing the points the card gives if the requirements are fulfilled
     * @param resource The type of resource of the card
     * @param frontCorners Corners of the front side of the card
     * @param backCorners Corners of the back side of the card
     */
    ResourceCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners) {
        this.points = points;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
    }
}
