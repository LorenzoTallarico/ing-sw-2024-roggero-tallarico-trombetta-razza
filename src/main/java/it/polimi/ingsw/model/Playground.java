package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

public class Playground {
private final Space[][] table;
private Map<Resource, Integer> resources;
private Map<Item, Integer> items;
private int northBound;
private int eastBound;
private int westBound;
private int southBound;

    public Playground() {
        table = new Space[81][81];
        for(int x = 0; x < 81; x++) {
            for(int y = 0; y < 81; y++) {
                if(x % 2 == y % 2)
                    table[x][y] = new Space(true, true);
                else
                    table[x][y] = new Space(false, false);
            }
        }
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

//GETTER AND SETTERS

    public void setSpace(Card card, int x, int y) {
        table[x][y].setCard(card);
        table[x][y].setFree(false);
        if(x < northBound)
            northBound = x;
        else if(x > southBound)
            southBound = x;
        if(y < westBound)
            westBound = y;
        else if(y > eastBound)
            eastBound = y;
    }

    public Space getSpace(int x, int y) {
        return table[x][y];
    }

    public int getNorthBound() {
        return northBound;
    }

    public void setNorthBound(int northBound) {
        this.northBound = northBound;
    }

    public int getEastBound() {
        return eastBound;
    }

    public void setEastBound(int eastBound) {
        this.eastBound = eastBound;
    }

    public int getWestBound() {
        return westBound;
    }

    public void setWestBound(int westBound) {
        this.westBound = westBound;
    }

    public int getSouthBound() {
        return southBound;
    }

    public void setSouthBound(int southBound) {
        this.southBound = southBound;
    }
}
