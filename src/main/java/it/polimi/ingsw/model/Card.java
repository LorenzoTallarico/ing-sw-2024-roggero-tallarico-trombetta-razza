package it.polimi.ingsw.model;

public abstract class Card {
    boolean front;
    int points;
    Resource resource;
    Corner[] frontCorners = new Corner[4];
    Corner[] backCorners = new Corner[4];

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
