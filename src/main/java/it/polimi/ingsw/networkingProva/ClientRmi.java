package it.polimi.ingsw.networkingProva;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualView;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRmi extends UnicastRemoteObject implements VirtualView {
    private VirtualView client = null;


    public ClientRmi(VirtualView client) throws RemoteException{
        this.client = client;
    }


    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showAction(Action act) throws IOException {
        client.showAction(act);
    }

    @Override
    public String getNickname() throws RemoteException {
        return client.getNickname();
    }

    @Override
    public boolean getOnline() { //da aggiungere
        return false;
    }

    @Override
    public boolean getGui() {
        return client.getGui();
    }


}
