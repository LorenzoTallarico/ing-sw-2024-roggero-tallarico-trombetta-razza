package it.polimi.ingsw.model;

import java.io.Serializable;

public class ResourceCard extends Card implements Serializable {

    /**
     * Default constructor for an empty resource card
     */
    public ResourceCard() {
        front = false;
        points = -1;
        resource = null;
        frontCorners = null;
        backCorners = null;
        id = "";
    }

    /**
     * Constructor of the class, initializes the resource cards with the given parameters
     * @param points integer representing the points the card gives if the requirements are fulfilled
     * @param resource The type of resource of the card
     * @param frontCorners Corners of the front side of the card
     * @param backCorners Corners of the back side of the card
     * @param id String representing the id of the card, used for images
     */
    public ResourceCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners, String id) {
        this.points = points;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
        this.id = id;
    }

    /**
     * Method that returns the ID of the current side of the card
     * used to identify the corresponding image
     * @return the string representing the current card&side ID
     */
    public String getSideID() {
        return getSideID(this.front);
    }

    /**
     * Overloading of the previous method, it returns the ID of a
     * specific side of the card, used to identify the corresponding image
     * @param front boolean representing the side, true = front, false = back
     * @return the string representing the wanted card&side ID
     */
    public String getSideID(boolean front) {
        if(front)
            return id;
        String result;
        switch(this.resource) {
            case MUSHROOM:
                result = "001";
                break;
            case LEAF:
                result = "011";
                break;
            case WOLF:
                result = "021";
                break;
            case BUTTERFLY:
                result = "031";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    public boolean equals(ResourceCard res){
        if(this.points != res.getPoints() || this.resource != res.getResource()){
            return false;
        }
        for(int i=0; i<4; i++){
            if(this.getFrontCorners()[i].getType() != res.getFrontCorners()[i].getType()){
                return false;
            }
        }
        return true;
    }
}
