
package it.polimi.ingsw.networking;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.ChatMessageAction;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * WebServer is responsible for managing the network communication between the clients and the server.
 * It supports both RMI and socket-based communication mechanisms. Here all the information between clients
 * and server are exchanged through actions, each one is handled differently.
 * Webserver also manages the possible client disconnections.
 */
public class WebServer implements VirtualServer {

    /**
     * Default port number for the RMI server.
     */
    private static int PORT_RMI = 6969;

    /**
     * Default port number for the socket server.
     */
    private static int PORT_SOCKET = 7171;

    /**
     * The GameController instance to manage the game logic.
     */
    private GameController controller = null;

    /**
     * List of clients.
     */
    private final ArrayList<VirtualView> clients = new ArrayList<>();

    /**
     * Actions received from clients.
     */
    private final BlockingQueue<Action> serverActions = new LinkedBlockingQueue<>();

    /**
     * Actions to be sent to clients.
     */
    private final BlockingQueue<Action> clientActions = new LinkedBlockingQueue<>();

    /**
     * Flag checking the thread that handles the actions to be sent to client.
     */
    private boolean connectionFlagClient = true;

    /**
     * Flag checking the thread that handles the action received form clients.
     */
    private boolean connectionFlagServer = true;

    /**
     * Indicates if the game has started.
     */
    private boolean gameStarted = false;

    /**
     * Number of connected players.
     */
    private int numStarter = 0;

    /**
     * Number of initial players (used to control initial connections).
     */
    private int playersNumber = 0;

    /**
     * Constructs a WebServer with specified ports for RMI and socket communication.
     *
     * @param ports an array of integers where ports[0] is the RMI port and ports[1] is the socket port
     */
    public WebServer(int[] ports) {
        PORT_RMI = ports[0];
        PORT_SOCKET = ports[1];
        start();
    }

    public WebServer(GameController controller, int[] ports) {
        this.controller = controller;
        PORT_RMI = ports[0];
        PORT_SOCKET = ports[1];
    }

    /**
     * Starts the RMI server.
     * Binds the server to the specified RMI port and registers the server name "GameServer".
     */
    public void startRmiServer() {
        try {
            final String serverName = "GameServer";
            VirtualServer server = this;
            VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(PORT_RMI);
            registry.rebind(serverName, stub);
            System.out.println("RMI Server ready.");
        } catch (RemoteException e) {
            System.err.println("Error during the start of the RMI server: " + e.getMessage());
        }
    }

    /**
     * Starts the socket server.
     * Listens on the specified socket port and accepts client connections, each handled in a separate thread.
     */
    public void startSocketServer() {
        try (ServerSocket serverSocket = new ServerSocket(PORT_SOCKET)) {
            System.out.println("Server Socket ready.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                VirtualView actualSocket = (VirtualView) new ClientSocket(clientSocket, serverActions, clientActions, clients, gameStarted, playersNumber);
                Thread clientSocketThread = new Thread((Runnable) actualSocket);
                clientSocketThread.start();
            }
        } catch (IOException e) {
            System.err.println("Error during the start of the Socket server: " + e.getMessage());
        }
    }

    /**
     * Updates clients with actions from the clientActions queue.
     * This method continuously runs while the connectionFlagClient is true.
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     * @throws RemoteException      if there is an RMI-related issue
     */
    public void clientsUpdateThread() throws InterruptedException, RemoteException {
        try {
            while (connectionFlagClient) {
                if (!clientActions.isEmpty()) {
                    Action a = clientActions.take();
                    synchronized (clients) {
                        for (VirtualView handler : clients) {
                            if (handler.getOnline())
                                handler.showAction(a);
                        }
                    }
                }
            }
        } catch (InterruptedException | IOException e) {
            connectionFlagClient = false;
            System.err.println("Error encountered during the handling of an action taken form the clients update thread: " + e.getMessage());
        }
    }

    /**
     * Processes actions from the serverActions queue.
     * This method continuously runs while the connectionFlagServer is true.
     * Each action is handled according to his type
     *
     * @throws InterruptedException if the thread is interrupted while waiting
     * @throws RemoteException      if there is an RMI-related issue
     */
    public void serverUpdateThread() throws InterruptedException, RemoteException {
        while (connectionFlagServer) {
            try {
                Action action = serverActions.take();
                System.out.println("> Handling action, action type \"" + action.getType().toString() + "\".");
                Action newAction = null;
                switch (action.getType()) {
                    case CHOSENPLAYERSNUMBER:
                        playersNumber = ((ChosenPlayersNumberAction) action).getPlayersNumber();
                        break;
                    case ASKINGCHAT:
                        newAction = new WholeChatAction(action.getAuthor(), this.controller.getWholeChat());
                        clientActions.put(newAction);
                        break;
                    case CHATMESSAGE:
                        this.controller.sendChatMessage(((ChatMessageAction) action).getMessage());
                        clientActions.put(action);
                        break;
                    case CHOSENSIDESTARTERCARD:
                        this.controller.setStarterCardSide(action.getAuthor(), ((ChosenSideStarterCardAction) action).getSide());
                        break;
                    case CHOSENACHIEVEMENT:
                        this.controller.setSecretAchievement(action.getAuthor(), ((ChosenAchievementAction) action).getAchievement());
                        numStarter++;
                        break;
                    case PLACINGCARD:
                        if (!this.controller.placeCard(action.getAuthor(), ((PlacingCardAction) action).getCardIndex(), ((PlacingCardAction) action).getSide(), ((PlacingCardAction) action).getRow(), ((PlacingCardAction) action).getColumn())) {
                            newAction = new PlacedErrorAction(action.getAuthor());
                            clientActions.put(newAction);
                        }
                        break;
                    case CHOSENDRAWCARD:
                        this.controller.drawCard(action.getAuthor(), ((ChosenDrawCardAction) action).getIndex());
                        break;
                    case START:
                        if (countOnlinePlayer() == playersNumber) {
                            controller.setPlayersNumber(playersNumber);
                            synchronized (clients) {
                                for (VirtualView client : clients) {
                                    if (client.getOnline()) {
                                        synchronized (this.clients) {
                                            this.controller.addPlayer(new Player(client.getNickname()), client);

                                        }
                                    }
                                }
                            }
                            gameStarted = true;

                        }
                        break;
                    case RECONNECTEDPLAYER:
                        this.controller.reconnection(((ReconnectedPlayerAction) action).getNick(), ((ReconnectedPlayerAction) action).getOldVirtualView(), ((ReconnectedPlayerAction) action).getNewVirtualview());
                        break;
                    default:
                        break;
                }
            } catch (InterruptedException e) {
                connectionFlagServer = false;
                System.err.println("Error encountered during the handling of an action taken form the server update thread: " + e.getMessage());
            }
        }
    }

    /**
     * Starts the WebServer by initializing and running the RMI and socket servers.
     * Also starts threads to handle client updates and server requests.
     */
    public static void start() {
        int[] ports = {PORT_RMI, PORT_SOCKET};
        WebServer webServer = new WebServer(new GameController(), ports);
        Thread rmiThread = new Thread(webServer::startRmiServer);
        Thread socketThread = new Thread(webServer::startSocketServer);
        rmiThread.start();
        socketThread.start();

        //Thread for client update
        Runnable clientsUpdateRunnable = () -> {
            try {
                webServer.clientsUpdateThread();
            } catch (InterruptedException | RemoteException ignored) {}
        };
        Thread clientsUpdateThread = new Thread(clientsUpdateRunnable);
        clientsUpdateThread.start();

        //Thread to execute client requests
        Runnable serverUpdateRunnable = () -> {
            try {
                webServer.serverUpdateThread();
            } catch (InterruptedException | RemoteException ignored) {}
        };
        Thread serverUpdateThread = new Thread(serverUpdateRunnable);
        serverUpdateThread.start();

        //Thread to check clients responsivity
        Runnable checkAliveRunnable = () -> {
            try {
                webServer.checkAliveThread();
            } catch (InterruptedException | IOException ignored) {}
        };
        Thread checkAliveThread = new Thread(checkAliveRunnable);
        checkAliveThread.start();
    }

    /**
     * Handles the connection request from an RMI client.
     * If the game hasn't started, it handles the connection to the lobby.
     * If the game has started, it attempts to reconnect the client.
     *
     * @param client the VirtualView instance representing the client
     * @return true if the connection is successful, false otherwise
     * @throws RemoteException if there is an RMI-related issue
     */
    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            System.err.println("> Join request received.");
            String nick = client.getNicknameFirst();
            if (!gameStarted) { // connection to Lobby
                boolean sizeRequest = false;
                if (!clients.isEmpty()) {
                    for (VirtualView v : this.clients) {
                        if (nick != null && v.getNickname().equalsIgnoreCase(nick)) {
                            System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing and now online.");
                            try {
                                client.showAction(new ErrorAction(nick, "Denied connection to a new client, user \"" + nick + "\" already existing and now online."));
                            } catch (Exception ignored) {}
                            return false;
                        }
                    }
                    if (countOnlinePlayer() >= 4) {
                        System.out.println("> Denied connection to a new client, max number of players already reached.");
                        try {
                            client.showAction(new ErrorAction(nick, "Max amount of players reached."));
                        } catch (Exception ignored) {}
                        return false;
                    }
                }
                if (clients.size() == 1 && playersNumber == 0) {
                    try {
                        client.showAction(new ErrorAction(nick, "Another player has just started a game, they still haven't chosen the size of the game, wait some seconds before reconnecting."));
                    } catch (Exception ignored) {}
                    return false;
                }
                if (clients.isEmpty()) {
                    sizeRequest = true;
                }
                ClientRmi c = new ClientRmi(client);
                c.setOnline(true);
                c.setPing(true);
                c.setNickname(nick);
                System.out.println("> Allowed RMI connection to a new client named \"" + nick + "\".");
                clients.add(c);
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
                return true;

            } else { // Reconnection
                VirtualView oldVirtualView = null;
                for (VirtualView c : clients) {
                    if (c.getNickname().equalsIgnoreCase(nick)) {
                        oldVirtualView = c;
                        break;
                    }
                }
                if (oldVirtualView != null && !oldVirtualView.getOnline()) {
                    try {
                        VirtualView cli = new ClientRmi(client);
                        cli.setStarter(oldVirtualView.getStarter());
                        cli.setOnline(true);
                        cli.setPing(true);
                        cli.setNickname(nick);
                        int index = clients.indexOf(oldVirtualView);
                        clients.remove(index);
                        clients.add(index, cli);
                        serverActions.put(new ReconnectedPlayerAction(nick, oldVirtualView, cli));
                    } catch (InterruptedException ignored) {}
                } else {
                    System.out.println("> User " + nick + " already online or doesn't exist.");
                    try {
                        client.showAction(new ErrorAction(nick, "Game started, user not found."));
                    } catch (Exception ignored) {}
                    return false;
                }
                return true;
            }
        }
    }

    /**
     * Counts the number of currently online players.
     *
     * @return the number of online players
     * @throws RemoteException if there is an RMI-related issue
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

    /**
     * Receives an action form a client (that sent a command) and processes it.
     * If the action is a PONG type, it sets the client's ping status to true.
     * Otherwise, it puts the action into the server actions queue.
     *
     * @param action the action received from the client
     * @throws RemoteException if there is any networking related issue
     */
    @Override
    public void sendAction(Action action) throws RemoteException {
        try {
            if (action.getType().equals(ActionType.PONG)) {
                for (VirtualView c : clients) {
                    if (c.getNickname().equalsIgnoreCase(action.getAuthor())) {
                        c.setPing(true);
                    }
                }

                return;
            }
            serverActions.put(action);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Periodically checks the connection status of clients.
     * Sends ping actions to clients and marks them as disconnected if they do not respond.
     * Handles disconnection of clients and manages game start conditions.
     *
     * @throws InterruptedException if the thread is interrupted
     * @throws IOException if there is an I/O-related issue
     */
    public void checkAliveThread() throws InterruptedException, IOException {
        ArrayList<String> disconnectedClient = new ArrayList<>();
        while (true) {
            boolean startActionRequired = false;
            synchronized (clients) {
                for (VirtualView c : clients) {
                    if (c.getOnline() && c.getNickname() != null) {
                        c.setPing(false);
                        try {
                            c.showAction(new PingAction());
                        } catch (Exception ignored) {}
                    }
                }
            }

            Thread.sleep(1000);
            disconnectedClient.clear();
            synchronized (clients) {
                for (VirtualView c : clients) {
                    if (!c.getPing() && c.getOnline()) {
                        disconnectedClient.add(c.getNickname());
                        c.setOnline(false);
                    }

                }
            }
            //adds disconnection action to queue
            for (String c : disconnectedClient) {
                clientActions.put(new DisconnectedPlayerAction(c, countOnlinePlayer()));
                for (VirtualView v : clients) {
                    if (disconnectedClient.contains(v.getNickname())) {
                        v.setOnline(false);
                    }
                }
            }

            if (!gameStarted) {
                //client offline will be eliminated
                ArrayList<VirtualView> clientsToRemove = new ArrayList<>();
                for (VirtualView c : clients) {
                    if (disconnectedClient.contains(c.getNickname()))
                        clientsToRemove.add(c);
                }

                //removes disconnected clients
                if (!disconnectedClient.isEmpty()) {
                    synchronized (clients) {
                        clients.removeAll(clientsToRemove);
                        startActionRequired = true;
                    }
                }
                if (countOnlinePlayer() > 1 && startActionRequired) {
                    try {
                        clients.get(0).showAction(new AskingStartAction(clients.get(0).getNickname(), countOnlinePlayer()));
                    } catch (RemoteException ignored) {}
                    catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {  //game not started
                //disconnected clients will be set offline
                for (String c : disconnectedClient) {
                    if (numStarter == clients.size()) { //every player chose the starter card and the achievement
                        controller.disconnection(c);
                        if (countOnlinePlayer() <= 1) {
                            String lastOnlineNick = null;
                            for (VirtualView cli : clients) {
                                if (cli.getOnline()) {
                                    lastOnlineNick = cli.getNickname();
                                    Action act = new WaitAction(lastOnlineNick);
                                    cli.showAction(act);
                                    break;
                                }
                            }
                        }

                    } else {  //if still in the starter card / achievement choice phase
                        for (VirtualView v : clients) {
                            if (v.getNickname().equalsIgnoreCase(c)) {
                                if (!v.getStarter()) {
                                    waitingRoutineChoiceAchi(v.getNickname());
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * Starts a routine to wait for a player to reconnect during the starter card and achievement choice phase,
     * this will start only when one last player is connected to the game.
     * The routine checks every second for up to 30 seconds if the player has reconnected.
     * If the player does not reconnect within the time limit, the game is shut down.
     *
     * @param nickname the nickname of the player who needs to reconnect
     */
    public void waitingRoutineChoiceAchi(String nickname) {
        Runnable task = new Runnable() {
            int attempts = 30;

            @Override
            public void run() {
                try {
                    while (attempts != 0) {
                        Thread.sleep(1000);
                        synchronized (clients) {
                            for (VirtualView c : clients) {
                                if (c.getNickname().equalsIgnoreCase(nickname) && c.getOnline()) {
                                    return;
                                }
                            }
                        }
                        attempts--;
                        System.out.println("Seconds remaining: " + attempts);
                    }
                    shutdown("Game ended, user did not reconnected during starter card and achievement choice.");
                } catch (InterruptedException | IOException ignore) {}
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    /**
     * Shuts down the server and disconnects all clients, sending them an error message.
     *
     * @param message the message to send to clients before shutting down
     * @throws IOException if an I/O error occurs while sending the error message to clients
     */
    public void shutdown (String message) throws IOException {
        synchronized (clients) {
            for (VirtualView c : clients) {
                c.showAction(new ErrorAction(c.getNickname(), message, true));
            }
        }
        System.exit(0);
    }


}




