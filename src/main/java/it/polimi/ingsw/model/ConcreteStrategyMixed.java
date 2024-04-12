package it.polimi.ingsw.model;

public class ConcreteStrategyMixed implements Strategy{

    public ConcreteStrategyMixed() {    }

    /**
     * @param resource it's useless
     * @param player the player who owns the card
     * @param item it's useless
     * @return number of points added to the player due to the achievement card
     */
    public int execute(Resource resource,Player player, Item item){
        int jarCounter = player.getArea().countItems(Item.JAR);
        int scrollCounter = player.getArea().countItems(Item.SCROLL);
        int plumeCounter = player.getArea().countItems(Item.PLUME);
        return Math.min(jarCounter,Math.min(scrollCounter,plumeCounter))*3; // ritorna il piu piccolo tra i tre contatori
    }
}
