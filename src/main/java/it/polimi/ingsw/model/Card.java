package it.polimi.ingsw.model;

public abstract class Card {
    protected boolean front;
    protected int points;
    protected Resource resource;
    protected Corner[] frontCorners = new Corner[4];
    protected Corner[] backCorners = new Corner[4];

//GETTER

    /**
     * Method that tells if a card is showing his front or his back side
     * @return the boolean 'front', true if the front is showing, false otherwise
     */
    public boolean isFront() {
        return front;
    }

    /**
     * Method that returns the card's points
     * @return an integer that represents the card's points
     */
    public int getPoints() {
        return points;
    }

    /**
     * Method that returns the card's resource type
     * @return the card's resource type
     */
    public Resource getResource() {
        return resource;
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
//SETTER

    /**
     * Method that given a parameter sets a boolean that represents which side of the card will be showed
     * @param front The boolean value indicating the side of the card
     */
    public void setFront(boolean front) {
        this.front = front;
    }
}
