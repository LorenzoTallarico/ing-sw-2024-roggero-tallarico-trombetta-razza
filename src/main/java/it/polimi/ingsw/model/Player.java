package it.polimi.ingsw.model;

import it.polimi.ingsw.listener.Listener;

import java.io.Serializable;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;


public class Player implements Serializable {
    private final String name;
    private final boolean gui = false;
    private boolean winner;
    private int points;
    private ArrayList<Card> hand;
    private Playground area;
    private Color color;
    private ArrayList<AchievementCard> secretAchievement;


    public Player(){
        name = "";
        points = 0;
        hand = new ArrayList<>();
        area = new Playground();
        color = Color.NONE;
        secretAchievement = new ArrayList<>();
    }

    /**
     * Constructor of the class, Initializes a new player with a 'name' and a 'color',
     * his status 'winner' to false, the 'area' and the 'points' to 0.
     * @param name String representing the name of the player
     */
    public Player(String name, boolean gui) {
        this.name = name;
        color = Color.NONE;
        winner = false;
        area = new Playground();
        hand = new ArrayList<Card>();
        points = 0;
        secretAchievement = new ArrayList<>();
        //this.gui=gui;
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

    //SETTER


    /**
     * Method that sets the secret achievement of the player
     * @param secretAchievement AchievementCard representing the secret achievement of the player
     */
    public void setSecretAchievement(ArrayList<AchievementCard> secretAchievement) {
        this.secretAchievement = secretAchievement;
    }

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
        //probabilmente è stato sistemato e non serve il try catch
        try{
            this.hand.add(card);
        } catch (NullPointerException e){
            //da gestire meglio
            System.out.println("Cannot add card because Player's hand is null");
        }
    }

    /**
     * Method that places the card in the space indicated, if possible
     * @param card Card that would be placed
     * @param row integer indicating the row of the playground where the card would be placed
     * @param column integer indicating the column of the playground where the card would be placed
     * @return boolean 'true' if the card was placed correctly, 'false' otherwise
     */
    public boolean place(Card card, boolean side, int row, int column) throws RemoteException {
        card.setFront(side);

        boolean check = placeable(card, row, column);
        if(check) {
            //card must be added to the correct space
            int score = area.setSpace(card, row, column);
            hand.remove(card);
            points += score;
 //           try {
            Game.getInstance().getListener().notifyCardPlacement(this.name, this, card, row, column);
 //           } catch (NullPointerException e) {
                //da gestire meglio
 //               System.err.println("Null pointer exception in Player.place() due to notifyCardPlacement() call");
 //           }
            Game.getInstance().getListener().notifyDrawCard(this.name, Game.getInstance().getCommonGold(), !Game.getInstance().getGoldDeck().isEmpty(),
                    Game.getInstance().getCommonResource(), !Game.getInstance().getResourceDeck().isEmpty());
            return true;
        }
        else {
            Game.getInstance().getListener().notifyCardError(this.name);
            card.setFront(!side);
            return false;
        }
    }


    public boolean placeable(Card card, int row, int column) {
        //collection in which we find the possible corners of the card that will cover another card
        Stack<Integer> corn = new Stack<Integer>();
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

        if(card.getClass() == GoldCard.class){
            if(!checkGold((GoldCard) card))
                return false;
        }

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

    //checks if there are enough resources on the playground to place the gold card
    public boolean checkGold(GoldCard card){
        boolean result=true;
        if(card.countResource(Resource.LEAF) > area.countResources(Resource.LEAF)){
            result = false;
            return result;
        }
        if(card.countResource(Resource.WOLF) > area.countResources(Resource.WOLF)){
            result = false;
            return result;
        }
        if(card.countResource(Resource.BUTTERFLY) > area.countResources(Resource.BUTTERFLY)){
            result = false;
            return result;
        }
        if(card.countResource(Resource.MUSHROOM) > area.countResources(Resource.MUSHROOM)){
            result = false;
            return result;
        }

        return result;
    }


}
