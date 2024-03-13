package it.polimi.ingsw.model;

public class GoldCard extends Card {
    private int[] reqResources = new int[4];
    private Item reqItem;
    private ReqPoint reqPoints;
    public GoldCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners,int[] reqResources, Item reqItem, ReqPoint reqPoints) {
        this.points = points;
        this.resource = resource;
        for(int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for(int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
        for(int i = 0; i < reqResources.length; i++) {
            this.reqResources[i] = reqResources[i];
        }
        this.reqItem = reqItem;
        this.reqPoints = reqPoints;
    }

    /*
    REMAKE
    va fatta la classe con una mappa e non un vettore, rifare il countResource dopo
     */
    public int countResource(Resource res){
        int rit = 0;
        switch(res) {
            case WOLF:
                rit = reqResources[0];
                break;
            case BUTTERFLY:
                rit = reqResources[1];
                break;
            case LEAF:
                rit = reqResources[2];
                break;
            case MUSHROOM:
                rit = reqResources[3];
                break;
            default:
                rit = -1;
                break;
        }
        return rit;
    }
    public ReqPoint getPointsType() {
        return reqPoints;
    }
    public Item getItem(){
        return reqItem;
    }
}
