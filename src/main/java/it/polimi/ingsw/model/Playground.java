package it.polimi.ingsw.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Playground of a single player, where the cards are placed
 */
public class Playground implements Serializable {

    /**
     * Matrix made of 81*81 spaces, big enough for all possible combinations of cards
     * in a game with the smallest amount of players (2).
     * (0,0) is the top left corner and (80,80) is the bottom right corner
     */
    private final Space[][] table;

    /**
     * Amount of every single resource currently available on the playground
     */
    private final Map<Resource, Integer> resources;

    /**
     * Amount of every single item currently available on the playground
     */
    private final Map<Item, Integer> items;

    /**
     * Lowest row where a card is placed / northernmost y
     */
    private int northBound;

    /**
     * Highest column where a card is placed / easternmost x
     */
    private int eastBound;

    /**
     * Lowest column where a card is placed / westernmost x
     */
    private int westBound;

    /**
     * Highest row where a card is placed / southernmost y
     */
    private int southBound;

    /**
     * Ordered map used to retrieve the coordinates of a placed card
     * and the order they've been placed, used by the graphical application
     */
    private final LinkedHashMap<Card, int[]> orderedCoords;

    /**
     * Constructor of the playground
     * Sets half of the cells as free and half as dead since the playground
     * works as chessboard where you can only move on the white squares.
     * It also initializes the attributes
     */
    public Playground() {
        orderedCoords = new LinkedHashMap<>();
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

    /**
     * Method that given a card and a position, places the card in the right space, covering adjacent corners
     * and updating values
     * @param card The card to place
     * @param row The integer representing the row index
     * @param column The integer representing the column index
     * @return The integer representing the points made by placing the card, if any
     */
    public int setSpace(Card card, int row, int column) {
        int points = 0;
        if(card.getClass() == StarterCard.class){
            table[40][40].setCard(card);
            table[40][40].setFree(false);
            if(card.isFront()){
                resources.put(card.getResource(), resources.get(card.getResource()) + 1);
                if(((StarterCard)card).getSecondResource() != null) {
                    resources.put(((StarterCard)card).getSecondResource(), resources.get(((StarterCard)card).getSecondResource()) + 1);
                    if(((StarterCard)card).getThirdResource() != null)
                        resources.put(((StarterCard)card).getThirdResource(), resources.get(((StarterCard)card).getThirdResource()) + 1);
                }
            }
        } else { //add permanent resource
            table[row][column].setCard(card);
            table[row][column].setFree(false);
            if(!card.isFront())
                resources.put(card.getResource(), resources.get(card.getResource()) + 1);
        }

        //update bounds
        if (row < northBound)
            northBound = row;
        else if (row > southBound)
            southBound = row;
        if (column < westBound)
            westBound = column;
        else if (column > eastBound)
            eastBound = column;

        //check corners to add items and sources to the playground counter
        Corner[] corners;
        if (card.isFront())
            corners = card.getFrontCorners();
        else
            corners = card.getBackCorners();
        for (Corner corn : corners) {
            if (corn.getType().equals(CornerType.ITEM)) {
                items.put(corn.getItem(), items.get(corn.getItem()) + 1);
            } else if (corn.getType().equals(CornerType.RESOURCE)) {
                resources.put(corn.getResource(), resources.get(corn.getResource()) + 1);
            }
        }

        //cover adjacent cards' corners and subtract their items and resource from the playground counter
        //--- top left corner
        if (row > 0 && column > 0 && table[row - 1][column - 1] != null && !table[row - 1][column - 1].isFree() && !table[row - 1][column - 1].isDead()) {
            //startercards's side are the inverse of resource and gold
            if(table[row - 1][column - 1].getCard().getClass() == StarterCard.class) {
                Card sc = table[row - 1][column - 1].getCard();
                Corner[] tc = sc.isFront() ? sc.getFrontCorners() : sc.getBackCorners();
                tc[1].cover();
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (tc[1].getType().equals(CornerType.ITEM))
                    items.put(tc[1].getItem(), items.get(tc[1].getItem()) - 1);
                if (tc[1].getType().equals(CornerType.RESOURCE))
                    resources.put(tc[1].getResource(), resources.get(tc[1].getResource()) - 1);
            } else {
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (table[row - 1][column - 1].getCard().isFront()) { //the adjacent card is upside
                    table[row - 1][column - 1].getCard().getFrontCorners()[1].cover();
                    if (table[row - 1][column - 1].getCard().getFrontCorners()[1].getType().equals(CornerType.ITEM))
                        items.put(table[row - 1][column - 1].getCard().getFrontCorners()[1].getItem(), items.get(table[row - 1][column - 1].getCard().getFrontCorners()[1].getItem()) - 1);
                    if (table[row - 1][column - 1].getCard().getFrontCorners()[1].getType().equals(CornerType.RESOURCE))
                        resources.put(table[row - 1][column - 1].getCard().getFrontCorners()[1].getResource(), resources.get(table[row - 1][column - 1].getCard().getFrontCorners()[1].getResource()) - 1);
                } else { //the adjacent card is downside
                    table[row - 1][column - 1].getCard().getBackCorners()[1].cover();
                }
            }
        }
        //--- top right corner
        if (row > 0 && column < 80 && table[row - 1][column + 1] != null && !table[row - 1][column + 1].isFree() && !table[row - 1][column + 1].isDead()) {
            //startercards's side are the inverse of resource and gold
            if(table[row - 1][column + 1].getCard().getClass() == StarterCard.class) {
                Card sc = table[row - 1][column + 1].getCard();
                Corner[] tc = sc.isFront() ? sc.getFrontCorners() : sc.getBackCorners();
                tc[2].cover();
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (tc[2].getType().equals(CornerType.ITEM))
                    items.put(tc[2].getItem(), items.get(tc[2].getItem()) - 1);
                if (tc[2].getType().equals(CornerType.RESOURCE))
                    resources.put(tc[2].getResource(), resources.get(tc[2].getResource()) - 1);
            } else {
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (table[row - 1][column + 1].getCard().isFront()) { //the adjacent card is upside
                    table[row - 1][column + 1].getCard().getFrontCorners()[2].cover();
                    if (table[row - 1][column + 1].getCard().getFrontCorners()[2].getType().equals(CornerType.ITEM))
                        items.put(table[row - 1][column + 1].getCard().getFrontCorners()[2].getItem(), items.get(table[row - 1][column + 1].getCard().getFrontCorners()[2].getItem()) - 1);
                    if (table[row - 1][column + 1].getCard().getFrontCorners()[2].getType().equals(CornerType.RESOURCE))
                        resources.put(table[row - 1][column + 1].getCard().getFrontCorners()[2].getResource(), resources.get(table[row - 1][column + 1].getCard().getFrontCorners()[2].getResource()) - 1);
                } else { //the adjacent card is downside
                    table[row - 1][column + 1].getCard().getBackCorners()[2].cover();
                }
            }
        }
        //--- bottom right corner
        if (row < 80 && column < 80 && table[row + 1][column + 1] != null && !table[row + 1][column + 1].isFree() && !table[row + 1][column + 1].isDead()) {
            //startercards's sides are the inverse of resource and gold
            if(table[row + 1][column + 1].getCard().getClass() == StarterCard.class) {
                Card sc = table[row + 1][column + 1].getCard();
                Corner[] tc = sc.isFront() ? sc.getFrontCorners() : sc.getBackCorners();
                tc[3].cover();
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (tc[3].getType().equals(CornerType.ITEM))
                    items.put(tc[3].getItem(), items.get(tc[3].getItem()) - 1);
                if (tc[3].getType().equals(CornerType.RESOURCE))
                    resources.put(tc[3].getResource(), resources.get(tc[3].getResource()) - 1);
            } else {
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (table[row + 1][column + 1].getCard().isFront()) { //the adjacent card is upside
                    table[row + 1][column + 1].getCard().getFrontCorners()[3].cover();
                    if (table[row + 1][column + 1].getCard().getFrontCorners()[3].getType().equals(CornerType.ITEM))
                        items.put(table[row + 1][column + 1].getCard().getFrontCorners()[3].getItem(), items.get(table[row + 1][column + 1].getCard().getFrontCorners()[3].getItem()) - 1);
                    if (table[row + 1][column + 1].getCard().getFrontCorners()[3].getType().equals(CornerType.RESOURCE))
                        resources.put(table[row + 1][column + 1].getCard().getFrontCorners()[3].getResource(), resources.get(table[row + 1][column + 1].getCard().getFrontCorners()[3].getResource()) - 1);
                } else { //the adjacent card is downside
                    table[row + 1][column + 1].getCard().getBackCorners()[3].cover();
                }
            }
        }
        //--- bottom left corner
        if (row < 80 && column > 0 && table[row + 1][column - 1] != null && !table[row + 1][column - 1].isFree() && !table[row + 1][column - 1].isDead()) {
            //startercards's side are the inverse of resource and gold
            if(table[row + 1][column - 1].getCard().getClass() == StarterCard.class) {
                Card sc = table[row + 1][column - 1].getCard();
                Corner[] tc = sc.isFront() ? sc.getFrontCorners() : sc.getBackCorners();
                tc[0].cover();
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (tc[0].getType().equals(CornerType.ITEM))
                    items.put(tc[0].getItem(), items.get(tc[0].getItem()) - 1);
                if (tc[0].getType().equals(CornerType.RESOURCE))
                    resources.put(tc[0].getResource(), resources.get(tc[0].getResource()) - 1);
            } else {
                //increase points if it's gold card with cover corner type
                if (card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.CORNER) && card.isFront())
                    points += 2;
                if (table[row + 1][column - 1].getCard().isFront()) { //the adjacent card is upside
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
        //if the card is a gold card with item requirement, set the points to the available items on the area
        if(card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.ITEM) && card.isFront()) {
            points = items.get(((GoldCard) card).getItem());
        } else if(card.getClass() == GoldCard.class && ((GoldCard) card).getPointsType().equals(ReqPoint.SIMPLE) && card.isFront()){
            points = card.getPoints(); //simple gold card with no requirements
        }
        if(card.getClass() == ResourceCard.class && card.getPoints() == 1 && card.isFront())
            points = 1;

        orderedCoords.put(card, new int[]{row, column});
        return points;
    }

    /**
     * Removes a card from the playground updating the values of adjacent cards and items and resources counter
     * @param row The integer representing the row index
     * @param column The integer representing the column index
     * @param card The card to remove
     */
    public void setPlaygroundBeforePlace(int row, int column, Card card) {
        boolean side = getSpace(row, column).getCard().isFront();       //true = front      false = back
        getSpace(row, column).setCard(null);     //controllare se inizializzati così gli space
        getSpace(row, column).setFree(true);

        //Remove elements from items/resources maps
        int oldValue = 0;
        if(side){
            //front side
            for(int i=0; i<4; i++){
                if(card.getFrontCorners()[i].getType().equals(CornerType.RESOURCE)){
                    oldValue = resources.get(card.getFrontCorners()[i].getResource());
                    resources.put(card.getFrontCorners()[i].getResource(), oldValue-1);
                } else if (card.getFrontCorners()[i].getType().equals(CornerType.ITEM)) {
                    oldValue = items.get(card.getFrontCorners()[i].getItem());
                    items.put(card.getFrontCorners()[i].getItem(), oldValue-1);
                }
            }
        } else {
            //back side (just removes the resource obtained from the back of the card)
            oldValue = resources.get(card.getResource());
            resources.put(card.getResource(), oldValue-1);
        }

        //CORNERS VISIBLE FIX
        //oldValue = 0;
        //top-left
        if(!getSpace(row-1, column-1).isFree() && !getSpace(row-1, column-1).isDead()){
            if(getSpace(row-1, column-1).getCard().isFront()){
                getSpace(row-1, column-1).getCard().getFrontCorners()[1].uncover();
                if(getSpace(row-1, column-1).getCard().getFrontCorners()[1].getType().equals(CornerType.RESOURCE)){
                    oldValue = resources.get(getSpace(row-1, column-1).getCard().getFrontCorners()[1].getResource());
                    resources.put(getSpace(row-1, column-1).getCard().getFrontCorners()[1].getResource(), oldValue+1);
                } else if (getSpace(row-1, column-1).getCard().getFrontCorners()[1].getType().equals(CornerType.ITEM)) {
                    oldValue = items.get(getSpace(row-1, column-1).getCard().getFrontCorners()[1].getItem());
                    items.put(getSpace(row-1, column-1).getCard().getFrontCorners()[1].getItem(), oldValue+1);
                }
            }
            else {
                getSpace(row-1, column-1).getCard().getBackCorners()[1].uncover();
            }
        }

        //top-right
        if(!getSpace(row-1, column+1).isFree() && !getSpace(row-1, column+1).isDead()){
            if(getSpace(row-1, column+1).getCard().isFront()){
                getSpace(row-1, column+1).getCard().getFrontCorners()[2].uncover();
                if(getSpace(row-1, column+1).getCard().getFrontCorners()[2].getType().equals(CornerType.RESOURCE)){
                    oldValue = resources.get(getSpace(row-1, column+1).getCard().getFrontCorners()[2].getResource());
                    resources.put(getSpace(row-1, column+1).getCard().getFrontCorners()[2].getResource(), oldValue+1);
                } else if (getSpace(row-1, column+1).getCard().getFrontCorners()[2].getType().equals(CornerType.ITEM)) {
                    oldValue = items.get(getSpace(row-1, column+1).getCard().getFrontCorners()[2].getItem());
                    items.put(getSpace(row-1, column+1).getCard().getFrontCorners()[2].getItem(), oldValue+1);
                }
            }
            else {
                getSpace(row-1, column+1).getCard().getBackCorners()[2].uncover();
            }
        }

        //bottom-left
        if(!getSpace(row+1, column-1).isFree() && !getSpace(row+1, column-1).isDead()){
            if(getSpace(row+1, column-1).getCard().isFront()){
                getSpace(row+1, column-1).getCard().getFrontCorners()[0].uncover();
                if(getSpace(row+1, column-1).getCard().getFrontCorners()[0].getType().equals(CornerType.RESOURCE)){
                    oldValue = resources.get(getSpace(row+1, column-1).getCard().getFrontCorners()[0].getResource());
                    resources.put(getSpace(row+1, column-1).getCard().getFrontCorners()[0].getResource(), oldValue+1);
                } else if (getSpace(row+1, column-1).getCard().getFrontCorners()[0].getType().equals(CornerType.ITEM)) {
                    oldValue = items.get(getSpace(row+1, column-1).getCard().getFrontCorners()[0].getItem());
                    items.put(getSpace(row+1, column-1).getCard().getFrontCorners()[0].getItem(), oldValue+1);
                }
            }
            else {
                getSpace(row+1, column-1).getCard().getBackCorners()[0].uncover();
            }
        }

        //bottom-right
        if(!getSpace(row+1, column+1).isFree() && !getSpace(row+1, column+1).isDead()){
            if(getSpace(row+1, column+1).getCard().isFront()){
                getSpace(row+1, column+1).getCard().getFrontCorners()[3].uncover();
                if(getSpace(row+1, column+1).getCard().getFrontCorners()[3].getType().equals(CornerType.RESOURCE)){
                    oldValue = resources.get(getSpace(row+1, column+1).getCard().getFrontCorners()[3].getResource());
                    resources.put(getSpace(row+1, column+1).getCard().getFrontCorners()[3].getResource(), oldValue+1);
                } else if (getSpace(row+1, column+1).getCard().getFrontCorners()[3].getType().equals(CornerType.ITEM)) {
                    oldValue = items.get(getSpace(row+1, column+1).getCard().getFrontCorners()[3].getItem());
                    items.put(getSpace(row+1, column+1).getCard().getFrontCorners()[3].getItem(), oldValue+1);
                }
            }
            else {
                getSpace(row+1, column+1).getCard().getBackCorners()[3].uncover();
            }
        }

        removeLastOrderedCoord();
    }

    /**
     * Removes last card placed from the linked hash map, used when the player disconnects before drawing a card
     */
    public void removeLastOrderedCoord() {
        if (!orderedCoords.isEmpty()) {
            Card lastKey = null;
            for (Card key : orderedCoords.keySet()) {
                lastKey = key;
            }
            if (lastKey != null) {
                orderedCoords.remove(lastKey);
            }
        }
    }



    //getters

    /**
     * Method that returns the amount available on the playground of a specified resource
     * @param res The resource type we want to know the number
     * @return The integer representing the number of the available specified resource
     */
    public int countResources(Resource res){
        return resources.get(res);
    }

    /**
     * Method that returns the amount available on the playground of a specified item
     * @param it The item type we want to know the number
     * @return The integer representing the number of the available specified item
     */
    public int countItems(Item it){
        return items.get(it);
    }

    /**
     * Method that returns the space in a specified position on the playground
     * @param row The row where my space is
     * @param column The column where my space is
     * @return The space in the specified position
     */
    public Space getSpace(int row, int column) {
        return table[row][column];
    }

    /**
     * Method that lowest row with at least one card, the northernmost one on the playground
     * @return The integer representing the index of the northernmost row with a card
     */
    public int getNorthBound() {
        return northBound;
    }

    /**
     * Method that greatest column with at least one card, the easternmost one on the playground
     * @return The integer representing the index of the easternmost row with a card
     */
    public int getEastBound() {
        return eastBound;
    }

    /**
     * Method that lowest column with at least one card, the westernmost one on the playground
     * @return The integer representing the index of the westernmost row with a card
     */
    public int getWestBound() {
        return westBound;
    }

    /**
     * Method that greater row with at least one card, the southernmost one on the playground
     * @return The integer representing the index of the southernmost row with a card
     */
    public int getSouthBound() {
        return southBound;
    }

    /**
     * Method that returns a map of cards in the order they've been place
     * @return the Map: Card, int[2], first int is row (y), the second is column (x)
     */
    public Map<Card, int[]> getOrderedCoords() {
        return orderedCoords;
    }
}
