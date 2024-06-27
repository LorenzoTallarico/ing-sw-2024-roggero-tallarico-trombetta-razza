
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

//Ricezione info in webServer e gestione di tali info, invece invio al client tutto in VirtualView
public class WebServer implements VirtualServer {
    private static int PORT_RMI = 6969;
    private static int PORT_SOCKET = 7171;
    private GameController controller = null;
    private final ArrayList<VirtualView> clients = new ArrayList<>();
    private final BlockingQueue<Action> serverActions = new LinkedBlockingQueue<>(); //Action arrivate da Client
    private final BlockingQueue<Action> clientActions = new LinkedBlockingQueue<>(); //Action da mandare a Client
    private boolean connectionFlagClient = true, connectionFlagServer = true;
    private boolean gameStarted = false;
    private int playersNumber = 0;
    private int numStarter = 0;

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
            System.err.println("Errore durante l'avvio del server Socket: " + e.getMessage());
        }
    }

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
            e.printStackTrace();
        }
    }

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
                                            //String textUpdate = "> Player " + p.getName() + " joined the game. " + this.controller.getCurrPlayersNumber() + "/" + (this.controller.getMaxPlayersNumber() == 0 ? "?" : this.controller.getMaxPlayersNumber());
                                            //System.out.println(textUpdate);
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
            }
        }
    }

    public static void start() {
        int[] ports = {PORT_RMI, PORT_SOCKET};
        WebServer webServer = new WebServer(new GameController(), ports);
        //listener and accepter start up
        Thread rmiThread = new Thread(webServer::startRmiServer);
        Thread socketThread = new Thread(webServer::startSocketServer);
        rmiThread.start();
        socketThread.start();

        //Thread for client update
        Runnable clientsUpdateRunnable = () -> {
            try {
                webServer.clientsUpdateThread();
            } catch (InterruptedException | RemoteException e) {
                // Gestione dell'eccezione
                e.printStackTrace();
            }
        };
        Thread clientsUpdateThread = new Thread(clientsUpdateRunnable);
        clientsUpdateThread.start();

        //Thread to execute client requests
        Runnable serverUpdateRunnable = () -> {
            try {
                webServer.serverUpdateThread();
            } catch (InterruptedException | RemoteException e) {
                // Gestione dell'eccezione
                e.printStackTrace();
            }
        };
        Thread serverUpdateThread = new Thread(serverUpdateRunnable);
        serverUpdateThread.start();
        Runnable checkAliveRunnable = () -> {
            try {
                webServer.checkAliveThread();
            } catch (InterruptedException | IOException e) {
                // Gestione dell'eccezione
                e.printStackTrace();
            }
        };
        Thread checkAliveThread = new Thread(checkAliveRunnable);
        checkAliveThread.start();
    }

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
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            return false;
                        }
                    }
                    if (countOnlinePlayer() >= 4) {
                        System.out.println("> Denied connection to a new client, max number of players already reached.");
                        try {
                            client.showAction(new ErrorAction(nick, "Max amount of players reached."));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
                if (clients.size() == 1 && playersNumber == 0) {
                    try {
                        client.showAction(new ErrorAction(nick, "Another player has just started a game, they still haven't chosen the size of the game, wait some seconds before reconnecting."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (clients.size() > 1) {
                        clientActions.put(new JoiningPlayerAction(nick, countOnlinePlayer(), playersNumber));
                        serverActions.put(new StartAction(null));
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                return true;
            } else { // Reconnect
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
                        //vedere se sul riferimento di Virtualviw client passato in ingresso gli va benen
                        int index = clients.indexOf(oldVirtualView);
                        clients.remove(index);
                        clients.add(index, cli);
                        serverActions.put(new ReconnectedPlayerAction(nick, oldVirtualView, cli));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                } else {
                    System.out.println("> User " + nick + " already online or doesn't exist.");
                    try {
                        client.showAction(new ErrorAction(nick, "Game started, user not found."));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return false;
                }
                return true;
            }
        }
    }

    //NB: controllare la sincronizzazione qui su "this"
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

    @Override
    public void sendAction(Action action) throws RemoteException {
        try {
            System.out.println("> Received action, type \"" + action.getType().toString() + "\".");
            if (action.getType().equals(ActionType.PONG)) {
                //synchronized (clients) {
                for (VirtualView c : clients) {
                    if (c.getNickname().equalsIgnoreCase(action.getAuthor())) {
                        //System.out.println("sostituito il boolean di " + action.getAuthor() + "trovato c :" + c.getNickname());
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

    public void checkAliveThread() throws InterruptedException, IOException {
        ArrayList<String> disconnectedClient = new ArrayList<>();
        while (true) {
            boolean startActionRequired = false;
            synchronized (clients) {
                for (VirtualView c : clients) {
                    if (c.getOnline() && c.getNickname() != null) {
                        c.setPing(false);
                        try {
                            System.out.println("invio ping a " + c.getNickname());
                            c.showAction(new PingAction());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }

            Thread.sleep(1000);
            disconnectedClient.clear();
            synchronized (clients) {
                for (VirtualView c : clients) {
                    //System.out.println("Client: " + c.getNickname() + " Online: " + c.getOnline() + " ping: " + c.getPing());
                    if (!c.getPing() && c.getOnline()) {
                        //System.err.println("Sono entrato in !c.getPing() && c.getOnline() ---> mi mette nella lista per disconnettere");
                        disconnectedClient.add(c.getNickname());
                        c.setOnline(false);
                    }

                }
            }

            for (String c : disconnectedClient) {
                clientActions.put(new DisconnectedPlayerAction(c, countOnlinePlayer()));
                System.err.println("appena fatta put di action Disconnected, ");
                for (VirtualView v : clients) {
                    if (disconnectedClient.contains(v.getNickname())) {
                        v.setOnline(false);
                    }
                }
            }

            if (!gameStarted) {
                ArrayList<VirtualView> clientsToRemove = new ArrayList<>();
                for (VirtualView c : clients) {
                    if (disconnectedClient.contains(c.getNickname()))
                        clientsToRemove.add(c);
                }

                if (!disconnectedClient.isEmpty()) {
                    synchronized (clients) {
                        clients.removeAll(clientsToRemove);
                        startActionRequired = true;
                    }
                }
                if (countOnlinePlayer() > 1 && startActionRequired) {
                    try {
                        clients.get(0).showAction(new AskingStartAction(clients.get(0).getNickname(), countOnlinePlayer()));
                        System.out.println("Invio AskingStart");
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            } else {
                for (String c : disconnectedClient) {
                    //System.err.println("Dentro for Virtualview nell'else di !gamestarted, elenco dei client:");
                    if (numStarter == clients.size()) {
                        controller.disconnection(c);
                        if (countOnlinePlayer() <= 1) {
                            //System.out.println("Dentro webserver: se è connesso solo un ultimo player parto con la routine\n");
                            String lastOnlineNick = null;
                            for (VirtualView cli : clients) {
                                if (cli.getOnline()) {
                                    lastOnlineNick = cli.getNickname();
                                    //Ho il nickname dell'ultimo player online, mando l'action per bloccare il client
                                    Action act = new WaitAction(lastOnlineNick);
                                    System.out.println("Sto per lanciare action che blocca il client");
                                    cli.showAction(act);
                                    break;
                                }
                            }
                        }

                    } else {
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

    public void waitingRoutineChoiceAchi(String nickname) {
        Runnable task = new Runnable() {
            int attempts = 30;

            @Override
            public void run() {
                try {
                    // NB: bisogna sincronizzare l'accesso a clients
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
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

        //butta giù clients e server
    public void shutdown (String messaggio) throws IOException {
        synchronized (clients) {
            for (VirtualView c : clients) {
                c.showAction(new ErrorAction(c.getNickname(), messaggio, true));
            }
        }
        System.exit(0);
    }


}

    /*
    checkAliveThread()
        se il gameStarted == false => l'utente che non risulta connesso viene eliminato e invio messaggio notificaDisconnessione e askingStart
        se il gameStarted == true => setto solo il boolean connected == false e sarà da gestire la ricezione e invio e salto turno
        per riconessione => modifica connection se gameStarted == true allora verra creata nuova VirtualView che verrà rimpiazzata e inviati tutti i dati della partita tramite un action
        il thread sarà strutturato come:
        invia Ping a tutti, aspetta tot ms e verifica quanti hanno risposto, magari con un campo check nella virtualView e riparte dopo aver gestito cancellazione client o set connected = false;
     */



