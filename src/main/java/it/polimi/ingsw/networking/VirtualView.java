package it.polimi.ingsw.networking;


import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

import it.polimi.ingsw.networking.action.Action;

/**
 * The VirtualView interface defines the methods that must be implemented
 * by any class that acts as a view in the Model-View-Controller (MVC) pattern
 * over a network. It extends the Remote interface.
 */
public interface VirtualView extends Remote {

    /**
     * Checks if the client is online.
     *
     * @return true if the client is online, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    boolean getOnline() throws RemoteException;

    /**
     * Retrieves the nickname of the client.
     *
     * @return the client's nickname.
     * @throws RemoteException if a remote communication error occurs.
     */
    String getNickname() throws RemoteException;

    /**
     * Checks if the client is responsive.
     *
     * @return true if the client is responsive, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    boolean getPing() throws RemoteException;

    /**
     * Checks if the client is the starter of the game.
     *
     * @return true if the client finished its phase of choosing starter card and achievements, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    boolean getStarter()throws RemoteException;

    /**
     * Retrieves the nickname of the first client.
     *
     * @return the nickname of the first client.
     * @throws RemoteException if a remote communication error occurs.
     */
    String getNicknameFirst() throws RemoteException;

    /**
     * Sets the online status of the client.
     *
     * @param b true to set the client online, false to set the client offline.
     * @throws RemoteException if a remote communication error occurs.
     */
    void setOnline(boolean b) throws RemoteException;

    /**
     * Sets the nickname of the client.
     *
     * @param nick the nickname to set for the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    void setNickname(String nick) throws RemoteException;

    /**
     * Sets the ping status of the client.
     *
     * @param b true to set the client as responsive, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    void setPing(boolean b) throws RemoteException;

    /**
     * Sets the starter status of the client.
     *
     * @param b true if the client finished its phase of choosing starter card and achievements, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    void setStarter(boolean b) throws RemoteException;

    /**
     * Displays an action to the client.
     *
     * @param act the action to display.
     * @throws IOException if an I/O error occurs during communication.
     */
    void showAction(Action act) throws IOException;
}
