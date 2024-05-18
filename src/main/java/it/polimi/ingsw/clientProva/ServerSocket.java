package it.polimi.ingsw.clientProva;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class ServerSocket implements VirtualServer, Runnable {

    private final Socket cliSocket;
    private final ObjectOutputStream outputStream;
    private final BlockingQueue<Action> clientActionsToSend;
    private final ObjectInputStream inputStream;
    private String nickname = null;
    private boolean connected = true;

    public ServerSocket(Socket cliSocket, BlockingQueue<Action> clientActionsToSend) throws IOException {
        this.cliSocket = cliSocket;
        this.outputStream = new ObjectOutputStream(cliSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(cliSocket.getInputStream());
        this.clientActionsToSend = clientActionsToSend;
    }

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
    @Override
    public void run() {
        while(true) {
            Action action = null;
            try {
                action = (Action) inputStream.readObject();
            } catch (IOException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                clientActionsToSend.put(action);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
