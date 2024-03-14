package it.polimi.ingsw.model;
import  java.util.Map;
import  java.util.HashMap;

public class GoldCard extends Card {
    private Map<Resource, Integer> reqResources = new HashMap<>();
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
        for(int i = 0; i < this.reqResources.size(); i++) {
            this.reqResources.put(resource.WOLF, reqResources[0]);
            this.reqResources.put(resource.BUTTERFLY, reqResources[1]);
            this.reqResources.put(resource.LEAF, reqResources[2]);
            this.reqResources.put(resource.MUSHROOM, reqResources[3]);
        }
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
