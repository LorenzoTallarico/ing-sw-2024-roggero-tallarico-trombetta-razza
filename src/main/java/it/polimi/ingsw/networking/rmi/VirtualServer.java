package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.model.*;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {

    boolean connect(VirtualView client) throws RemoteException;

    void sendAction(Action action) throws RemoteException;

    void addPlayer(Player p, VirtualView v) throws RemoteException;


}
