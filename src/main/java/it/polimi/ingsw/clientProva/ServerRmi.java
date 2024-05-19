package it.polimi.ingsw.clientProva;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.io.IOException;
import java.rmi.RemoteException;

public class ServerRmi implements VirtualServer {
    private VirtualServer server = null;

    public ServerRmi(VirtualServer server) throws RemoteException{
        this.server = server;
    }



    @Override
    public void sendAction(Action act) throws RemoteException {
        server.sendAction(act);
    }

    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        return false;
    }

    @Override
    public void addPlayer(Player p, VirtualView v) throws RemoteException {

    }
}
