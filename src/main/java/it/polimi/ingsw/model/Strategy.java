package it.polimi.ingsw.model;
/**
 * Represents a strategy that can be executed with a given resource, player, and item.
 */
public interface Strategy {
    /**
     * Executes the strategy using the provided resource, player, and item.
     *
     * @param r the resource involved in the strategy
     * @param p the player involved in the strategy
     * @param i the item involved in the strategy
     * @return an integer result based on the execution of the strategy
     */
    public int execute(Resource r,Player p,Item i);

}
