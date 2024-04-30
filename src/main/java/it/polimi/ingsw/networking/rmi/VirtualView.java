package it.polimi.ingsw.networking.rmi;


import java.rmi.Remote;
import java.rmi.RemoteException;
import it.polimi.ingsw.action.Action;
import it.polimi.ingsw.action.ActionType;
import java.util.ArrayList;

public interface VirtualView extends Remote {

    void reportError(String details) throws RemoteException;

    void showUpdate(Object o) throws RemoteException;

    void showAction(Action act) throws RemoteException;

    String getNickname() throws RemoteException;
}
