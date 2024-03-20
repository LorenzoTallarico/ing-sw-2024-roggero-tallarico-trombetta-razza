package it.polimi.ingsw.model;

import java.util.HashMap;
import java.util.Map;

public class Playground {
public Space[][] table;
private Map<Resource, Integer> resources;
private Map<Item, Integer> items;
public int northBound;
public int eastBound;
public int westBound;
public int southBound;

    public Playground() {
        table = new Space[81][81];
        for(int x = 0; x < 81; x++) {
            for(int y = 0; y < 81; y++) {
                if(x % 2 == y % 2)
                    table[x][y] = new Space(true, false);
                else
                    table[x][y] = new Space(false, true);
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

    /**
     * Method that given a card and a position, places the card in the right space, covering adjacents corners
     * and updating values
     * @param card The card to place
     * @param row The integer representing the row index
     * @param column The integer representing the column index
     */
    public void setSpace(Card card, int row, int column) {
        table[row][column].setCard(card);
        table[row][column].setFree(false);

        //update bounds
        if(row < northBound)
            northBound = row;
        else if(row > southBound)
            southBound = row;
        if(column < westBound)
            westBound = column;
        else if(column > eastBound)
            eastBound = column;

        //check corners to add items and sources to the playground counter
        Corner[] corners;
        if(card.isFront())
            corners = card.getFrontCorners();
        else
            corners = card.getBackCorners();
        for(Corner corn : corners) {
            if(corn.getType().equals(CornerType.ITEM)) {
                items.put(corn.getItem(), items.get(corn.getItem()) + 1);
            } else if(corn.getType().equals(CornerType.RESOURCE)) {
                resources.put(corn.getResource(), resources.get(corn.getResource()) + 1);            }
        }

        //cover adjacent cards' corners and subtract their items and resource from the playground counter
        //--- top left corner
        if(row > 0 && column > 0 && table[row-1][column-1] != null && !table[row-1][column-1].isFree() && !table[row-1][column-1].isDead()) {
            if(table[row-1][column-1].getCard().isFront()) { //the adjacent card is upside
                table[row - 1][column - 1].getCard().getFrontCorners()[1].cover();
                if (table[row - 1][column - 1].getCard().getFrontCorners()[1].getType().equals(CornerType.ITEM))
                    items.put(table[row - 1][column - 1].getCard().getFrontCorners()[1].getItem(), items.get(table[row - 1][column - 1].getCard().getFrontCorners()[1].getItem()) - 1);
                if (table[row - 1][column - 1].getCard().getFrontCorners()[1].getType().equals(CornerType.RESOURCE))
                    resources.put(table[row - 1][column - 1].getCard().getFrontCorners()[1].getResource(), resources.get(table[row - 1][column - 1].getCard().getFrontCorners()[1].getResource()) - 1);
            } else { //the adjacent card is downside
                table[row - 1][column - 1].getCard().getBackCorners()[1].cover();
            }
        }
        //--- top right corner
        if(row > 0 && column < 80 && table[row-1][column+1] != null && !table[row-1][column+1].isFree() && !table[row-1][column+1].isDead()) {
            if(table[row-1][column+1].getCard().isFront()) { //the adjacent card is upside
                table[row - 1][column + 1].getCard().getFrontCorners()[2].cover();
                if (table[row - 1][column + 1].getCard().getFrontCorners()[2].getType().equals(CornerType.ITEM))
                    items.put(table[row - 1][column + 1].getCard().getFrontCorners()[2].getItem(), items.get(table[row - 1][column + 1].getCard().getFrontCorners()[1].getItem()) - 1);
                if (table[row - 1][column + 1].getCard().getFrontCorners()[2].getType().equals(CornerType.RESOURCE))
                    resources.put(table[row - 1][column + 1].getCard().getFrontCorners()[2].getResource(), resources.get(table[row - 1][column + 1].getCard().getFrontCorners()[1].getResource()) - 1);
            } else { //the adjacent card is downside
                table[row - 1][column + 1].getCard().getBackCorners()[2].cover();
            }
        }
        //--- bottom right corner
        if(row < 80 && column < 80 && table[row+1][column+1] != null && !table[row+1][column+1].isFree() && !table[row+1][column+1].isDead()) {
            if(table[row+1][column+1].getCard().isFront()) { //the adjacent card is upside
                table[row + 1][column + 1].getCard().getFrontCorners()[3].cover();
                if (table[row + 1][column + 1].getCard().getFrontCorners()[3].getType().equals(CornerType.ITEM))
                    items.put(table[row + 1][column + 1].getCard().getFrontCorners()[3].getItem(), items.get(table[row + 1][column + 1].getCard().getFrontCorners()[3].getItem()) - 1);
                if (table[row + 1][column + 1].getCard().getFrontCorners()[3].getType().equals(CornerType.RESOURCE))
                    resources.put(table[row + 1][column + 1].getCard().getFrontCorners()[3].getResource(), resources.get(table[row + 1][column + 1].getCard().getFrontCorners()[3].getResource()) - 1);
            } else { //the adjacent card is downside
                table[row + 1][column + 1].getCard().getBackCorners()[3].cover();
            }
        }
        //--- bottom left corner
        if(row < 80 && column > 0 && table[row+1][column-1] != null && !table[row+1][column-1].isFree() && !table[row+1][column-1].isDead()) {
            if(table[row+1][column-1].getCard().isFront()) { //the adjacent card is upside
                table[row + 1][column - 1].getCard().getFrontCorners()[0].cover();
                if (table[row + 1][column - 1].getCard().getFrontCorners()[0].getType().equals(CornerType.ITEM))
                    items.put(table[row + 1][column - 1].getCard().getFrontCorners()[0].getItem(), items.get(table[row + 1][column - 1].getCard().getFrontCorners()[0].getItem()) - 1);
                if (table[row + 1][column - 1].getCard().getFrontCorners()[0].getType().equals(CornerType.RESOURCE))
                    resources.put(table[row + 1][column - 1].getCard().getFrontCorners()[0].getResource(), resources.get(table[row + 1][column - 1].getCard().getFrontCorners()[0].getResource()) - 1);
            } else { //the adjacent card is downside
                table[row + 1][column - 1].getCard().getBackCorners()[0].cover();
            }
        }
    }

    public int countResource(Resource res){
        return resources.get(res);
    }

    public int countItems(Item it){
        return items.get(it);
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
