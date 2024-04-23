package it.polimi.ingsw.networking.socket;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualViewSocket {
    public void showUpdate(Integer number);
    public void reportError(String details);
}
