package it.polimi.ingsw.listener;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Listener {

    private final ArrayList<VirtualView> clients;

    public Listener(ArrayList<VirtualView> clients) {
        this.clients = clients;
    }

    public void notifyStarterCard(ArrayList<Player> players) throws RemoteException {
        System.out.println("> All clients notified by starter card listener.");
        for(Player p : players) {
            StarterCard card = (StarterCard) p.getArea().getSpace(40,40).getCard();
            Action action = new ChooseSideStarterCardAction(p.getName(), card, p);
            for(VirtualView client : clients) {
                client.showAction(action);
            }
        }
    }

    public void notifyCardPlacement(String nickname, Player p, Card card, int row, int col, int score) throws RemoteException {
        System.out.println("> All clients notified by card placement listener. <- " + nickname);
        Action action = new PlacedCardAction(nickname, p, card, row, col, score);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }

    public void notifyCardError(String nickname) throws RemoteException {
        System.out.println("> All clients notified by placement error listener. <- " + nickname);
        Action action = new PlacedErrorAction(nickname);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }

    public void notifyDrawCard(String nickname, ArrayList<GoldCard> commonGold, Resource goldDeck, ArrayList<ResourceCard> commonResource, Resource resourceDeck) throws RemoteException{
        System.out.println("> All clients notified by ask to draw listener. <- " + nickname);
        Action action = new AskingDrawAction(nickname, commonGold, goldDeck, commonResource, resourceDeck);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }


    public void notifyDrawCompleted(Player player, Card card) throws RemoteException {
        System.out.println("> All clients notified by completed draw listener. <- " + player.getName());
        Action action = new CardDrawnAction(player.getName(), player, card);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }



    public void notifyHands(ArrayList<Player> players) throws RemoteException {
        for(Player p : players) {
            Action action = new HandAction(p.getName(), p.getHand());
            for(VirtualView client : clients) {
                client.showAction(action);
            }
        }
    }

    public void notifyAchievementChoice(String recipient, ArrayList<AchievementCard> achievements,  ArrayList<AchievementCard> commonGoals) throws RemoteException {
        System.out.println("> All clients notified by achievement choice listener. <- " + recipient);
        Action action = new ChooseableAchievementsAction(recipient, achievements, commonGoals);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }

    public void notifyToPlace(Player player) throws RemoteException {
        System.out.println("> All clients notified by ask to place listener. <- " + player.getName());
        Action action = new AskingPlaceAction(player);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }

    public void notifyWinners(ArrayList<Player> players) throws RemoteException {
        System.out.println("> All clients notified by end game winners listener.");
        Action action = new WinnersAction(players);
        for(VirtualView client : clients) {
            client.showAction(action);
        }
    }

}
