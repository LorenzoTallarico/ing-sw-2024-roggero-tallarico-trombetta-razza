package it.polimi.ingsw.model;
import  java.util.Map;
import  java.util.HashMap;

public class GoldCard extends Card {
    private Map<Resource, Integer> reqResources;
    private Item reqItem;
    private ReqPoint reqPoints;

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
