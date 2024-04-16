package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface VirtualView extends Remote {

    void reportError(String details) throws RemoteException;

    void showUpdate(Object o) throws RemoteException;
}
