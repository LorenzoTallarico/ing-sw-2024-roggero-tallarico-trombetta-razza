package it.polimi.ingsw.listener;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.util.Print;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;

public class Listener {

    private final ArrayList<VirtualView> clients;

    public Listener(ArrayList<VirtualView> clients) {
        this.clients = clients;
    }



    public void notifyReconnection(String nickname, ArrayList<Player> players,  ArrayList<GoldCard> commonGold, ArrayList<ResourceCard> commonResource,ArrayList<AchievementCard> commonAchievement, Resource goldDeck, Resource resourceDeck) throws RemoteException {
        System.out.println("> All clients notified by the reconnection of player " + nickname +".");
        Action action = new ReconnectionSuccessAction(nickname, players, commonGold, goldDeck, commonResource, resourceDeck, commonAchievement);
        for(VirtualView client : clients){
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }



    }

    public void notifyStarterCard(ArrayList<Player> players,  ArrayList<GoldCard> commonGold, ArrayList<ResourceCard> commonResource, Resource goldDeck, Resource resourceDeck) throws RemoteException {
        System.out.println("> All clients notified by starter card listener.");
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

    public void notifyCardPlacement(String nickname, Player p, Card card, int row, int col, int score) throws RemoteException {
        System.out.println("> All clients notified by card placement listener. <- " + nickname);
        Action action = new PlacedCardAction(nickname, p, card, row, col, score);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    public void notifyCardError(String nickname) throws RemoteException {
        System.out.println("> All clients notified by placement error listener. <- " + nickname);
        Action action = new PlacedErrorAction(nickname);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    public void notifyDrawCard(String nickname, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) throws RemoteException{
        System.out.println("> All clients notified by ask to draw listener. <- " + nickname);
        Action action = new AskingDrawAction(nickname, commonGold, goldDeck, commonResource, resourceDeck);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }


    public void notifyDrawCompleted(Player player, Card card) throws RemoteException {
        System.out.println("> All clients notified by completed draw listener. <- " + player.getName());
        Action action = new CardDrawnAction(player.getName(), player, card);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }




    public void notifyAchievementChoice(String recipient, ArrayList<AchievementCard> achievements,  ArrayList<AchievementCard> commonGoals) throws RemoteException {
        System.out.println("> All clients notified by achievement choice listener. <- " + recipient);
        Action action = new ChooseableAchievementsAction(recipient, achievements, commonGoals);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    public void notifyToPlace(Player player) throws RemoteException {
        System.out.println("> All clients notified by ask to place listener. <- " + player.getName());
        Action action = new AskingPlaceAction(player);
        for(VirtualView client : clients) {
            try {
                client.showAction(action);
            } catch(IOException e) {
                System.out.println(Print.ANSI_RED + "Exception " + e.getClass() + " sending showAction to " + client.getNickname() + "." + Print.ANSI_RESET);
            }
        }
    }

    public void notifyWinners(ArrayList<Player> players) throws RemoteException {
        System.out.println("> All clients notified by end game winners listener.");
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
