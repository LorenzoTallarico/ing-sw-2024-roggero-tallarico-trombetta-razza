package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.toserver.PongAction;

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
    public  void sendAction(Action action) throws RemoteException {
        synchronized (outputStream){
            try{
                //System.out.println("Invio messaggio: " + action.getType());
                outputStream.writeObject(action);
                outputStream.flush();
                outputStream.reset();
                //Thread.sleep(10); // Piccola pausa per sincronizzare il flusso
            }catch (IOException /*| /*InterruptedException */ e){
                e.printStackTrace();
                closeResources();
            }
        }

    }

    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Action action = null;
                    try {
                        action = (Action) inputStream.readObject();
                    } catch (IOException | ClassNotFoundException e) {
                        throw new RuntimeException(e);
                    }
                    if (action.getType().equals(ActionType.PING)) {
                        sendAction(new PongAction(""));
                    } else {
                        serverActionReceived.put(action);
                    }
                } catch (InterruptedException e) {
                    System.err.println("InterruptedException durante put: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            System.err.println("IOException durante readObject: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                System.err.println("IOException durante la chiusura dello stream: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void closeResources() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura delle risorse: " + e.getMessage());
        }
    }

}

