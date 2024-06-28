package it.polimi.ingsw.model;

import java.io.Serializable;

/**
 * Card representing an achievement to fulfill in game
 */
public class AchievementCard extends Card implements Serializable {

    /**
     * The strategy associated with this achievement card.
     */
    private final Strategy strategy;

    /**
     * The player associated with this achievement card.
     */
    private Player player;

    /**
     * The item associated with this achievement card.
     */
    private final Item item;

    /**
     * The type of strategy as a string.
     */
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
     * @return the string representing the current card and side ID
     */
    public String getSideID() {
        return getSideID(this.front);
    }

    /**
     * Overloading of the previous method, it returns the ID of a
     * specific side of the card, used to identify the corresponding image
     * @param front boolean representing the side, true = front, false = back
     * @return the string representing the wanted card and side ID
     */
    public String getSideID(boolean front) {
        if(front)
            return id;
        else
            return "087";
    }

    /**
     * Checks which achievement type the card corresponds to.
     *
     * @param ach The achievement card to compare with.
     * @return True if the achievement's card corresponds to one of the achievements, false otherwise.
     */
    public boolean equals(AchievementCard ach) {
        if(!this.strategyType.equals(ach.getStrategyType())){
            return false;
        } else if(this.getStrategyType().equals("ConcreteStrategyItem")) {
            return this.item == ach.getItem();
        } else if(this.getStrategyType().equals("ConcreteStrategyMixed")) {
            return true;
        }
        return this.resource == ach.getResource();
    }

    /**
     * Sets the player for this achievement card.
     *
     * @param player The player to set.
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    /**
     * Gets the strategy associated with this achievement card.
     *
     * @return The strategy.
     */
    public Strategy getStrategy() {
        return strategy;
    }

    /**
     * Gets the item associated with this achievement card.
     *
     * @return The item.
     */
    public Item getItem(){
        return item;
    }

    /**
     * Gets the strategy type as a string.
     *
     * @return The strategy type.
     */
    public String getStrategyType() {
        return strategyType;
    }

    /**
     * Calculates the points for this achievement card based on its strategy.
     *
     * @return The points calculated.
     */
    public int calculatePoints() {
        return strategy.execute(resource, player, item);
    }

    /**
     * Calculates the points for this achievement card for a specified player.
     *
     * @param p The player to calculate points for.
     * @return The points calculated.
     */
    public int calculatePoints(Player p) {
        return strategy.execute(resource, p, item);
    }
}
