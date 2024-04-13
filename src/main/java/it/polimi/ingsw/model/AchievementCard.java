package it.polimi.ingsw.model;

public class AchievementCard extends Card{
    private Strategy strategy;
    private Player player;
    private Item item;

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
     * @param frontCorners Corners of the front side of the card
     * @param backCorners Corners of the back side of the card
     */
    public AchievementCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners, Strategy strategy,Item item){
        this.points = points;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
        this.strategy = strategy;
        this.item = item;
    }

    /**
     * method that given a player, sets it as the owner of the achievement card
     * @param player indicates the player
     */
    public void setPlayer(Player player) {
        this.player = player;
    }

    public Strategy getStrategy() {
        return strategy;
    }

    public Item getItem(){
        return item;
    }
    /**
     * Method that returns the card's related points
     * @return number of points
     */
    @Override
    public int getPoints(){
        return points+strategy.execute(resource,player,item);
    }
}
