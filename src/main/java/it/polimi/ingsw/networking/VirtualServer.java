package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.action.Action;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {

    boolean connect(VirtualView client) throws RemoteException;

    void sendAction(Action action) throws RemoteException;

}
