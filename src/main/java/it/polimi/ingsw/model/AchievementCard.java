package it.polimi.ingsw.model;

import java.io.Serializable;

public class AchievementCard extends Card implements Serializable {
    private Strategy strategy;
    private Player player;
    private Item item;
    private String strategyType;

    public AchievementCard(){
        this.strategy = null;
        this.player = null;
        this.item = null;
    }

    /**
     * Constructor of the class, initializes a new achievement card.
     * It initializes the AchievementCard using the given parameters.
     * @param points integer representing the points the card gives if the requirements are fulfilled
     * @param resource The type of resource of the card
     */
    public AchievementCard(int points, Resource resource, String strategyType, Item item) {
        this.points = points;
        this.resource = resource;
        StrategyInstanceCreator sic = new StrategyInstanceCreator(strategyType);
        this.strategy = sic.createInstance(null);
        this.item = item;
        this.strategyType = strategyType;
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
