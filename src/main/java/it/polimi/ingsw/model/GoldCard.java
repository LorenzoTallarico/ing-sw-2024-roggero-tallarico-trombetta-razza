package it.polimi.ingsw.model;
import  java.util.Map;
import  java.util.HashMap;

public class GoldCard extends Card {
    private Map<Resource, Integer> reqResources;
    private Item reqItem;
    private ReqPoint reqPoints;


    public GoldCard(){
        reqResources = null;
        reqItem = null;
        reqPoints = null;
        front = false;
        points = -1;
        resource = null;
        frontCorners = null;
        backCorners = null;
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
     */
    public GoldCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners, int[] reqResources, Item reqItem, ReqPoint reqPoints) {
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