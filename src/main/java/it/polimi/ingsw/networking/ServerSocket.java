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

/**
 * The ServerSocket class implements the VirtualServer interface and handles the
 * client socket communication with the server. It also implements the Runnable interface
 * to allow for multi-threaded operation.
 */
public class ServerSocket implements VirtualServer, Runnable {

    /**
     * The output stream for sending data to the client.
     */
    private final ObjectOutputStream outputStream;

    /**
     * The queue to store received actions.
     */
    private final BlockingQueue<Action> serverActionReceived;

    /**
     * The input stream for receiving data from the client.
     */
    private final ObjectInputStream inputStream;


    /**
     * Constructs a ServerSocket instance.
     *
     * @param cliSocket the client socket connected to the server.
     * @param serverActionReceived the queue to store received actions.
     * @throws IOException if an I/O error occurs when creating the input or output streams.
     */
    public ServerSocket(Socket cliSocket, BlockingQueue<Action> serverActionReceived) throws IOException {
        this.outputStream = new ObjectOutputStream(cliSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(cliSocket.getInputStream());
        this.serverActionReceived = serverActionReceived;
    }

    /**
     * Connects a client to the server. Not implemented for Socket with this method.
     *
     * @param client the client to connect to the server.
     * @return false as the method is not implemented.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        return false;
    }

    /**
     * Sends an action to the client.
     *
     * @param action the action to be sent.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public  void sendAction(Action action) throws RemoteException {
        synchronized (outputStream){
            try{
                outputStream.writeObject(action);
                outputStream.flush();
                outputStream.reset();
            } catch (IOException e){
                System.err.println("> Error during sendAction (ServerSocket), closing input/output streams");
                closeResources();
            }
        }

    }


    /**
     * Thread that receives the actions from the server and put them in the corresponded queue.
     */
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
                    System.err.println("InterruptedException during put: " + e.getMessage());
                    closeResources();
                }
            }
        } catch (IOException e) {
            System.err.println("IOException during readObject: " + e.getMessage());
            closeResources();
        } finally {
            try {
                inputStream.close();
                outputStream.close();
            } catch (IOException e) {
                System.err.println("IOException stream closure: " + e.getMessage());
                System.exit(0);
            }
        }
    }

    /**
     * Closes the input and output streams to release resources.
     */
    private void closeResources() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (IOException e) {
            System.err.println("Error during socket resources closure: " + e.getMessage());
            System.exit(0);
        }
    }

}

