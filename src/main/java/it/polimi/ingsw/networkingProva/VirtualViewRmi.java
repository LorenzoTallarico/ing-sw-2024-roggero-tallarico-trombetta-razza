package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.Action;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewRmi extends Remote {
    void reportError(String details) throws RemoteException;

    void showAction(Action act) throws RemoteException;

    String getNickname() throws RemoteException;
}
