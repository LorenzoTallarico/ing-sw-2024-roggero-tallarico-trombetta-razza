package it.polimi.ingsw.model;

public class Playground {
int[][] table = new Space[81][81];
int[] resources = new int[4];
int[] items=  new int[3];
int northBound;
int eastBound;
int westBound;
int southBound;

    public Playground(int[][] table, int[] resources, int[] items) {
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
