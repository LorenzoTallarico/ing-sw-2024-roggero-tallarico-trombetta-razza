package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.action.Action;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * The VirtualServer interface defines the methods that must be implemented
 * by any class that acts as a server in the Model-View-Controller (MVC) pattern
 * over a network. It extends the Remote interface.
 */
public interface VirtualServer extends Remote {

    /**
     * Connects a client to the server. Used for RMI.
     *
     * @param client the client to connect to the server.
     * @return true if the connection is successful, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    boolean connect(VirtualView client) throws RemoteException;

    /**
     * Sends an action to the server.
     *
     * @param action the action to be sent to the server.
     * @throws RemoteException if a remote communication error occurs.
     */
    void sendAction(Action action) throws RemoteException;

}
