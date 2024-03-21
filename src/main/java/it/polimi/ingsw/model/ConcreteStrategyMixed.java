package it.polimi.ingsw.model;

public class ConcreteStrategyMixed implements Strategy{
    private int row;
    private int column;
    private int jarCounter;
    private int scrollCounter;
    private int plumeCounter;
    Corner[] cardCorners;
    public int execute(Resource resource,Player player, Item item){
        for (column = player.getArea().getWestBound(); column <= player.getArea().getEastBound(); column++) {
            for (row = player.getArea().getNorthBound(); row <= player.getArea().getSouthBound(); row++) {
                if(!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree()){
                    if(player.getArea().getSpace(row,column).getCard().front) {
                        cardCorners = player.getArea().getSpace(row, column).getCard().getFrontCorners();
                        for(int i=0; i<4; i++){
                            if(cardCorners[i].getType() == CornerType.ITEM && cardCorners[i].isVisible() && cardCorners[i].getItem() == Item.JAR){
                                jarCounter++;
                            }
                            if(cardCorners[i].getType() == CornerType.ITEM && cardCorners[i].isVisible() && cardCorners[i].getItem() == Item.PLUME){
                                plumeCounter++;
                            }
                            if(cardCorners[i].getType() == CornerType.ITEM && cardCorners[i].isVisible() && cardCorners[i].getItem() == Item.SCROLL){
                                scrollCounter++;
                            }
                        }
                    }
                }
            }
        }
    return Math.min(jarCounter,Math.min(scrollCounter,plumeCounter))*3; // ritorna il piu piccolo tra i tre contatori
    }
}
