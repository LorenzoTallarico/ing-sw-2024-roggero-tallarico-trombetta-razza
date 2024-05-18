package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networking.action.Action;


import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.rmi.RemoteException;
import java.util.concurrent.BlockingQueue;

public class ClientSocket implements VirtualView, Runnable {
    private Socket serSocket = null;
    private final ObjectOutputStream outputStream;
    private final BlockingQueue<Action> serverActions;
    private final ObjectInputStream inputStream;
    private String nickname = null;
    private boolean connected = true;

    public ClientSocket(Socket serSocket, BlockingQueue<Action> serverActions) throws IOException {
        this.serSocket = serSocket;
        this.outputStream = new ObjectOutputStream(serSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(serSocket.getInputStream());
        this.serverActions = serverActions;
    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showAction(Action act) throws IOException {
        //qui il client sta MANDANDO l'azione
        outputStream.writeObject(act);
        outputStream.flush();
    }

    @Override
    public String getNickname() {
        //if(firstConnection)
        //invio AskNickname
        //ricezione Nickname e set dell'attributo

        return nickname;
    }


    public void setNickname(String nickname){
        this.nickname = nickname;
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
                serverActions.put(action);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
