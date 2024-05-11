package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networkingProva.VirtualViewRmi;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
/*
public class RmiServer extends UnicastRemoteObject implements VirtualServer {
    private final GameController controller;
    private BlockingQueue<Action> clientActions;
    private ArrayList<ClientHandler> clients = null;

    public RmiServer(BlockingQueue<Action> clientActions, ArrayList<ClientHandler> clients) throws RemoteException {
        this.controller = new GameController(); // Aggiungi il controller se necessario
        this.clientActions = clientActions;
        this.clients = clients;
    }

    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        if (client != null && !clients.contains(client)) {
            clients.add(client);
            return true;
        }
        return false;
    }

    @Override
    public void sendAction(Action action) throws RemoteException {
        // Implementazione per l'invio di azioni tramite RMI
        try {
            clientActions.put(action);
        } catch (InterruptedException e) {
            throw new RemoteException("Errore durante l'invio dell'azione al client: " + e.getMessage());
        }
    }

    @Override
    public void addPlayer(Player p, VirtualView c) throws RemoteException {
        // Implementazione per l'aggiunta di un giocatore tramite RMI
        controller.addPlayer(p, c);
    }
}
*/
 */