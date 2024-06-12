package it.polimi.ingsw.clientProva;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.toserver.PongAction;
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

    //non serve
    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        return false;
    }

    @Override
    public /*NBBBB*/ synchronized void sendAction(Action action) throws RemoteException {
        try{
            outputStream.writeObject(action);
            outputStream.flush();
            outputStream.reset();
            //Thread.sleep(10); // Piccola pausa per sincronizzare il flusso
        }catch (IOException /*| /*InterruptedException */ e){
            e.printStackTrace();
        }

    }

    @Override
    public void addPlayer(Player p, VirtualView v) throws RemoteException {

    }



    @Override
    public void run() {
        try {
            while (true) {
                try {
                    Object obj = inputStream.readObject();
                    if (obj instanceof Action) {
                        Action action = (Action) obj;
                        if (action.getType().equals(ActionType.PING)) {
                            sendAction(new PongAction("")); // Da sistemare il nickname
                        }
                        serverActionReceived.put(action);
                    } else {
                        System.err.println("Ricevuto un oggetto che non è un'Action: " + obj);
                    }
                } catch (ClassNotFoundException e) {
                    System.err.println("ClassNotFoundException durante readObject: " + e.getMessage());
                    e.printStackTrace();
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























//    @Override
//    public void run() {
//        while (true) {
//            try {
//                Action action = (Action) inputStream.readObject();
//                if (action.getType().equals(ActionType.PING)) {
//                    sendAction(new PongAction("")); // da sistemare il nickname
//                }
//                serverActionReceived.put(action);
//            } catch (IOException e) {
//                System.err.println("IOException during readObject: " + e.getMessage());
//                e.printStackTrace();
//                break;
//            } catch (ClassNotFoundException e) {
//                System.err.println("ClassNotFoundException during readObject: " + e.getMessage());
//                e.printStackTrace();
//            } catch (InterruptedException e) {
//                System.err.println("InterruptedException during put: " + e.getMessage());
//                e.printStackTrace();
//            }
//        }
//    }
























//        @Override
//        public void run() {
//            while(true) {
//                Action action = null;
//                try {
//                    action = (Action) inputStream.readObject();
//                } catch (IOException | ClassNotFoundException e) {
//                    throw new RuntimeException(e);
//                }
//                try {
//                    if(action.getType().equals(ActionType.PING)){
//                        sendAction(new PongAction("")); //da Sistemare il nickname
//
//                    }
//                    serverActionReceived.put(action);
//                } catch (InterruptedException e) {
//                    throw new RuntimeException(e);
//                } catch (RemoteException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }









}

