package it.polimi.ingsw.model;

public class ConcreteStrategyDiagonal implements Strategy{
    private int row;
    private int column;
    private int cardPoints = 0;
    public int execute(Resource resource,Player player) {
        for (column = player.area.westBound; column <= player.area.eastBound; column++) {
            for (row = player.area.northBound; row <= player.area.southBound; row++) {

                if (player.area.table[row][column].card.resource != null && player.area.table[row][column].card.resource == Resource.MUSHROOM && !player.area.table[row][column].isChecked()) {
                    if (player.area.table[row-1][column+1].card.resource != null && player.area.table[row - 1][column + 1].card.resource == Resource.MUSHROOM && !player.area.table[row][column].isChecked()) {
                        if (player.area.table[row-2][column+2].card.resource != null && player.area.table[row - 2][column + 2].card.resource == Resource.MUSHROOM && !player.area.table[row][column].isChecked()) {
                            player.area.table[row][column].setChecked(true);
                            player.area.table[row - 1][column + 1].setChecked(true);
                            player.area.table[row - 2][column + 2].setChecked(true);
                            cardPoints = cardPoints + 2;
                        }
                    }
                }
                if (player.area.table[row][column].card.resource != null && player.area.table[row][column].card.resource == Resource.WOLF && !player.area.table[row][column].isChecked()) {
                    if (player.area.table[row-1][column+1].card.resource != null && player.area.table[row - 1][column + 1].card.resource == Resource.WOLF && !player.area.table[row][column].isChecked()) {
                        if (player.area.table[row-2][column+2].card.resource != null && player.area.table[row - 2][column + 2].card.resource == Resource.WOLF && !player.area.table[row][column].isChecked()) {
                            player.area.table[row][column].setChecked(true);
                            player.area.table[row - 1][column + 1].setChecked(true);
                            player.area.table[row - 2][column + 2].setChecked(true);
                            cardPoints = cardPoints + 2;

                        }
                    }
                }
                if (player.area.table[row][column].card.resource != null && player.area.table[row][column].card.resource == Resource.LEAF && !player.area.table[row][column].isChecked()) {
                    if (player.area.table[row+1][column+1].card.resource != null && player.area.table[row + 1][column + 1].card.resource == Resource.LEAF && !player.area.table[row][column].isChecked()) {
                        if (player.area.table[row+2][column+2].card.resource != null && player.area.table[row + 2][column + 2].card.resource == Resource.LEAF && !player.area.table[row][column].isChecked()) {
                            player.area.table[row][column].setChecked(true);
                            player.area.table[row + 1][column + 1].setChecked(true);
                            player.area.table[row + 2][column + 2].setChecked(true);
                            cardPoints = cardPoints + 2;

                        }
                    }
                }
                if (player.area.table[row][column].card.resource != null && player.area.table[row][column].card.resource == Resource.BUTTERFLY && !player.area.table[row][column].isChecked()) {
                    if (player.area.table[row+1][column+1].card.resource != null && player.area.table[row + 1][column + 1].card.resource == Resource.BUTTERFLY && !player.area.table[row][column].isChecked()) {
                        if (player.area.table[row+2][column+2].card.resource != null && player.area.table[row + 2][column + 2].card.resource == Resource.BUTTERFLY && !player.area.table[row][column].isChecked()) {
                            player.area.table[row][column].setChecked(true);
                            player.area.table[row + 1][column + 1].setChecked(true);
                            player.area.table[row + 2][column + 2].setChecked(true);
                            cardPoints = cardPoints + 2;

                        }
                    }
                }
            }
        }
        for (column = player.area.westBound; column <= player.area.eastBound; column++) {
            for (row = player.area.northBound; row <= player.area.southBound; row++) {
                if (player.area.table[row][column].isChecked()) {
                    player.area.table[row][column].setChecked(false);
                }
            }
        }
        return cardPoints;
    }
}
