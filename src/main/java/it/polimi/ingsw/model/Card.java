package it.polimi.ingsw.model;

import java.io.Serializable;

public abstract class Card implements Serializable {
    protected boolean front;
    protected int points;
    protected Resource resource;
    protected Corner[] frontCorners = new Corner[4];
    protected Corner[] backCorners = new Corner[4];
    protected String id;

//GETTER

    /**
     * Method that tells if a card is showing his front or his back side
     * @return the boolean 'front', true if the front is showing, false otherwise
     */
    public boolean isFront() {
        return front;
    }

    /**
     * Method that returns the card's resource type
     * @return the card's resource type
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Method that returns the card's points
     * @return the card's points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Method that returns the corners of the front side of the card
     * @return the array of the card's front side corners
     */
    public Corner[] getFrontCorners() {
        return frontCorners;
    }

    /**
     * Method that returns the corners of the back side of the card
     * @return the array of the card's back side corners
     */
    public Corner[] getBackCorners() {
        return backCorners;
    }

    /**
     * Method that returns the ID of the card used to identify the image
     * @return the string representing the card's ID
     */
    public String getID() {
        return id;
    }

//SETTER

    /**
     * Method that given a parameter sets a boolean that represents which side of the card will be showed
     * @param front The boolean value indicating the side of the card
     */
    public void setFront(boolean front) {
        this.front = front;
    }

}
