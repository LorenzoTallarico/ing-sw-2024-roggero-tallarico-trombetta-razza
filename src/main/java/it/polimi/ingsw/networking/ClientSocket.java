
package it.polimi.ingsw.networking;

import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.toclient.AskingPlayersNumberAction;
import it.polimi.ingsw.networking.action.toclient.ErrorAction;
import it.polimi.ingsw.networking.action.toclient.JoiningPlayerAction;
import it.polimi.ingsw.networking.action.toserver.ReconnectedPlayerAction;
import it.polimi.ingsw.networking.action.toserver.SetNicknameAction;
import it.polimi.ingsw.networking.action.toserver.StartAction;
import it.polimi.ingsw.networking.action.Action;


import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.io.ObjectOutputStream;
import java.io.ObjectInputStream;
import java.net.SocketException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;

/**
 * The ClientSocket class implements the VirtualView interface and handles the
 * server socket communication with the client.
 */
public class ClientSocket implements VirtualView, Runnable {

    /**
     * The output stream for sending actions to the server.
     */
    private final ObjectOutputStream outputStream;

    /**
     * The input stream for receiving actions from the server.
     */
    private final ObjectInputStream inputStream;


    /**
     * Queue for actions the receives from the clients
     */
    private final BlockingQueue<Action> serverActions;

    /**
     * Queue for actions the server sends to the client
     */
    private final BlockingQueue<Action> clientActions;

    /**
     * The list of connected clients.
     */
    private final ArrayList<VirtualView> clients;

    /**
     * The ping status of the client.
     */
    private boolean ping;

    /**
     * The online status of the client.
     */
    private boolean online;

    /**
     * The nickname of the client.
     */
    private String nickname = null;

    /**
     * The status of whether the game has started.
     */
    private final boolean gameStarted;

    /**
     * The number of players in the game.
     */
    private final int playersNumber;

    /**
     * The starter status of the client.
     */
    private boolean starter = false;

    /**
     * Constructs a ClientSocket instance.
     *
     * @param serSocket      the socket connected to the server.
     * @param serverActions  the queue for actions server receives from the client.
     * @param clientActions  the queue for actions server sends to the client.
     * @param clients        the list of connected clients.
     * @param gameStarted    the status of whether the game has started.
     * @param playersNumber  the number of players in the game.
     * @throws IOException if an I/O error occurs.
     */
    public ClientSocket(Socket serSocket, BlockingQueue<Action> serverActions, BlockingQueue<Action> clientActions, ArrayList<VirtualView> clients, boolean gameStarted, int playersNumber) throws IOException {
        this.outputStream = new ObjectOutputStream(serSocket.getOutputStream());
        this.inputStream = new ObjectInputStream(serSocket.getInputStream());
        this.serverActions = serverActions;
        this.clientActions = clientActions;
        this.clients = clients;
        this.gameStarted = gameStarted;
        this.playersNumber = playersNumber;
    }

    //GETTER
    /**
     * Gets the nickname of the client.
     *
     * @return the nickname of the client.
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Gets the online status of the client.
     *
     * @return true if the client is online, false otherwise.
     */
    @Override
    public boolean getOnline() {
        return online;
    }

    /**
     * Gets the ping status of the client.
     *
     * @return true if the client is responding to pings, false otherwise.
     */
    @Override
    public boolean getPing() {
        return ping;
    }

    /**
     * Gets the nickname of the first client. This method is not implemented in this class.
     *
     * @return an empty string.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public String getNicknameFirst() throws RemoteException {
        return "";
    }

    /**
     * Gets the starter status of the client.
     *
     * @return true if the client is the starter, false otherwise.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public boolean getStarter() throws RemoteException {
        return starter;
    }

    //SETTER

    /**
     * Sets the nickname of the client.
     *
     * @param nickname the nickname of the client.
     */
    @Override
    public void setNickname(String nickname){
        this.nickname = nickname;
    }

    /**
     * Sets the online status of the client.
     *
     * @param online true if the client is online, false otherwise.
     */
    @Override
    public void setOnline(boolean online) {
        this.online = online;
    }

    /**
     * Sets the ping status of the client.
     *
     * @param b true if the client is responding to pings, false otherwise.
     */
    @Override
    public void setPing(boolean b) {
        ping=b;
    }

    /**
     * Sets the starter status of the client.
     *
     * @param starter true if the client is the starter, false otherwise.
     */
    @Override
    public void setStarter(boolean starter) {
        this.starter = starter;
    }

    //METHODS

    /**
     * Sends an action to the client.
     *
     * @param act the action to be sent to the client.
     * @throws IOException if an I/O error occurs.
     */
    @Override
    public void showAction(Action act) throws IOException {
        //qui il server sta MANDANDO l'azione al client
        synchronized (outputStream) {
            try{
                System.out.println("Sending action to client" + act.getType());
                outputStream.writeObject(act);
                outputStream.flush();
                outputStream.reset();
            } catch (SocketException e) {
                System.err.println("Connection reset by peer");
                closeResources();
            }
        }
    }

    /**
     *
     * Thread that receives actions from client, and put the in the corresponding queue
     * Used for creating the first connection client server and possible reconnections
     */
    @Override
    public void run() {
        try {
            while (true) {
                Action action = null;
                try {
                    action = (Action) inputStream.readObject();
                } catch (SocketException | EOFException e) {
                    System.out.println("Connection reset by peer");
                    break;
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                    break;

                }
                if (nickname != null) {
                    if (action.getType().equals(ActionType.PONG)) {
                        this.ping = true;
                        //System.out.println("ho settato il ping a true di " + nickname);
                    } else {
                        try {
                            serverActions.put(action);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }
                    }
                } else if (action.getType().equals(ActionType.SETNICKNAME)) {

                    if (!gameStarted) { // connection to Lobby
                        String nick = ((SetNicknameAction) action).getNickname();
                        System.out.println("Nick: " + nick);
                        boolean sizeRequest = false;
                        if (!clients.isEmpty()) {
                            for (VirtualView v : this.clients) {
                                if (nick != null && v.getNickname() != null && v.getNickname().equalsIgnoreCase(nick)) {
                                    System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing and now online.");
                                    try {
                                        showAction(new ErrorAction(nick, "Denied connection to a new client, user \"" + nick + "\" already existing and now online."));
                                        showAction(new SetNicknameAction(null));
                                        return;
                                    } catch (Exception ignored) { }
                                }
                            }
                            if (countOnlinePlayer() >= 4) {
                                System.out.println("> Denied connection to a new client, max number of players already reached.");
                                try {
                                    showAction(new ErrorAction(nick, "Max amount of players reached."));
                                    showAction(new SetNicknameAction(null));
                                    return;
                                } catch (Exception ignored) {}

                            }
                        }
                        if (clients.size() == 1 && playersNumber == 0) {
                            try {
                                showAction(new ErrorAction(nick, "Another player has just started a game, they still haven't chosen the size of the game, wait some seconds before reconnecting."));
                                showAction(new SetNicknameAction(null));
                                return;
                            } catch (Exception ignored) {}
                        }
                        if (clients.isEmpty()) {
                            sizeRequest = true;
                        }
                        online = true;
                        ping = true;
                        nickname = nick;
                        System.out.println("nickname: " + nickname);
                        System.out.println("> Allowed Socket connection to a new client named \"" + nick + "\".");
                        clients.add(this);
                        showAction(new SetNicknameAction(nickname));
                        if (sizeRequest) {
                            try {
                                clientActions.put(new AskingPlayersNumberAction(nick));
                            } catch (InterruptedException ignored) {}
                        }
                        try {
                            if (clients.size() > 1) {
                                clientActions.put(new JoiningPlayerAction(nick, countOnlinePlayer(), playersNumber));
                                serverActions.put(new StartAction(null));
                            }
                        } catch (InterruptedException ignored) {}
                    } else {
                        //Reconnect (search for old virtualview)
                        nickname = ((SetNicknameAction) action).getNickname();
                        VirtualView oldVirtualView = null;
                        for (VirtualView c : clients) {
                            if (c.getNickname().equalsIgnoreCase(nickname)) {
                                oldVirtualView = c;
                                break;
                            }
                        }
                        if (oldVirtualView != null && !oldVirtualView.getOnline()) {
                            try {

                                starter = oldVirtualView.getStarter();
                                online = true;
                                ping = true;
                                int index = clients.indexOf(oldVirtualView);
                                clients.remove(index);
                                clients.add(index, this);
                                showAction(new SetNicknameAction(nickname));
                                serverActions.put(new ReconnectedPlayerAction(nickname, oldVirtualView, this));
                            } catch (InterruptedException ignored) {}
                        } else {
                            showAction(new SetNicknameAction(null));
                            System.out.println("> User " + nickname + " already online or doesn't exist");
                        }

                    }


                }
            }

        } catch (Exception ignored) {}

        finally {
            System.out.println("> Client disconnected: " + nickname);
            closeResources();
        }
    }

    /**
     * Closes the input and output streams.
     */
    private void closeResources() {
        try {
            if (inputStream != null)
                inputStream.close();
            if (outputStream != null)
                outputStream.close();
        } catch (IOException e) {
            System.err.println("Error during socket resource closure: " + e.getMessage());
            System.exit(0);
        }
    }

    /**
     * Counts the number of online players.
     *
     * @return the number of online players.
     * @throws RemoteException if a remote communication error occurs.
     */
    private int countOnlinePlayer() throws RemoteException {
        int count = 0;
        synchronized (clients) {
            for (VirtualView c : this.clients) {
                if (c.getOnline())
                    count++;
            }
        }
        return count;
    }

}
