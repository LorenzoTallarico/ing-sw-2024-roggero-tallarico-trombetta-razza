package it.polimi.ingsw.model;

public class AchievementCard extends Card{
    private AchievementType achievement;
    public AchievementCard(int points, Resource resource, Corner[] frontCorners, Corner[] backCorners,AchievementType achievement){
        this.points = points;
        this.resource = resource;
        for (int i = 0; i < frontCorners.length; i++) {
            this.frontCorners[i] = frontCorners[i];
        }
        for (int i = 0; i < backCorners.length; i++) {
            this.backCorners[i] = backCorners[i];
        }
        this.achievement = achievement;
    }
    public AchievementType getAchievementType(){
        return achievement;
    }
}
