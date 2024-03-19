package it.polimi.ingsw.model;

public class Corner {
    private final CornerType type;
    private final Resource resource;
    private final Item item;
    private boolean visible;

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
}
