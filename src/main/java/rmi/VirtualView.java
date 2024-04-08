package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView extends Remote {

    void showUpdate(Integer number) throws RemoteException;

    void reportError(String details) throws RemoteException;
}
