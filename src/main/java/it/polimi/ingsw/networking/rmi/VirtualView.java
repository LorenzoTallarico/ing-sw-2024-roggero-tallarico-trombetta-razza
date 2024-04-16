package it.polimi.ingsw.networking.rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;

public interface VirtualView extends Remote {

    void showUpdateNumber(Integer number) throws RemoteException;

    void showUpdateNames(ArrayList<String> names) throws RemoteException;

    String getNickname() throws RemoteException;

    void reportError(String details) throws RemoteException;

    void showUpdate(Object o) throws RemoteException;
}
