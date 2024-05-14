package it.polimi.ingsw.clientProva;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.rmi.RemoteException;

public class ServerRmi implements VirtualServer {


    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        return false;
    }

    @Override
    public void sendAction(Action action) throws RemoteException {

    }

    @Override
    public void addPlayer(Player p, VirtualView v) throws RemoteException {

    }
}
