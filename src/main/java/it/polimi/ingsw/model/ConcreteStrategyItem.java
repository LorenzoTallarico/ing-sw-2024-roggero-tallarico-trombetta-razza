package it.polimi.ingsw.model;

import java.util.ArrayList;

public class ConcreteStrategyItem implements Strategy {
    private int row;
    private int column;
    private int cardPoints;
    private int jarCounter;
    private int plumeCounter;
    private int scrollCounter;
    private Corner[] cardCorners;
    public int execute(Resource r, Player player, Item item) {
        for (column = player.getArea().getWestBound(); column <= player.getArea().getEastBound(); column++) {
            for (row = player.getArea().getNorthBound(); row <= player.getArea().getSouthBound(); row++) {
                if(!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree()){
                    if(player.getArea().getSpace(row,column).getCard().front) {
                        cardCorners = player.getArea().getSpace(row, column).getCard().getFrontCorners();
                        for(int i=0; i<4; i++){
                            if(cardCorners[i].getType() == CornerType.ITEM && cardCorners[i].isVisible() && cardCorners[i].getItem() == item){
                                cardPoints++;
                            }
                        }
                    }
                }
            }
        }
    return cardPoints/2;
    }
}