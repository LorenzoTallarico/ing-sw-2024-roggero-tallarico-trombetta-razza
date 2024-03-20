package it.polimi.ingsw.model;

public class ConcreteStrategyDiagonal implements Strategy{
    private int row;
    private int column;
    private int cardPoints = 0;
    public int execute(Resource resource,Player player) {
        for (column = player.getArea().getWestBound(); column <= player.getArea().getEastBound(); column++) {
            for (row = player.getArea().getNorthBound(); row <= player.getArea().getSouthBound(); row++) {
                if(resource==Resource.MUSHROOM) {
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.MUSHROOM && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row-1,column+1).isDead() && !player.getArea().getSpace(row-1,column+1).isFree() && player.getArea().getSpace(row - 1,column + 1).getCard().resource == Resource.MUSHROOM && !player.getArea().getSpace(row-1,column+1).isChecked() && !player.getArea().getSpace(row - 2,column + 2).isDead() && !player.getArea().getSpace(row - 2,column + 2).isFree() && player.getArea().getSpace(row - 2,column + 2).getCard().resource == Resource.MUSHROOM && !player.getArea().getSpace(row - 2,column + 2).isChecked()) {
                        player.getArea().getSpace(row,column).setChecked(true);
                        player.getArea().getSpace(row - 1,column + 1).setChecked(true);
                        player.getArea().getSpace(row - 2,column + 2).setChecked(true);
                        cardPoints = cardPoints + 2;

                    }
                }
                if(resource==Resource.WOLF) {
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.WOLF && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row - 1,column + 1).isDead() && !player.getArea().getSpace(row-1,column+1).isFree() && player.getArea().getSpace(row - 1,column + 1).getCard().resource == Resource.WOLF && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row - 2,column + 2).isDead() && !player.getArea().getSpace(row-2,column+2).isFree() && player.getArea().getSpace(row - 2,column + 2).getCard().resource == Resource.WOLF && !player.getArea().getSpace(row - 2,column + 2).isChecked()) {
                                player.getArea().getSpace(row,column).setChecked(true);
                                player.getArea().getSpace(row - 1,column + 1).setChecked(true);
                                player.getArea().getSpace(row - 2,column + 2).setChecked(true);
                                cardPoints = cardPoints + 2;
                    }
                }
                if(resource==Resource.LEAF) {
                    if (!player.getArea().getSpace(row,column).isDead() && player.getArea().getSpace(row,column).isFree() == true && player.getArea().getSpace(row,column).getCard().resource == Resource.LEAF && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row + 1,column + 1).isDead() && !player.getArea().getSpace(row+1,column+1).isFree() && player.getArea().getSpace(row + 1,column + 1).getCard().resource == Resource.LEAF && !player.getArea().getSpace(row+1,column+1).isChecked() && !player.getArea().getSpace(row + 2, column + 2).isDead() && !player.getArea().getSpace(row + 2, column + 2).isFree() && player.getArea().getSpace(row + 2, column + 2).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row + 2, column + 2).isChecked()) {
                        player.getArea().getSpace(row,column).setChecked(true);
                        player.getArea().getSpace(row + 1,column + 1).setChecked(true);
                        player.getArea().getSpace(row + 2,column + 2).setChecked(true);
                        cardPoints = cardPoints + 2;
                    }
                }
                if(resource==Resource.BUTTERFLY) {
                    if (!player.getArea().getSpace(row,column).isDead() && !player.getArea().getSpace(row,column).isFree() && player.getArea().getSpace(row,column).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row,column).isChecked() && !player.getArea().getSpace(row + 1,column + 1).isDead() && !player.getArea().getSpace(row + 1,column + 1).isFree() && player.getArea().getSpace(row + 1,column + 1).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row+1,column+1).isChecked() && !player.getArea().getSpace(row + 2, column + 2).isDead() && !player.getArea().getSpace(row + 2, column + 2).isFree() && player.getArea().getSpace(row + 2, column + 2).getCard().resource == Resource.BUTTERFLY && !player.getArea().getSpace(row + 2, column + 2).isChecked()) {
                            player.getArea().getSpace(row, column).setChecked(true);
                            player.getArea().getSpace(row + 1, column + 1).setChecked(true);
                            player.getArea().getSpace(row + 2, column + 2).setChecked(true);
                            cardPoints = cardPoints + 2;
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
