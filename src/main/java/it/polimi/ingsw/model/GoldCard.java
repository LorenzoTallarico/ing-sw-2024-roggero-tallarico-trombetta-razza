package it.polimi.ingsw.model;

import java.io.Serializable;
import  java.util.Map;
import  java.util.HashMap;

public class GoldCard extends Card implements Serializable {

    private Map<Resource, Integer> reqResources;
    private Item reqItem;
    private ReqPoint reqPoints;

    /**
     * Default constructor for an empty gold card
     */
    public GoldCard(){
        reqResources = null;
        reqItem = null;
        reqPoints = null;
        front = false;
        points = -1;
        resource = null;
        frontCorners = null;
        backCorners = null;
        id = "";
    }

    /**
     * Constructor of the class, initializes the gold cards with the given parameters
     * @param points integer representing the points the card gives if the requirements are fulfilled
     * @param resource The type of resource of the card
     * @param frontCorners Corners of the front side of the card
     * @param backCorners Corners of the back side of the card
     * @param reqResources Array of int counting the number of each resource required to place the card (?)
     * @param reqItem Item required to place the card if reqPoints is equal to Item
     * @param reqPoints The type of requirement needed to obtain the points
     * @param id String representing the id of the card, used for images
     */
    public GoldCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners, int[] reqResources, Item reqItem, ReqPoint reqPoints, String id) {
        this.points = points;
        this.resource = resource;
        for(int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for(int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
        this.reqResources = new HashMap<>();
        this.reqResources.put(Resource.WOLF, reqResources[0]);
        this.reqResources.put(Resource.BUTTERFLY, reqResources[1]);
        this.reqResources.put(Resource.LEAF, reqResources[2]);
        this.reqResources.put(Resource.MUSHROOM, reqResources[3]);
        this.reqItem = reqItem;
        this.reqPoints = reqPoints;
        this.id = id;
    }

    /**
     * Method that returns the ID of the current side of the card
     * used to identify the corresponding image
     * @return the string representing the current card&side ID
     */
    public String getSideID() {
        return getSideID(this.front);
    }

    /**
     * Overloading of the previous method, it returns the ID of a
     * specific side of the card, used to identify the corresponding image
     * @param front boolean representing the side, true = front, false = back
     * @return the string representing the wanted card&side ID
     */
    public String getSideID(boolean front) {
        if(front)
            return id;
        String result;
        switch(this.resource) {
            case MUSHROOM:
                result = "041";
                break;
            case LEAF:
                result = "051";
                break;
            case WOLF:
                result = "061";
                break;
            case BUTTERFLY:
                result = "071";
                break;
            default:
                result = "";
                break;
        }
        return result;
    }

    public boolean equals(GoldCard gol){
        if(this.points != gol.getPoints() || this.resource != gol.getResource()){
            return false;
        }
        for(int i=0; i<4; i++){
            if(this.getFrontCorners()[i].getType() != gol.getFrontCorners()[i].getType()){
                return false;
            }
        }
        return true;
    }

    public int countResource(Resource res){
        return reqResources.get(res);
    }

    public ReqPoint getPointsType() {
        return reqPoints;
    }

    public Item getItem(){
        return reqItem;
    }
}