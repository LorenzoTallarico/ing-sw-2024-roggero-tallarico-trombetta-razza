package it.polimi.ingsw.networking.rmi;


import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import it.polimi.ingsw.networking.action.Action;

public interface VirtualView extends Remote {

    void reportError(String details) throws RemoteException;

    void showAction(Action act) throws IOException;

    String getNickname() throws RemoteException;
    boolean getOnline() throws RemoteException;
    boolean getGui() throws RemoteException;
}
