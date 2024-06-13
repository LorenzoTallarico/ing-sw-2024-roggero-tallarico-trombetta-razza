
package it.polimi.ingsw.networkingProva;

import it.polimi.ingsw.clientProva.Client;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;


import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Iterator;
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
    private boolean connectionFlagClient = true, connectionFlagServer = true; //se incontra un problema con l'invio ai client stoppa il servizio
    private boolean gameStarted = false;

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
                VirtualView actualSocket = (VirtualView) new ClientSocket(clientSocket, serverActions, clientActions, clients, gameStarted);
                synchronized (clients) {
                    actualSocket.setOnline(true);
                    actualSocket.setPing(true);
                    clients.add(actualSocket);
                }
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
                if(!clientActions.isEmpty()) {
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
                        this.controller.setPlayersNumber(((ChosenPlayersNumberAction) action).getPlayersNumber());
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
                        if (countOnlinePlayer() > 1 && countOnlinePlayer() < 5) {
                            String firstNickname = null;
                            synchronized (clients) {
                                for (int i = 0; i < clients.size() && firstNickname == null; i++) {
                                    if (clients.get(i).getOnline()) {
                                        firstNickname = clients.get(i).getNickname();
                                    }
                                }
                            }
                            if (((StartAction) action).getAuthor().equalsIgnoreCase(firstNickname)) {
                                this.controller.setPlayersNumber(countOnlinePlayer());
                                synchronized (clients) {
                                    for (VirtualView client : clients) {
                                        if (client.getOnline())
                                            addPlayer(new Player(client.getNickname(), client.getGui()), client);
                                    }
                                }
                                System.out.println("Utenti Aggiunti al game");
                                gameStarted = true;
                            }
                        } else {
                            String nickname = null;
                            synchronized (clients) {
                                for (int i = 0; i < clients.size() && nickname == null; i++) {
                                    if (clients.get(i).getOnline()) {
                                        nickname = clients.get(i).getNickname();
                                    }
                                }
                            }
                            Action act = new AskingStartAction(nickname, countOnlinePlayer());
                            clientActions.put(act);
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
                if (!clients.isEmpty()) {
                    for (VirtualView v : this.clients) {
                        if (nick != null && v.getNickname().equalsIgnoreCase(nick)) {
                            System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing and now online.");
                            return false;
                        }
                    }
                    if (countOnlinePlayer() >= 4) {
                        System.out.println("> Denied connection to a new client, max number of players already reached.");
                        return false;
                    }
                }
                ClientRmi c= new ClientRmi(client);
                c.setOnline(true);
                c.setPing(true);
                c.setNickname(nick);
                System.out.println("> Allowed RMI connection to a new client named \"" + nick + "\".");
                clients.add(c);
                boolean startSend = false;
                for (VirtualView v : this.clients) {
                    //manda solo al primo client AskingStartAction (se sono connessi almeno 2 client)
                    if (v.getOnline() && v.getNickname() != null && !startSend && clients.size()>=2) {
                        startSend = true;
                        try {
                            clientActions.put(new AskingStartAction(v.getNickname(), countOnlinePlayer()));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
                try {
                    clientActions.put(new JoiningPlayerAction(nick, countOnlinePlayer(), 4));
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
                        serverActions.put(new ReconnectedPlayerAction(nick, oldVirtualView, client));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    int index = clients.indexOf(oldVirtualView);
                    clients.remove(index);
                    ClientRmi c= new ClientRmi(client);
                    c.setOnline(true);
                    c.setPing(true);
                    c.setNickname(nick);
                    clients.add(index, c);
                } else {
                    System.out.println("> User " + nick + " already online or doesn't exist");
                    return false;
                }
                return true;
            }
        }
    }

    private synchronized int countOnlinePlayer() throws RemoteException {
        int count = 0;
        for (VirtualView c : this.clients) {
            if (c.getOnline())
                count++;
        }
        return count;
    }

    @Override
    public void addPlayer(Player p, VirtualView c) throws RemoteException {
        synchronized (this.clients) {
            try {
                clientActions.put(new JoiningPlayerAction(p.getName(), this.controller.getCurrPlayersNumber() + 1, this.controller.getMaxPlayersNumber()));
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            this.controller.addPlayer(p, c);
            String textUpdate = "> Player " + p.getName() + " joined the game. " + this.controller.getCurrPlayersNumber() + "/" + (this.controller.getMaxPlayersNumber() == 0 ? "?" : this.controller.getMaxPlayersNumber());
            System.out.println(textUpdate);
        }
    }

    @Override
    public void sendAction(Action action) throws RemoteException {
        try {
            System.out.println("> Received action, type \"" + action.getType().toString() + "\".");
            if (action.getType().equals(ActionType.PONG)) {
                //synchronized (clients) {
                    for (VirtualView c : clients) {
                        if (c.getNickname().equalsIgnoreCase(action.getAuthor())) {
                            System.out.println("sostituito il boolean di " + action.getAuthor() + "trovato c :" + c.getNickname());
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
        while (true) {
            boolean startActionRequired = false;
            synchronized (clients) {
                for (VirtualView c : clients) {
                    if (c.getOnline()) {
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
            Thread.sleep(5000);
            synchronized (clients) {
                for(VirtualView c : clients){
                    if (!c.getPing() && c.getOnline()) {
                        System.out.println("User " + c.getNickname() + " did not respond to ping");
                        if (!gameStarted) {
                            startActionRequired = true;
                            for(VirtualView v : clients ) {
                                try {
                                    if(v.getPing() && v.getOnline())
                                        v.showAction(new DisconnectedPlayerAction(c.getNickname()));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                clients.remove(c);
                            }
                        } else {
                            c.setOnline(false);
                            controller.disconnection(c.getNickname());
                            try {
                                clientActions.put(new DisconnectedPlayerAction(c.getNickname()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
            if (startActionRequired) {
                synchronized (clients) {
                    if (countOnlinePlayer() > 0) {
                        try {
                            clients.get(0).showAction(new AskingStartAction(clients.get(0).getNickname(), countOnlinePlayer()));
                            System.out.println("Invio AskingStart");
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
        }
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



