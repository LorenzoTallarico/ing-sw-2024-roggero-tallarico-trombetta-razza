package it.polimi.ingsw.model;

import java.io.Serializable;
/**
 * Represents a space in the game that can hold a card and has various states.
 */
public class Space implements Serializable {

    private Card card;
    private boolean free;
    private boolean dead;
    private boolean checked;
    /**
     * Default constructor for a space with no card.
     */
    public Space(){
        card = null;
    }
    /**
     * Constructs a space with the specified states.
     *
     * @param free  whether the space is free
     * @param dead  whether the space is dead
     */
    public Space(boolean free, boolean dead) {
        this.free = free;
        this.dead = dead;
        checked = false;
    }

//GETTER
    /**
     * Returns the card in this space.
     *
     * @return the card in this space, or null if there is no card
     */
    public Card getCard() {
        return card;
    }

    /**
     * Returns whether this space is free.
     *
     * @return true if the space is free, false otherwise
     */
    public boolean isFree() {
        return free;
    }

    /**
     * Returns whether this space is dead.
     *
     * @return true if the space is dead, false otherwise
     */
    public boolean isDead() {
        return dead;
    }

    /**
     * Returns whether this space has been checked.
     *
     * @return true if the space has been checked, false otherwise
     */
    public boolean isChecked() {
        return checked;
    }

//SETTER
    /**
     * Sets the card in this space.
     *
     * @param card the card to place in this space
     */
    public void setCard(Card card) {
        this.card = card;
    }

    /**
     * Sets the space free.
     *
     * @param free true if the space needs to be set free
     */
    public void setFree(boolean free) {
        this.free = free;
    }
    /**
     * Sets the space dead.
     *
     * @param dead true if the space is dead, false otherwise
     */
    public void setDead(boolean dead) {
        this.dead = dead;
    }
    /**
     * Sets the space checked.
     *
     * @param checked true if the space needs to be checked
     */
    public void setChecked(boolean checked) {
        this.checked = checked;
    }
}
