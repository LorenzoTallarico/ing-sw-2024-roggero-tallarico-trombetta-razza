package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.action.Action;

import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.util.concurrent.BlockingQueue;

public class ClientHandlerSocket implements ClientHandler, Runnable {
    private final Socket clientSocket;
    private final ObjectOutputStream outputStream;
    private final BlockingQueue<Action> serverActions;
    private final ObjectInputStream inputStream;

    public ClientHandlerSocket(Socket clientSocket, BlockingQueue<Action> serverActions) throws IOException {
        this.clientSocket = clientSocket;
        this.outputStream = new ObjectOutputStream(clientSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(clientSocket.getInputStream());
        this.serverActions = serverActions;
    }

    @Override
    public void sendAction(Action action) throws IOException {
        outputStream.writeObject(action);
        outputStream.flush();
    }

    @Override
    public void run() {
        while(true) {
            Action action = null;
            try {
                action = (Action) inputStream.readObject();
            } catch (IOException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            try {
                serverActions.put(action);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
