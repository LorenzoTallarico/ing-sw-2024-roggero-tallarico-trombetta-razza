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

    /**
     * Constructor of the class, Initializes a new player with a 'name' and a 'color',
     * his status 'winner' to false, the 'area' and the 'points' to 0.
     * @param name String representing the name of the player
     * @param color Color of the game marker of the player
     */
    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        winner = false;
        area = new Playground();
        points = 0;
    }

    // GETTER
    /**
     * Method that returns the name of the player
     * @return a String representing the name of the player
     */
    public String getName() {
        return name;
    }

    /**
     * Method that tells if a player is a winner
     * @return the boolean 'winner', it's true if the player is a winner, otherwise false
     */
    public boolean isWinner() {
        return winner;
    }

    /**
     * Method that returns the points of the player at this moment
     * @return an integer representing the amount of points scored by the player
     */
    public int getPoints() {
        return points;
    }

    /**
     * Method that returns the cards in the player's hand
     * @return Arraylist of the cards in the player's hand
     */
    public ArrayList<Card> getHand() {
        return hand;
    }

    /**
     * Method that returns the playground of the player
     * @return Playground of the player
     */
    public Playground getArea() {
        return area;
    }

    /**
     * Method that returns the color of the player's game marker
     * @return Color of the marker
     */
    public Color getColor() {
        return color;
    }

    /**
     * Method that returns the secret achievement of the player
     * @return AchievementCard representing the secret achievement the player chose
     */
    public AchievementCard getSecretAchievement() {
        return secretAchievement;
    }
    //SETTER

    /**
     * Method that sets the player as a winner
     * @param winner boolean, true if the player is a winner, false otherwise
     */
    public void setWinner(boolean winner) {
        this.winner = winner;
    }

    /**
     * Method that summarize the old amount of points the player scored with the points he scored in his last turn
     * @param points integer representing the amount of points the player scored in his last turn
     */
    public void addPoints(int points) {
        this.points += points;
    }

    /**
     * Method that sets the cards in the players hand
     * @param hand Arraylist of cards that are in the player's hand
     */
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }

    /**
     * Method that sets the secret achievement of the player
     * @param secretAchievement AchievementCard representing the secret achievement of the player
     */
    public void setSecretAchievement(AchievementCard secretAchievement) {
        this.secretAchievement = secretAchievement;
    }






}
