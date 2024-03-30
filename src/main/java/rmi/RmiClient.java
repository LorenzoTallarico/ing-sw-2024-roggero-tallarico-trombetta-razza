package rmi;

import it.polimi.ingsw.model.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class RmiClient extends UnicastRemoteObject implements VirtualView {

    final VirtualServer server;

    public RmiClient(VirtualServer server) throws RemoteException{
        this.server = server;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";
        Registry registry = LocateRegistry.getRegistry(args[0],1234);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        new RmiClient(server);
    }
    // private void run(){}

    @Override
    public void showUpdate(int state) throws RemoteException {


    }

    @Override
    public void reportError(String details) throws RemoteException {

    }
}
