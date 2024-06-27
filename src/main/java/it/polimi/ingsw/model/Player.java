package it.polimi.ingsw.model;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * User playing a game
 */
public class Player implements Serializable {

    /**
     * Name of the player
     */
    private final String name;

    /**
     * Indicates if the player is a winner or not, used at the end of the game
     */
    private boolean winner;

    /**
     * Current amount of points the player has
     */
    private int points;

    /**
     * ArrayList of cards the player has in their hand (less or equal than 3)
     */
    private ArrayList<Card> hand;

    /**
     * Playground where the player place their own cards
     */
    private Playground area;

    /**
     * Color of the pawn of the player
     */
    private Color color;

    /**
     * Indicates if a player is currently online or not,
     * used to edit their values if they disconnect in specific
     * phases of the gameplay
     */
    private boolean online;

    /**
     * Secret achievements of the player, initially two,
     * decreased to 1 after the choice
     */
    private ArrayList<AchievementCard> secretAchievement;

    /**
     * Last card placed by the player, used to reset their last valid values
     * when they disconnect before drawing a card
     */
    private Card lastCardPlaced;

    /**
     * Last points the player obtained placing a card, used to reset
     * their last valid values when they disconnect before drawing a card
     */
    private int pointsFromLastCard;

    /**
     * Default constructor for an empty dummy player
     */
    public Player() {
        name = "";
        points = 0;
        hand = new ArrayList<>();
        area = new Playground();
        color = Color.NONE;
        online = true;
        secretAchievement = new ArrayList<>();
        lastCardPlaced = null;
        pointsFromLastCard = 0;
    }

    /**
     * Constructor of the class, Initializes a new player with a 'name' and a 'color',
     * his status 'winner' to false, the 'area' and the 'points' to 0.
     * @param name String representing the name of the player
     */
    public Player(String name) {
        this.name = name;
        color = Color.NONE;
        online = true;
        winner = false;
        area = new Playground();
        hand = new ArrayList<>();
        points = 0;
        secretAchievement = new ArrayList<>();
        lastCardPlaced = null;
        pointsFromLastCard = 0;
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
    public ArrayList<AchievementCard> getSecretAchievement() {
        return secretAchievement;
    }

    /**
     * Method that returns a boolean that indicates if a player is online or not
     * @return online or offline player state
     */
    public boolean isOnline() {
        return online;
    }


    //SETTER

    /**
     * Flags the player as online or offline
     * @param b boolean true for online, false for offline
     */
    public void setOnline(boolean b){
        this.online = b;
    }

    /**
     * Associates a new playground to the player
     * @param area the new playground
     */
    public void setArea(Playground area) {
        this.area = area;
    }

    /**
     * Method that sets the secret achievement of the player
     * @param secretAchievement AchievementCard representing the secret achievement of the player
     */
    public void setSecretAchievement(ArrayList<AchievementCard> secretAchievement) {
        this.secretAchievement = secretAchievement;
    }

    /**
     * Sets a reference to the latest card placed by the player
     * @param card Latest card placed by the player
     */
    public void setLastCardPlaced(Card card){
        this.lastCardPlaced = card;
    }

    /**
     * Sets the amount of points obtained by the latest card placed by the player
     * @param pointsFromLastCard Last points obtained
     */
    public void setPointsFromLastCard(int pointsFromLastCard){
        this.pointsFromLastCard = pointsFromLastCard;
    }

    /**
     * Sets the color of the pawn of the player
     * @param color Pawn's color: red, yellow, green, blue or black
     */
    public void setColor(Color color) {
        this.color = color;
    }

    /**
     * Method that sets the cards in the players hand
     * @param hand Arraylist of cards that are in the player's hand
     */
    public void setHand(ArrayList<Card> hand) {
        this.hand = hand;
    }


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
     * Method that adds a card to the player hand
     * @param card Card added to the player hand
     */
    public void addCard(Card card) {
        this.hand.add(card);
    }

    //METHODS

    /**
     * Resets the playground of the player and their points if they disconnect
     * right after placing a card without drawing any other
     */
    public void disconnection() {
        this.online = false;
        if(lastCardPlaced != null){
            boolean find = false;
            for(int i = area.getWestBound(); i <= area.getEastBound() && !find; i++) {
                for(int j = area.getNorthBound(); j <= area.getSouthBound() && !find; j++) {
                    if(area.getSpace(j, i).getCard() != null && area.getSpace(j, i).getCard().equals(lastCardPlaced)) {
                        find = true;
                        area.setPlaygroundBeforePlace(j, i, lastCardPlaced);
                        hand.add(lastCardPlaced);
                        points -= pointsFromLastCard;
                        lastCardPlaced = null;
                        pointsFromLastCard = 0;
                    }
                }
            }
        }
    }

    /**
     * Method that places the card in the space indicated, if possible
     * @param cardIndex inter representing the position in the hand of the card to place
     * @param row integer indicating the row of the playground where the card would be placed
     * @param column integer indicating the column of the playground where the card would be placed
     * @return boolean 'true' if the card was placed correctly, 'false' otherwise
     */
    public boolean place(int cardIndex, boolean side, int row, int column) throws RemoteException {
        Card card = hand.get(cardIndex);
        card.setFront(side);
        boolean check = placeable(card, row, column);
        if(check) {
            //card must be added to the correct space
            hand.remove(cardIndex);
            int score = area.setSpace(card, row, column);
            points += score;
            setLastCardPlaced(card); //sets the card placed for client disconnections
            setPointsFromLastCard(score);  //sets the points obtained from the last card placed for client disconnections
            if(points >= 20)
                Game.getInstance().setGameState(GameState.LASTROUND);
            Game.getInstance().getListener().notifyCardPlacement(this.name, this, card, row, column, score);
            Game.getInstance().getListener().notifyDrawCard(this.name, Game.getInstance().getCommonGold(), Game.getInstance().getGoldDeck().get(0).getResource(),
                    Game.getInstance().getCommonResource(), Game.getInstance().getResourceDeck().get(0).getResource());
            return true;
        } else {
            Game.getInstance().getListener().notifyCardError(this.name);
            card.setFront(!side);
            return false;
        }
    }

    /**
     * Checks if a card can be placed in a specific position
     * @param card the card to check
     * @param row y of the position
     * @param column x of the position
     * @return true if it's possible to place the card, false otherwise
     */
    public boolean placeable(Card card, int row, int column) {
        //collection in which we find the possible corners of the card that will cover another card
        Stack<Integer> corn = new Stack<>();
        //Collection that allows only unique elements (helps checking that the corner's number is always different for the same card)
        HashSet<Integer> counter = new HashSet<Integer>();
        Corner[] corners;
        boolean stop = false;

        //NB: Starter card must be placed in 40:40 position
        if (card.getClass() == StarterCard.class) {
            if (area.getSpace(40, 40).isFree() && row == 40 && column == 40)
                return true;
            else
                return false;
        }

        //if space is out of bound (+1) card cannot be placed
        //if space is not free card cannot be placed
        //if space is dead card cannot be placed
        if ((area.getNorthBound() - 1 > row) ||
                (area.getSouthBound() + 1 < row) ||
                (area.getEastBound() + 1 < column) ||
                (area.getWestBound() - 1 > column) ||
                (area.getSpace(row, column).isDead()) ||
                (!area.getSpace(row, column).isFree()))
            return false;

        if(card.getClass() == GoldCard.class && card.isFront())
            if(!checkGold((GoldCard) card))
                return false;


        //within bounds, space is free and not dead (down here is not necessary to check also if space is dead, might be removed)
        //topRight = 0
        if (!area.getSpace(row - 1, column + 1).isFree() && !area.getSpace(row - 1, column + 1).isDead()) {
            corn.push(0);
        }
        //bottomRight = 1
        if (!area.getSpace(row + 1, column + 1).isFree() && !area.getSpace(row + 1, column + 1).isDead()) {
            corn.push(1);
        }

        //bottomLeft = 2
        if (!area.getSpace(row + 1, column - 1).isFree() && !area.getSpace(row + 1, column - 1).isDead()) {
            corn.push(2);
        }

        //topLeft = 3
        if (!area.getSpace(row - 1, column - 1).isFree() && !area.getSpace(row - 1, column - 1).isDead()) {
            corn.push(3);
        }

        //Opposite corners:  0 <--> 2    ||    1 <---> 3
        //Corner of the card is still visibile, the adjacent card's corner must be covered

        if (!corn.isEmpty() && corn.size() <= 5) {
            //the card will cover at least 1 corner

            //this helps checking that the number of corners accepted equals the number of corners that need to be checked
            int checker = corn.size();

            for (int i = 0; i < checker; i++) {
                int c1 = corn.pop();
                switch (c1) {
                    case 0:
                        //top right corner
                        if (area.getSpace(row - 1, column + 1).getCard().isFront()) {
                            //gets the card next to the space, checks if the corner is dead
                            //Card is on the front side
                            corners = area.getSpace(row - 1, column + 1).getCard().getFrontCorners();
                            if (!corners[2].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(0);
                            }
                        } else {
                            //Card is on the back side
                            corners = area.getSpace(row - 1, column + 1).getCard().getBackCorners();
                            if (!corners[2].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(0);
                            }
                        }
                        break;

                    case 1:
                        //bottom right corner
                        if (area.getSpace(row + 1, column + 1).getCard().isFront()) {
                            //gets the card next to the space, checks if the corner is dead
                            //Card is on the front side
                            corners = area.getSpace(row + 1, column + 1).getCard().getFrontCorners();
                            if (!corners[3].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(1);
                            }
                        } else {
                            //Card is on the back side
                            corners = area.getSpace(row + 1, column + 1).getCard().getBackCorners();
                            if (!corners[3].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(1);
                            }
                        }
                        break;

                    case 2:
                        //bottom left corner
                        if (area.getSpace(row + 1, column - 1).getCard().isFront()) {
                            //gets the card next to the space, checks if the corner is dead
                            //Card is on the front side
                            corners = area.getSpace(row + 1, column - 1).getCard().getFrontCorners();
                            if (!corners[0].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(2);
                            }
                        } else {
                            //Card is on the back side
                            corners = area.getSpace(row + 1, column - 1).getCard().getBackCorners();
                            if (!corners[0].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(2);
                            }
                        }
                        break;

                    case 3:
                        //top left corner
                        if (area.getSpace(row - 1, column - 1).getCard().isFront()) {
                            //gets the card next to the space, checks if the corner is dead
                            //Card is on the front side
                            corners = area.getSpace(row - 1, column - 1).getCard().getFrontCorners();
                            if (!corners[1].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(3);
                            }
                        } else {
                            //Card is on the back side
                            corners = area.getSpace(row - 1, column - 1).getCard().getBackCorners();
                            if (!corners[1].getType().equals(CornerType.DEAD)) {
                                //add element to HashSet
                                counter.add(3);
                            }
                        }
                        break;
                    default:
                        break;
                }

            }

            //if correct, every corner from the adjacent cards is eligible to be covered (not DEAD)
            if(checker == counter.size()) {
                stop = true;
            }
        }

        return stop;
    }

    /**
     * Checks if the requirements to place a gold card are fulfilled
     * @param card the gold card to check
     * @return true if it's possible to place the gold card front-side, false otherwise
     */
    public boolean checkGold(GoldCard card) {
        boolean result = true;
        if(card.countResource(Resource.LEAF) > area.countResources(Resource.LEAF) ||
                card.countResource(Resource.WOLF) > area.countResources(Resource.WOLF) ||
                card.countResource(Resource.BUTTERFLY) > area.countResources(Resource.BUTTERFLY) ||
                card.countResource(Resource.MUSHROOM) > area.countResources(Resource.MUSHROOM))
            result = false;
        return result;
    }

}
