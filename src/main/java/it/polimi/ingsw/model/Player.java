package it.polimi.ingsw.model;

public class Player {
    String name;
    boolean winner;
    int points;
    ArrayList<Card> hand = new ArrayList<Card>;
    Playground area;
    Color color;
    AchievementCard secretAchievement;

    public Player(String name, boolean winner, int points, ArrayList<Card> hand, Color color, AchievementCard secretAchievement) {
        this.name = name;
        this.winner = winner;
        this.points = points;
        this.hand = hand;
        this.color = color;
        this.secretAchievement = secretAchievement;
    }
    // GETTER
    public String getName() {
        return name;
    }

    public boolean isWinner() {
        return winner;
    }

    public int getPoints() {
        return points;
    }

    public ArrayList<Card> getHand() {
        return hand;
    }

    public Playground getArea() {
        return area;
    }

    public Color getColor() {
        return color;
    }

    //SETTER

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public void setSecretAchievement(AchievementCard secretAchievement) {
        this.secretAchievement = secretAchievement;
    }

    //Functions
    public AchievementCard getSecretAchievement() {
        return secretAchievement;
    }

    public Card draw(){

    }

    public void place (){

    }


}
