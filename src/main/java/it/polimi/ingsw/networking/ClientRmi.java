
package it.polimi.ingsw.networking;
import it.polimi.ingsw.networking.action.Action;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

/**
 * The ClientRmi class implements the VirtualView interface and handles RMI communication for the client.
 */
public class ClientRmi extends UnicastRemoteObject implements VirtualView {

    /**
     * The VirtualView instance representing the client.
     */
    private VirtualView client = null;


    /**
     * The nickname of the client.
     */
    private String nickname = null;

    /**
     * The ping status of the client.
     */
    private boolean ping = true;

    /**
     * The online status of the client.
     */
    private boolean online;

    /**
     * The starter status of the client.
     */
    private boolean starter = false;

    /**
     * Constructs a ClientRmi instance.
     *
     * @param client the VirtualView instance representing the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public ClientRmi(VirtualView client) throws RemoteException{
        this.client = client;
    }

    /**
     * Gets the nickname of the client.
     *
     * @return the nickname of the client.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public String getNickname() throws RemoteException {
        return nickname;
    }

    /**
     * Gets the nickname of the first client.
     *
     * @return the nickname of the first client.
     * @throws RemoteException if a remote communication error occurs.
     */
    public String getNicknameFirst() throws RemoteException {
        return client.getNickname();
    }

    /**
     * Gets the starter status of the client.
     *
     * @return true if the client finished its phase of choosing starter card and achievements, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean getStarter() throws RemoteException {
        return starter;
    }

    /**
     * Gets the online status of the client.
     *
     * @return true if the client is online, false otherwise.
     */
    @Override
    public boolean getOnline() { //da aggiungere
        return online;
    }


    /**
     * Gets the ping status of the client.
     *
     * @return true if the client is responding to pings, false otherwise.
     */
    @Override
    public boolean getPing() {
        return ping;
    }

    /**
     * Sets the starter status of the client.
     *
     * @param starter true if the client finished its phase of choosing starter card and achievements, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public void setStarter(boolean starter) throws RemoteException {
        this.starter = starter;
    }

    /**
     * Sets the nickname of the client.
     *
     * @param nick the nickname of the client.
     */
    @Override
    public void setNickname(String nick) {
        nickname = nick;
    }


    /**
     * Sets the online status of the client.
     *
     * @param b true if the client is online, false otherwise.
     */
    @Override
    public void setOnline(boolean b) {
        online = b;
    }

    /**
     * Sets the ping status of the client.
     *
     * @param b true if the client is responding to pings, false otherwise.
     */
    @Override
    public void setPing(boolean b) {
        ping = b;
    }

    /**
     * Shows an action to the client if the client is online.
     *
     * @param act the action to be shown to the client.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void showAction(Action act) throws IOException {
        if (online) {
            try {
                client.showAction(act);
            } catch (RemoteException e) {
                // Client disconnection handling
                System.err.println("Exception in showAction from ClientRmi (ping set false)");
                ping = false;
            }
        }
    }


}
