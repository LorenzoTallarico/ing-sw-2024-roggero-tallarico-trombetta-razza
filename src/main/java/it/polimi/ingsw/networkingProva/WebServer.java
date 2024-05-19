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
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//Ricezione info in webServer e gestione di tali info, invece invio al client tutto in VirtualView
public class WebServer implements VirtualServer {
    private static int PORT_RMI= 6969;
    private static int PORT_SOCKET= 7171;
    private GameController controller = null;
    private final ArrayList<VirtualView> clients = new ArrayList<>();
    private final BlockingQueue<Action> serverActions = new LinkedBlockingQueue<>(); //Action arrivate da Client
    private final BlockingQueue<Action> clientActions = new LinkedBlockingQueue<>(); //Action da mandare a Client
    private boolean connectionFlagClient = true, connectionFlagServer = true; //se incontra un problema con l'invio ai client stoppa il servizio
    private VirtualView rmiServer;
    private boolean gameStarted = false;

    public WebServer(int[] ports) {
        PORT_RMI = ports[0];
        PORT_SOCKET = ports[1];
        start();
    }
    
    public WebServer(GameController controller, int[] ports){
        this.controller = controller;
        PORT_RMI = ports[0];
        PORT_SOCKET = ports[1];
    }

    public void startRmiServer() {
        //Creazione RmiServer
        try {
            final String serverName = "GameServer";

            //VirtualServer server = new RmiServer(new GameController);
            VirtualServer server = this; //al posto di GameController
            VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server, 0);
            Registry registry = LocateRegistry.createRegistry(PORT_RMI);
            registry.rebind(serverName, stub);
            System.out.println("RMI Server ready.");

        } catch (RemoteException e) {
            System.err.println("Error during the start of the RMI server: " + e.getMessage());
        }

    }
    public void startSocketServer() {
        try {
            // ServerSocket è una classe predefinita di java, dovremmo cambiare nome perché si crea confusione
            ServerSocket serverSocket = new ServerSocket(PORT_SOCKET);
            System.out.println("Server Socket ready.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                //qui si è attaccato un nuovo client
                VirtualView actualSocket = new ClientSocket(clientSocket, serverActions, clients);
                clients.add(actualSocket);
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
        while(connectionFlagServer) {
            try {
                Action action = serverActions.take();
                System.out.println("> Handling action, action type \"" + action.getType().toString() + "\".");
                Action newAction = null;
                if (gameStarted) {
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
                        default:
                            break;
                    }
                }
                else{
                    switch (action.getType()) {
                        case START:
                            if(((StartAction) action).getPlayerNumber() == countOnlinePlayer()){
                                this.controller.setPlayersNumber(((StartAction) action).getPlayerNumber());
                                for(VirtualView client : clients){
                                    addPlayer(new Player(client.getNickname(), false), client);
                                }
                                gameStarted = true;
                            }
                            else{
                                String nickname = null;
                                for(int i=0; i<clients.size() && nickname == null; i++) {
                                    if(clients.get(i).getOnline()) {
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
                }
                } catch(InterruptedException e){
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
        Thread serverUpdateThread = new Thread(clientsUpdateRunnable);
        serverUpdateThread.start();
    }

   /* @Override
    public boolean connect(VirtualView client) throws RemoteException {
        //VirtualView client = new Client(cli);
        synchronized (this.clients) {
            System.err.println("> Join request received.");
            String nick = client.getNickname();
            if(!clients.isEmpty())
                //Da gestire anche in Socket, aggiungere nome a VirtualViewSocket
                for(VirtualView v : this.clients) {
                    if(v.getNickname().equalsIgnoreCase(nick)) {
                        System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing.");
                        return false;
                    }
                }
            if(this.controller.getCurrPlayersNumber() != 0 && this.controller.getCurrPlayersNumber() == this.controller.getMaxPlayersNumber()) {
                System.out.println("> Denied connection to a new client, max number of players already reached.");
                return false;
            } else {
                this.clients.add(client);
                System.out.println("> Allowed connection to a new client named \"" + nick + "\".");
                addPlayer(new Player(nick, false), client);
                if(this.controller.getCurrPlayersNumber() == 1) {
                    try {
                        System.out.println("> " + nick + " is the first player.");
                        clientActions.put(new AskingPlayersNumberAction(nick));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }
        }
    }*/

    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        //VirtualView client = new Client(cli);
        synchronized (this.clients) {
            System.err.println("> Join request received.");
            String nick = client.getNickname();
            if(!clients.isEmpty())
                //Da gestire anche in Socket, aggiungere nome a VirtualViewSocket
                for(VirtualView v : this.clients) {
                    if(v.getNickname().equalsIgnoreCase(nick)) {
                        System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing.");
                        return false;
                    }
                }
            if(countOnlinePlayer()==4) {
                System.out.println("> Denied connection to a new client, max number of players already reached.");
                return false;
            } else {
                this.clients.add(client);
                System.out.println("> Allowed connection to a new client named \"" + nick + "\".");
                for(VirtualView v : this.clients) {
                    if(v.getOnline()) {
                        Action act = new AskingStartAction(v.getNickname(), countOnlinePlayer());
                        return true;
                    }
                }
                return true;
            }
        }
    }


    private int countOnlinePlayer() throws RemoteException {
        int count = 0;
        for(VirtualView v : this.clients) {
            if(v.getOnline() && v.getNickname() != null)
                count ++;
        }
        return count;
    }

    @Override
    public void addPlayer(Player p, VirtualView c) throws RemoteException {
        synchronized (this.clients){
            try {
                clientActions.put(new JoiningPlayerAction(p.getName(), this.controller.getCurrPlayersNumber() + 1, this.controller.getMaxPlayersNumber()));
            } catch(InterruptedException e) {
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
            System.out.println("> Received action, type \"" + action.getType().toString() +"\".");
            serverActions.put(action);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

}
