package it.polimi.ingsw.listener;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.VirtualView;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.util.Print;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * The Listener class is responsible for notifying clients about various events occurring in the game model.
 */
public class Listener {

    private final ArrayList<VirtualView> clients;

    /**
     * Constructs a new Listener with the specified list of clients.
     *
     * @param clients The list of clients to be notified.
     */
    public Listener(ArrayList<VirtualView> clients) {
        this.clients = clients;
    }


    /**
     * Notifies clients about a player reconnection.
     *
     * @param nickname The nickname of the reconnected player.
     * @param players The list of players in the game.
     * @param commonGold The common gold cards.
     * @param commonResource The common resource cards.
     * @param commonAchievement The common achievement cards.
     * @param goldDeck The gold resource deck.
     * @param resourceDeck The resource deck.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyReconnection(String nickname, ArrayList<Player> players,  ArrayList<GoldCard> commonGold, ArrayList<ResourceCard> commonResource,ArrayList<AchievementCard> commonAchievement, Resource goldDeck, Resource resourceDeck) throws RemoteException {
        Action action = new ReconnectionSuccessAction(nickname, players, commonGold, goldDeck, commonResource, resourceDeck, commonAchievement);
        for(VirtualView client : clients){
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

/**
 * Notifies clients about the starter card choice.
 *
 * @param players The list of players in the game.
 * @param commonGold The common gold cards.
 * @param commonResource The common resource cards.
 * @param goldDeck The gold resource deck.
 * @param resourceDeck The resource deck.
 * @throws RemoteException If a remote communication error occurs.
 */
    public void notifyStarterCard(ArrayList<Player> players,  ArrayList<GoldCard> commonGold, ArrayList<ResourceCard> commonResource, Resource goldDeck, Resource resourceDeck) throws RemoteException {
        for(Player p : players) {
            StarterCard card = (StarterCard) p.getArea().getSpace(40,40).getCard();
            Action action = new ChooseSideStarterCardAction(p.getName(), card, p, commonGold, goldDeck, commonResource, resourceDeck);
            for(VirtualView client : clients) {
                try {
                    client.showAction(action);
                } catch(IOException e) {
                    System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
                }
            }
        }
    }

    /**
     * Notifies clients about a card placement.
     *
     * @param nickname The nickname of the player placing the card.
     * @param p The player placing the card.
     * @param card The card being placed.
     * @param row The row on the board where the card is placed.
     * @param col The column on the board where the card is placed.
     * @param score The score associated with the card placement.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyCardPlacement(String nickname, Player p, Card card, int row, int col, int score) throws RemoteException {
        Action action = new PlacedCardAction(nickname, p, card, row, col, score);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    /**
     * Notifies clients about a card placement error.
     *
     * @param nickname The nickname of the player who encountered the error.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyCardError(String nickname) throws RemoteException {
        Action action = new PlacedErrorAction(nickname);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    /**
     * Notifies clients that a player is drawing a card.
     *
     * @param nickname The nickname of the player drawing the card.
     * @param commonGold The common gold cards.
     * @param goldDeck The gold resource deck.
     * @param commonResource The common resource cards.
     * @param resourceDeck The resource deck.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyDrawCard(String nickname, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) throws RemoteException{
        Action action = new AskingDrawAction(nickname, commonGold, goldDeck, commonResource, resourceDeck);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    /**
     * Notifies clients that a player has completed drawing a card.
     *
     * @param player The player who drew the card.
     * @param card The card drawn.
     * @param commonGold The common gold cards.
     * @param goldDeck The gold resource deck.
     * @param commonResource The common resource cards.
     * @param resourceDeck The resource deck.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyDrawCompleted(Player player, Card card,  ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) throws RemoteException {
        Action action = new CardDrawnAction(player.getName(), player, card, commonGold, goldDeck, commonResource, resourceDeck);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }



    /**
     * Notifies clients about a player's achievement choice.
     *
     * @param recipient The recipient of the achievement choice notification.
     * @param achievements The list of achievements to choose from.
     * @param commonGoals The common goals for the game.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyAchievementChoice(String recipient, ArrayList<AchievementCard> achievements,  ArrayList<AchievementCard> commonGoals) throws RemoteException {
        Action action = new ChooseableAchievementsAction(recipient, achievements, commonGoals);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    /**
     * Notifies clients that a player is asked to place a card.
     *
     * @param player The player who is asked to place a card.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyToPlace(Player player) throws RemoteException {
        Action action = new AskingPlaceAction(player);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    /**
     * Notifies clients about the end game winner.
     *
     * @param players The list of players.
     * @throws RemoteException If a remote communication error occurs.
     */
    public void notifyWinners(ArrayList<Player> players) throws RemoteException {
        Action action = new WinnersAction(players);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

}
