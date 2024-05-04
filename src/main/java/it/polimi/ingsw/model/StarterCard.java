package it.polimi.ingsw.model;

import java.io.Serializable;

public class StarterCard extends Card implements Serializable {

    private final Resource secondResource;
    private final Resource thirdResource;

    public StarterCard() {
        secondResource = null;
        thirdResource = null;
        front = false;
        points = -1;
        resource = null;
        frontCorners = null;
        backCorners = null;
    }


    /**
     * Constructor of the class, initializes the resource cards with the given parameters
     * @param resource The type of resource of the card
     * @param secondResource The type of the potential second resource of the card
     * @param thirdResource The type of potential third resource of the card
     * @param frontCorners Corners of the front side of the card
     * @param backCorners Corners of the back side of the card
     */
     public StarterCard(Resource resource, Resource secondResource, Resource thirdResource, Corner[] frontCorners, Corner[] backCorners) {
        this.secondResource = secondResource;
        this.thirdResource = thirdResource;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
    }



    @Override
    public Resource getResource() {
        return resource;
    }
    /**
     * Method that returns the card's potential second resource type
     * @return the card's resource type
     */
    public Resource getSecondResource() {
        return secondResource;
    }

    /**
     * Method that returns the card's potential third resource type
     * @return the card's resource type
     */
    public Resource getThirdResource() {
        return thirdResource;
    }
}
