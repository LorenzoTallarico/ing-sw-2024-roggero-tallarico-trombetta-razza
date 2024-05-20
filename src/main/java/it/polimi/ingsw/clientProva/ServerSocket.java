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

    private final ObjectOutputStream outputStream;
    private final BlockingQueue<Action> serverActionReceived;
    private final ObjectInputStream inputStream;

    public ServerSocket(Socket cliSocket, BlockingQueue<Action> serverActionReceived) throws IOException {
        this.outputStream = new ObjectOutputStream(cliSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(cliSocket.getInputStream());
        this.serverActionReceived = serverActionReceived;
    }

    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        return false;
    }

    @Override
    public void sendAction(Action action) throws RemoteException {
        try{
            outputStream.writeObject(action);
            outputStream.flush();
        }catch (IOException e){
            e.printStackTrace();
        }

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
                serverActionReceived.put(action);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
