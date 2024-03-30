package rmi;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualView {

    void showUpdate(int state) throws RemoteException;

    void reportError(String details) throws RemoteException;
}
