package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;
import it.polimi.ingsw.model.Resource;

public class Playground {
private Space[][] table;
private Map<Resource, Integer> resources;
private Map<Item, Integer> items;
private int northBound;
private int eastBound;
private int westBound;
private int southBound;

    public Playground() {
        table = new Space[81][81];
        this.resources = new HashMap<>();
        this.resources.put(Resource.WOLF, 0);
        this.resources.put(Resource.BUTTERFLY, 0);
        this.resources.put(Resource.LEAF, 0);
        this.resources.put(Resource.MUSHROOM, 0);
        this.items = new HashMap<>();
        this.items.put(Item.JAR,  0);
        this.items.put(Item.SCROLL, 0);
        this.items.put(Item.PLUME, 0);
        this.northBound = 40;
        this.eastBound = 40;
        this.westBound = 40;
        this.southBound = 40;
    }

    public Space getSpace(int x, int y){
        return table[x][y];
    }
}
