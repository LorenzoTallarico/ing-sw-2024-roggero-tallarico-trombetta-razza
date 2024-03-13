package it.polimi.ingsw.model;

public class Playground {
private Space[][] table = new Space[81][81];
private int[] resources = new int[4];
private int[] items=  new int[3];
private int northBound;
private int eastBound;
private int westBound;
private int southBound;

    public Playground(Space[][] table, int[] resources, int[] items) {
        this.table = table;
        this.resources = resources;
        this.items = items;
        this.northBound = 40;
        this.eastBound = 40;
        this.westBound = 40;
        this.southBound = 40;
    }

    public Space getSpace(int x, int y){
        return table[x][y];
    }
}
