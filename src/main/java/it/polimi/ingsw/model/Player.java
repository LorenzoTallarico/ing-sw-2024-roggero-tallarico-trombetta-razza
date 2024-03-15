package it.polimi.ingsw.model;
import java.util.ArrayList;

public class Player {
    private String name;
    private boolean winner;
    private int points;
    private ArrayList<Card> hand;
    private Playground area;
    private Color color;
    private AchievementCard secretAchievement;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        winner = false;
        area = new Playground();
        points = 0;
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

    public AchievementCard getSecretAchievement() {
        return secretAchievement;
    }
    //SETTER

    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    public void addPoints(int points) {
        this.points += points;
    }

    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public void setSecretAchievement(AchievementCard secretAchievement) {
        this.secretAchievement = secretAchievement;
    }






}
