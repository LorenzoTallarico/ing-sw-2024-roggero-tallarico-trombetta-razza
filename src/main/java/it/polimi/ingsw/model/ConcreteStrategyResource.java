package it.polimi.ingsw.model;

public class ConcreteStrategyResource implements Strategy {
    private int row;
    private int column;
    private int cardPoints;
    private Corner[] cardCorners;
    private StarterCard starterCard;
    public int execute(Resource resource, Player player, Item item) {
        for (column = player.getArea().getWestBound(); column <= player.getArea().getEastBound(); column++) {
            for (row = player.getArea().getNorthBound(); row <= player.getArea().getSouthBound(); row++) {
                if (!player.getArea().getSpace(row, column).isDead() && !player.getArea().getSpace(row, column).isFree()) {
                    if (player.getArea().getSpace(row, column).getCard().front) {
                        cardCorners = player.getArea().getSpace(row, column).getCard().getFrontCorners();
                        for (int i = 0; i < 4; i++) {
                            if (cardCorners[i].getType() == CornerType.RESOURCE && cardCorners[i].isVisible() && cardCorners[i].getResource() == resource) {
                                cardPoints++;
                            }
                        }
                    } else {
                        if (player.getArea().getSpace(row, column).getCard().getResource() == resource) {
                            cardPoints++;
                        }
                    }

                }
                if(player.getArea().getSpace(row, column).getCard() instanceof StarterCard && !player.getArea().getSpace(row, column).getCard().front){
                    starterCard = ((StarterCard) player.getArea().getSpace(row, column).getCard());
                    if(starterCard.getSecondResource() == resource ){
                        cardPoints++;
                    }
                    if(starterCard.getThirdResource() == resource ){
                        cardPoints++;
                    }
                }
            }
        }
    return (cardPoints/3)*2;
    }
}
