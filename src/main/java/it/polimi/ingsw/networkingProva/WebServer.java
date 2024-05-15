package it.polimi.ingsw.networkingProva;

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
/*
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

            System.out.println("RMI Server pronto.");
        } catch (RemoteException e) {
            System.err.println("Errore durante l'avvio del server RMI: " + e.getMessage());
        }

    }
    public void startSocketServer() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT_SOCKET);
            System.out.println("Server Socket pronto.");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                VirtualView actualSocket = new ClientSocket(clientSocket, serverActions);
                //clients.add(VirtualViewSocket);
                Thread clientSocketThread = new Thread((Runnable) actualSocket); // Crea un nuovo thread per ogni client handler
                clientSocketThread.start();
                connect(actualSocket);
                //.....
                //fingiamo che è connesso


                //invio messaggio connessione accettata, e si aspetta di ricevere stesso messaggio indietro
                //client invia il suo nickname in modo da aggiornare la mappa con il proprio nickname e attende risposta
                //server thread che controlla se è arrivato a capienza con Boolean attesaDiRisposta
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
                switch (action.getType()) {
                    case CHOSENPLAYERSNUMBER:
                        this.controller.setPlayersNumber(((ChosenPlayersNumberAction)action).getPlayersNumber());
                        break;
                    case WHOLECHAT:
                        System.err.println("> Server should not receive any WHOLECHAT action.");
                        break;
                    case ASKINGCHAT:
                        newAction = new WholeChatAction(action.getAuthor(), this.controller.getWholeChat());
                        clientActions.put(newAction);
                        break;
                    case CHATMESSAGE:
                        this.controller.sendChatMessage(((ChatMessageAction)action).getMessage());
                        clientActions.put(action);
                        break;
                    case CHOSENSIDESTARTERCARD:
                        this.controller.setStarterCardSide(action.getAuthor(), ((ChosenSideStarterCardAction)action).getSide());
                        break;
                    case CHOSENACHIEVEMENT:
                        this.controller.setSecretAchievement(action.getAuthor(), ((ChosenAchievementAction)action).getAchievement());
                        break;
                    case CHOOSEABLEACHIEVEMENTS:
                        break;
                    case HAND:
                        break;
                    case PLACINGCARD:
                        if(!this.controller.placeCard(action.getAuthor(), ((PlacingCardAction)action).getCardIndex(), ((PlacingCardAction)action).getSide(), ((PlacingCardAction)action).getRow(), ((PlacingCardAction)action).getColumn())){
                            newAction = new PlacedErrorAction(action.getAuthor());
                            clientActions.put(newAction);
                        }
                        break;
                    case CHOSENDRAWCARD:
                        this.controller.drawCard(action.getAuthor(), ((ChosenDrawCardAction)action).getIndex());
                        break;
                    default:
                        break;
                }
            }  catch (InterruptedException e) {
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

    @Override
    public boolean connect(VirtualView cli) throws RemoteException {
        VirtualView client = new Client(cli);
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
*/