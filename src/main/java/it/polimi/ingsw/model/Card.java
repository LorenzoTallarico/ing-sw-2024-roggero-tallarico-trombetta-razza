package it.polimi.ingsw.model;

public abstract class Card {
    protected boolean front;
    protected int points;
    protected Resource resource;
    protected Corner[] frontCorners = new Corner[4];
    protected Corner[] backCorners = new Corner[4];

//GETTER

    public boolean isFront() {
        return front;
    }

    public int getPoints() {
        return points;
    }

    public Resource getResource() {
        return resource;
    }

    public Corner[] getFrontCorners() {
        return frontCorners;
    }

    public Corner[] getBackCorners() {
        return backCorners;
    }
//SETTER

    public void setFront(boolean front) {
        this.front = front;
    }
}
