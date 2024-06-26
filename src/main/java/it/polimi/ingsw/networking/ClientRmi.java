
package it.polimi.ingsw.networking;
import it.polimi.ingsw.networking.action.Action;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRmi extends UnicastRemoteObject implements VirtualView {
    private VirtualView client = null;
    private String nickname = null;
    private boolean ping = true;
    private boolean online;
    private boolean starter = false;

    public ClientRmi(VirtualView client) throws RemoteException{
        this.client = client;
    }

    @Override
    public String getNickname() throws RemoteException {
        return nickname;
    }
    public String getNicknameFirst() throws RemoteException {
        return client.getNickname();
    }

    @Override
    public boolean getStarter() throws RemoteException {
        return starter;
    }

    @Override
    public boolean getOnline() { //da aggiungere
        return online;
    }

    @Override
    public boolean getPing() {
        return ping;
    }

    @Override
    public void setStarter(boolean starter) throws RemoteException {
        this.starter = starter;
    }

    @Override
    public void setNickname(String nick) {
        nickname = nick;
    }

    @Override
    public void setOnline(boolean b) {
        online = b;
    }

    @Override
    public void setPing(boolean b) {
        ping = b;
    }

    @Override
    public void showAction(Action act) throws IOException {
        if (online) {
            try {
                client.showAction(act);
            } catch (RemoteException e) {
                // Gestione della disconnessione del client
                System.err.println("Eccezione showAction di ClientRmi (ping settato a false)");
                ping = false;
            }
        }
    }


}
