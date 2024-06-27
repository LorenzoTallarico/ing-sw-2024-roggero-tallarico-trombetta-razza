package it.polimi.ingsw.model;

import java.io.Serializable;

public class Corner implements Serializable {

    /**
     * Type of the corner
     */
    private final CornerType type;

    /**
     * Resource on the corner if any
     */
    private final Resource resource;

    /**
     * Item on the corner if any
     */
    private final Item item;

    /**
     * Indicates if the corner is visible or hidden by another card
     */
    private boolean visible;

    /**
     * Dummy constructor of the class, initializes a generic corner with dummy values
     */
    public Corner() {
        type = null;
        resource = null;
        item = null;
        visible = true;
    }

    /**
     * Constructor of the class, initializes the corner.
     * It initializes Corner with the given parameters, also setting 'visible' to true
     * @param type The corner's type
     * @param resource The type of resource in the corner
     * @param item The type of item in the corner
     */
    public Corner(CornerType type, Resource resource, Item item) {
        this.type = type;
        this.resource = resource;
        this.item = item;
        visible = true;
    }

    /**
     * Method that returns the corner's type
     * @return the corner's type
     */
    public CornerType getType() {
        return type;
    }

    /**
     * Method that returns the resource in the corner
     * @return the resource in the corner
     */
    public Resource getResource() {
        return resource;
    }

    /**
     * Method that returns the item in the corner
     * @return the item in the corner
     */
    public Item getItem() {
        return item;
    }

    /**
     * Method that tells if a corner is visibile
     * @return the boolean 'visible', true if the corner is visible, otherwise false
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * Method that sets false to the flag 'visible' meaning that the corner is
     * covered by another card, and it's not visible anymore
     */
    public void cover() {
        visible = false;
    }

    /**
     * Method that sets true to the flag 'visible' meaning that the corner is
     * not covered by another card anymore, and it's now visible
     */
    public void uncover() {
        visible = true;
    }



}
