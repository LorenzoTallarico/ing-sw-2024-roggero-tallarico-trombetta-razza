package it.polimi.ingsw.networking.rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;
import it.polimi.ingsw.networking.action.Action;

public interface VirtualView extends Remote {

    void reportError(String details) throws RemoteException;

    void showAction(Action act) throws RemoteException;

    String getNickname() throws RemoteException;
}
