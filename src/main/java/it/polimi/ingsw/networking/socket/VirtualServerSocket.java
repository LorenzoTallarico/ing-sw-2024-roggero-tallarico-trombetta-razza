package it.polimi.ingsw.networking.socket;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServerSocket {

    public void connect(VirtualViewSocket client);

    public void add(Integer number);

    public void reset();


}
