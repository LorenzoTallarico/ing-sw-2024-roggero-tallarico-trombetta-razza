package it.polimi.ingsw.model;

public class Corner {
    private CornerType type;
    private Resource resource;
    private Item item;
    private boolean visible;

    public Corner(CornerType type, Resource resource, Item item, boolean visible) {
        this.type = type;
        this.resource = resource;
        this.item = item;
        this.visible = visible;
    }

    public CornerType getType() {
        return type;
    }

    public Resource getResource() {
        return resource;
    }

    public Item getItem() {
        return item;
    }

    public boolean isVisible() {
        return visible;
    }
}
