
package it.polimi.ingsw.networkingProva;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualView;


import java.io.IOException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientRmi extends UnicastRemoteObject implements VirtualView {
    private VirtualView client = null;
    private String nickname = null;
    private boolean ping = true;
    private boolean online;
    private boolean inTurn;
    private boolean starter = false;

    public ClientRmi(VirtualView client) throws RemoteException{
        this.client = client;
        //sarebbero da inizializzare tutti
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

    public boolean getInTurn() {
        return inTurn;
    }

    @Override
    public void setStartRoutine(boolean b) throws RemoteException {

    }

    @Override
    public boolean getStartRoutine() throws RemoteException {
//        return client.getStartRoutine();
        System.out.println("Sto chiamando il metodo di ClientRmi************************************************************************************************");
        return false;
    }

    @Override
    public void setStarter(boolean starter) throws RemoteException {
        this.starter = starter;
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

    public void setInTurn(boolean b) {
        inTurn = b;
    }

    @Override
    public void reportError(String details) throws RemoteException {

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
