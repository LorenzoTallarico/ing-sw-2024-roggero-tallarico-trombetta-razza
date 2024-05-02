package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.action.Action;
import it.polimi.ingsw.model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface VirtualServer extends Remote {

    boolean connect(VirtualView client) throws RemoteException;

    void sendAction(Action action) throws RemoteException;

    void addPlayer(Player p, VirtualView v) throws RemoteException;

    ArrayList<Player> getPlayers() throws RemoteException;

    void placeCard(Card card, int row, int column) throws RemoteException;

    void drawCard(int index) throws RemoteException;

    void selectAchievementCard(int position) throws RemoteException;

}
