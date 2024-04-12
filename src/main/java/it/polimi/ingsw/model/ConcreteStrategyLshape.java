package it.polimi.ingsw.model;

public class ConcreteStrategyLshape implements Strategy {
    private int cardPoints = 0;

    public ConcreteStrategyLshape() {    }

    /**
     *
     * @param resource resource the cards have to belong to in order to give points for every L-shaped pattern found in the player area
     * @param player player who owns the card
     * @param item it's useless
     * @return number of points added to the player due to the card
     */
    public int execute(Resource resource, Player player, Item item) {
        int row;
        int column;
        for (column = player.getArea().getWestBound(); column <= player.getArea().getEastBound(); column++) {
            for (row = player.getArea().getNorthBound(); row <= player.getArea().getSouthBound(); row++) {
                if(resource == Resource.MUSHROOM){
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.MUSHROOM && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row+2,column).isDead() && !player.getArea().getSpace(row+2,column).isFree() && player.getArea().getSpace(row+2,column).getCard().resource == Resource.MUSHROOM && !player.getArea().getSpace(row+2,column).isChecked() && !player.getArea().getSpace(row+3,column+1).isDead() && !player.getArea().getSpace(row+3,column+1).isFree() && player.getArea().getSpace(row+3,column+1).getCard().resource == Resource.LEAF && !player.getArea().getSpace(row+3,column+1).isChecked()) {
                        player.getArea().getSpace(row,column).setChecked(true);
                        player.getArea().getSpace(row+2,column).setChecked(true);
                        player.getArea().getSpace(row+3,column+1).setChecked(true);
                        cardPoints = cardPoints + 3;
                    }
                }
                if(resource == Resource.LEAF){
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.LEAF && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row+2,column).isDead() && !player.getArea().getSpace(row+2,column).isFree() && player.getArea().getSpace(row+2,column).getCard().resource == Resource.LEAF && !player.getArea().getSpace(row+2,column).isChecked() && !player.getArea().getSpace(row+3,column-1).isDead() && !player.getArea().getSpace(row+3,column-1).isFree() && player.getArea().getSpace(row+3,column-1).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row+3,column-1).isChecked()) {
                        player.getArea().getSpace(row,column).setChecked(true);
                        player.getArea().getSpace(row+2,column).setChecked(true);
                        player.getArea().getSpace(row+3,column-1).setChecked(true);
                        cardPoints = cardPoints + 3;
                    }
                }
                if(resource == Resource.WOLF){
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.WOLF && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row+2,column).isDead() && !player.getArea().getSpace(row+2,column).isFree() && player.getArea().getSpace(row+2,column).getCard().resource == Resource.WOLF && !player.getArea().getSpace(row+2,column).isChecked() && !player.getArea().getSpace(row-1,column+1).isDead() && !player.getArea().getSpace(row-1,column+1).isFree() && player.getArea().getSpace(row-1,column+1).getCard().resource == Resource.MUSHROOM && !player.getArea().getSpace(row-1,column+1).isChecked()) {
                        player.getArea().getSpace(row,column).setChecked(true);
                        player.getArea().getSpace(row+2,column).setChecked(true);
                        player.getArea().getSpace(row-1,column+1).setChecked(true);
                        cardPoints = cardPoints + 3;
                    }
                }
                if(resource == Resource.BUTTERFLY){
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row+2,column).isDead() && !player.getArea().getSpace(row+2,column).isFree() && player.getArea().getSpace(row+2,column).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row+2,column).isChecked() && !player.getArea().getSpace(row-1,column-1).isDead() && !player.getArea().getSpace(row-1,column-1).isFree() && player.getArea().getSpace(row-1,column-1).getCard().resource == Resource.WOLF && !player.getArea().getSpace(row-1,column-1).isChecked()) {
                        player.getArea().getSpace(row,column).setChecked(true);
                        player.getArea().getSpace(row+2,column).setChecked(true);
                        player.getArea().getSpace(row-1,column-1).setChecked(true);
                        cardPoints = cardPoints + 3;
                    }
                }

            }
        }
    for (column = player.getArea().getWestBound(); column <= player.getArea().getEastBound(); column++) {
        for (row = player.getArea().getNorthBound(); row <= player.getArea().getSouthBound(); row++) {
            if (player.getArea().getSpace(row,column).isChecked()) {
                player.getArea().getSpace(row,column).setChecked(false);
            }
        }
    }
    return cardPoints;
    }
}