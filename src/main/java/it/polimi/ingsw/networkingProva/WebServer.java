
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
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//Ricezione info in webServer e gestione di tali info, invece invio al client tutto in VirtualView
public class WebServer implements VirtualServer {
    private static int PORT_RMI = 6969;
    private static int PORT_SOCKET = 7171;
    private GameController controller = null;
    private Map<VirtualView, String> nicknamesMap = new HashMap<>();  //mappa client-nickname
    private Map<VirtualView, Boolean> onlineMap = new HashMap<>();    //mappa client-isOnline
    private Map<VirtualView, Boolean> pingMap = new HashMap<>();      //mappa client-ping
    private ArrayList<VirtualView> clients = new ArrayList<>();
    private BlockingQueue<Action> serverActions = new LinkedBlockingQueue<>(); //Action arrivate da Client
    private BlockingQueue<Action> clientActions = new LinkedBlockingQueue<>(); //Action da mandare a Client
    private boolean connectionFlagClient = true, connectionFlagServer = true; //se incontra un problema con l'invio ai client stoppa il servizio
    private VirtualView rmiServer;
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
        this.clients = clients;
    }

    public void startRmiServer() {
        //Creazione RmiServer
        try {
            int rmiPort = PORT_RMI;
            int socketPort = PORT_SOCKET;
            int[] ports = {rmiPort, socketPort};


            final String serverName = "GameServer";

            //VirtualServer server = new RmiServer(new GameController);
            VirtualServer server = this; //al posto di GameController
            //VirtualServer server = this; //al posto di GameController
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
            // ServerSocket è una classe predefinita di java, dovremmo cambiare nome perché si crea confusione
            //ServerSocket serverSocket = new ServerSocket(PORT_SOCKET);
            System.out.println("Server Socket ready.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                //qui si è attaccato un nuovo client
                VirtualView actualSocket = (VirtualView) new ClientSocket(clientSocket, serverActions, clientActions, clients, pingMap);
                clients.add(actualSocket);
                onlineMap.put(actualSocket, Boolean.TRUE);
                nicknamesMap.put(actualSocket, actualSocket.getNickname());
                Thread clientSocketThread = new Thread((Runnable) actualSocket); // Crea un nuovo thread per ogni client handler
                clientSocketThread.start();
            }
        } catch (IOException e) {
            System.err.println("Errore durante l'avvio del server Socket: " + e.getMessage());
        }
    }

    public void clientsUpdateThread() throws InterruptedException, RemoteException {
        // Invia l'azione a tutti i client appena c'è
        try {
            while (connectionFlagClient) {
                Action a = clientActions.take();
                for (VirtualView handler : clients) {
                    handler.showAction(a);
                }
            }
        } catch (InterruptedException | IOException e) {
            // Gestione dell'eccezione
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
                        if (countOnlinePlayer() > 1 && countOnlinePlayer() < 5) {//(((StartAction) action).getPlayerNumber() == countOnlinePlayer()){
                            String firstNickname = null;
                            for (int i = 0; i < clients.size() && firstNickname == null; i++) {
                                if (clients.get(i).getOnline()) {
                                    firstNickname = clients.get(i).getNickname();
                                }
                            }
                            if (((StartAction) action).getAuthor().equalsIgnoreCase(firstNickname)) {
                                this.controller.setPlayersNumber(countOnlinePlayer());
                                for (VirtualView client : clients) {
                                    if (client.getOnline())
                                        addPlayer(new Player(client.getNickname(), client.getGui()), client);
                                }
                                System.out.println("Utenti Aggiunti al game");
                                gameStarted = true;
                            }
                        } else {
                            String nickname = null;
                            for (int i = 0; i < clients.size() && nickname == null; i++) {
                                if (clients.get(i).getOnline()) {
                                    nickname = clients.get(i).getNickname();
                                }
                            }
                            Action act = new AskingStartAction(nickname, countOnlinePlayer());
                            clientActions.put(act);
                        }
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
        //VirtualView client = new Client(cli);
        synchronized (this.clients) {
            System.err.println("> Join request received.");
            String nick = client.getNickname();
            if (!clients.isEmpty())
                //Da gestire anche in Socket, aggiungere nome a VirtualViewSocket
                for (VirtualView v : this.clients) {
                    if (v.getNickname().equalsIgnoreCase(nick)) {
                        System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing.");
                        return false;
                    }
                }
            if (countOnlinePlayer() >= 4) {
                System.out.println("> Denied connection to a new client, max number of players already reached.");
                return false;
            } else {
                clients.add((VirtualView) client);
                onlineMap.put(client, Boolean.TRUE);
                System.out.println(onlineMap.get(client).toString());
                for(VirtualView c : onlineMap.keySet())
                    System.out.println(c.getNickname());
                nicknamesMap.put(client, nick);
                System.out.println("> Allowed RMI connection to a new client named \"" + nick + "\".");
                for (VirtualView v : this.clients) {
                    if (v.getOnline() && v.getNickname() != null) {
                        Action act = new AskingStartAction(v.getNickname(), countOnlinePlayer());
                        try {
                            clientActions.put(act);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        act = new JoiningPlayerAction(nick, countOnlinePlayer() , 4);
                        try {
                            clientActions.put(act);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                return true;
            }
        }
    }


    private int countOnlinePlayer() throws RemoteException {
        int count = 0;
        for (VirtualView v : this.clients) {
            if (onlineMap.get(v).booleanValue())
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
                throw new RuntimeException();
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
                for (VirtualView c : nicknamesMap.keySet()) {
                    if (nicknamesMap.get(c).equals(action.getAuthor())) {
                        pingMap.replace(c, Boolean.TRUE);
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
        Action act;
        while (true) {
            boolean startActionRequired = false;
            pingMap.clear();
            for (VirtualView c : onlineMap.keySet()) {
                if (onlineMap.get(c).equals(Boolean.TRUE))
                    pingMap.put(c, Boolean.FALSE);
            }
            for (VirtualView c : pingMap.keySet()){
                System.out.println("- Chiave");
                c.showAction(new PingAction());
            }
            Thread.sleep(5000);
            for (VirtualView c : pingMap.keySet()) {
                if (pingMap.get(c).equals(Boolean.FALSE)) {
                    //Entriamo se non ha pingato c
                    if (!gameStarted) {
                        //Gioco non startato, client in fase di connessione
                        startActionRequired = true;
                        for (VirtualView v : pingMap.keySet()) {
                            act = new DisconnectedPlayerAction(nicknamesMap.get(v));
                            clientActions.put(act);
                        }
                    }
                    clients.remove(c);
                    onlineMap.remove(c);
                    nicknamesMap.remove(c);
                } else {
                    //gioco già startato
                    onlineMap.replace(c, Boolean.FALSE);
                }
            }
            if (startActionRequired) {
                if (clients.get(0) != null) {
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
    /*
    checkAliveThread()
        se il gameStarted == false => l'utente che non risulta connesso viene eliminato e invio messaggio notificaDisconnessione e askingStart
        se il gameStarted == true => setto solo il boolean connected == false e sarà da gestire la ricezione e invio e salto turno
        per riconessione => modifica connection se gameStarted == true allora verra creata nuova VirtualView che verrà rimpiazzata e inviati tutti i dati della partita tramite un action
        il thread sarà strutturato come:
        invia Ping a tutti, aspetta tot ms e verifica quanti hanno risposto, magari con un campo check nella virtualView e riparte dopo aver gestito cancellazione client o set connected = false;
     */



