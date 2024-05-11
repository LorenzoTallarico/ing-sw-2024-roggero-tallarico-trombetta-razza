package it.polimi.ingsw.networkingProva;
import it.polimi.ingsw.networking.action.Action;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientHandlerRmi extends UnicastRemoteObject implements ClientHandler {
    private final VirtualViewRmi virtualViewRmi;

    public ClientHandlerRmi(VirtualViewRmi virtualViewRmi) throws RemoteException {
        this.virtualViewRmi = virtualViewRmi;
    }

    @Override
    public void sendAction(Action action) throws RemoteException {
        virtualViewRmi.showAction(action);
    }
}
