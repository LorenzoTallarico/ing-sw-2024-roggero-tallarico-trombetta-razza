
package it.polimi.ingsw.networkingProva;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualView;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRmi implements VirtualView {
    private VirtualView client = null;
    private String nickname = null;
    private boolean ping;
    private boolean online;


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
    public boolean getOnline() { //da aggiungere
        return online;
    }

    @Override
    public boolean getPing() {
        return ping;
    }

    @Override
    public boolean getGui() throws RemoteException {
        return client.getGui();
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
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showAction(Action act) throws IOException {
        client.showAction(act);
    }


}
