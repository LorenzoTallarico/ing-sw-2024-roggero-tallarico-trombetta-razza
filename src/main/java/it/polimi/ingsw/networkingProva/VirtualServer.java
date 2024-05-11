package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networking.socket.VirtualViewSocket;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface VirtualServer extends Remote {

    boolean connect(VirtualView client) throws RemoteException;

    void sendAction(Action action) throws RemoteException;

}
