package it.polimi.ingsw.model;

import java.io.Serializable;

public class AchievementCard extends Card implements Serializable {
    private final Strategy strategy;
    private Player player;
    private final Item item;
    private String strategyType;

    /**
     * Default constructor for an empty achievement card
     */
    public AchievementCard() {
        this.strategy = null;
        this.player = null;
        this.item = null;
        this.id = "";
    }

    /**
     * Constructor of the class, initializes a new achievement card.
     * It initializes the AchievementCard using the given parameters.
     * @param points integer representing the points the card gives if the requirements are fulfilled
     * @param resource The type of resource of the card, possibly used for the strategy
     * @param strategyType String representing the strategy implemented
     * @param item item possibly used for the strategy
     * @param id String representing the id of the card, used for images
     */
    public AchievementCard(int points, Resource resource, String strategyType, Item item, String id) {
        this.points = points;
        this.resource = resource;
        StrategyInstanceCreator sic = new StrategyInstanceCreator(strategyType);
        this.strategy = sic.createInstance(null);
        this.item = item;
        this.strategyType = strategyType;
        this.id = id;
    }

    /**
     * Method that returns the ID of the current side of the card
     * used to identify the corresponding image
     * @return the string representing the current card&side ID
     */
    public String getSideID() {
        return getSideID(this.front);
    }

    /**
     * Overloading of the previous method, it returns the ID of a
     * specific side of the card, used to identify the corresponding image
     * @param front boolean representing the side, true = front, false = back
     * @return the string representing the wanted card&side ID
     */
    public String getSideID(boolean front) {
        if(front)
            return id;
        else
            return "087";
    }

    public boolean equals(AchievementCard ach) {
        if(!this.strategyType.equals(ach.getStrategyType())){
            return false;
        }
        if(this.getStrategyType().equals("ConcreteStrategyItem")) {
            if (this.item != ach.getItem()){
                return this.item == ach.getItem();
            }
        }
        if(this.getStrategyType().equals("ConcreteStrategyMixed")) {
            return true;
        }
        if(this.strategyType.equals(ach.getStrategyType())) {
            return this.resource == ach.getResource();
        }
        return true;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Item getItem(){
        return item;
    }

    public String getStrategyType() {
        return strategyType;
    }

    public int calculatePoints() {
        return strategy.execute(resource, player, item);
    }

    public int calculatePoints(Player p) {
        return strategy.execute(resource, p, item);
    }
}
