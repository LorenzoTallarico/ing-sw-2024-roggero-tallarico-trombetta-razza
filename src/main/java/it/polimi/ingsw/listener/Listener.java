package it.polimi.ingsw.listener;



import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.action.Action;
import it.polimi.ingsw.action.ActionType;
import it.polimi.ingsw.action.PlacedCardAction;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.StarterCard;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.rmi.RemoteException;
import java.util.ArrayList;

public class Listener {

    private ArrayList<VirtualView> clients;

    public Listener(ArrayList<VirtualView> clients) {
        this.clients = clients;
    }

    public void notifyStarterCard(ArrayList<Player> players) throws RemoteException {
        for(Player p : players) {
            StarterCard card = (StarterCard) p.getArea().getSpace(40,40).getCard();
            Action action = new ChooseSideStarterCard()
            for(VirtualView client : clients)
                client.showAction(action);
        }
    }

    public void notifyCardPlacement(String nickname, Card card, int row, int col) throws RemoteException {
        for(VirtualView client : clients) {
            Action action = new PlacedCardAction(nickname, card, row, col);
            client.showAction(action);
        }
    }

    public void notifyHands(ArrayList<Player> players) throws RemoteException {
        for(Player players : p) {
            Action action = new Action(ActionType.PLACEDCARD, card, null, );
            client.showAction(action);
        }
    }

}
