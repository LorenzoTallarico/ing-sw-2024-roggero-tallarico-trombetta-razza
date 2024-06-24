
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
    private boolean connectionFlagClient = true, connectionFlagServer = true; //se incontra un problema con l'invio ai client stoppa il servizio
    private boolean gameStarted = false;
    private int playersNumber=0;
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
                /*if(!clients.isEmpty() && playersNumber==0){
                    try {
                        //actualSocket.showAction(new ErrorAction(null, "GameSize non ancora settata dal primo utente, Impossibile accedere!"));
                        actualSocket.showAction(new SetNicknameAction("No Size", false));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                synchronized (clients) {
                    actualSocket.setOnline(true);
                    actualSocket.setPing(true);
                    clients.add(actualSocket);
                }*/


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
                            if(handler.getOnline())
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
                        //this.controller.setPlayersNumber(((ChosenPlayersNumberAction) action).getPlayersNumber());
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
                                    if (client.getOnline())
                                        addPlayer(new Player(client.getNickname(), client.getGui()), client);
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
                boolean sizeRequest=false;
                if (!clients.isEmpty()) {
                    for (VirtualView v : this.clients) {
                        if (nick != null && v.getNickname().equalsIgnoreCase(nick)) {
                            System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing and now online.");
                            try {
                                client.showAction(new ErrorAction(nick, "Denied connection to a new client, user \"" + nick + "\" already existing and now online."));
                            }catch(Exception e){
                                e.printStackTrace();
                            }
                            return false;
                        }
                    }
                    if (countOnlinePlayer() >= 4) {
                        System.out.println("> Denied connection to a new client, max number of players already reached.");
                        try {
                            client.showAction(new ErrorAction(nick, "Max amount of players reached."));
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        return false;
                    }
                }
                if(clients.size() == 1 && playersNumber == 0){
                    try {
                        client.showAction(new ErrorAction(nick, "Another player has just started a game, they still haven't chosen the size of the game, wait some seconds before reconnecting."));
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    return false;
                }
                if(clients.isEmpty()){
                    sizeRequest=true;
                }
                ClientRmi c= new ClientRmi(client);
                c.setOnline(true);
                c.setPing(true);
                c.setNickname(nick);
                System.out.println("> Allowed RMI connection to a new client named \"" + nick + "\".");
                clients.add(c);
                /*
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
                }*/
                if(sizeRequest){
                    try {
                        clientActions.put(new AskingPlayersNumberAction(nick));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if(clients.size()>1) {
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
                    /*
                    int index = clients.indexOf(oldVirtualView);
                    clients.remove(index);
                    ClientRmi c= new ClientRmi(client);
                    c.setOnline(true);
                    c.setPing(true);
                    c.setNickname(nick);
                    clients.add(index, c);
                    */
                } else {
                    System.out.println("> User " + nick + " already online or doesn't exist.");
                    try {
                        client.showAction(new ErrorAction(nick, "Game started, user not found."));
                    } catch(Exception e){
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
    public void addPlayer(Player p, VirtualView c) throws RemoteException {
        synchronized (this.clients) {
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
                        //sets if player is in turn (between draw and place)
                        //c.setInTurn(((PongAction) action).getIsInTurn().equals("DRAW") || ((PongAction) action).getIsInTurn().equals("FALSE"));
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
        //HashSet<String> alreadyDisconnected = new HashSet<>();
        boolean newDisconnection = false;
        while (true) {
            boolean startActionRequired = false;
            synchronized (clients) {
                for (VirtualView c : clients) {
                    if (c.getOnline() && c.getNickname()!=null) {
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
            disconnectedClient.clear();
            synchronized (clients) {
                for (VirtualView c : clients) {
                    System.out.println("Client: " + c.getNickname() + " Online: " + c.getOnline() + " ping: " + c.getPing());
                    if (!c.getPing() && c.getOnline()){
                        System.err.println("Sono entrato in !c.getPing() && c.getOnline() ---> mi mette nella lista per disconnettere");
                        disconnectedClient.add(c.getNickname());
                        c.setOnline(false);
                        newDisconnection = true;
                    }

                }
            }

            for (String c : disconnectedClient) {
                clientActions.put(new DisconnectedPlayerAction(c, countOnlinePlayer()));
                //offlineNumber++;
                System.err.println("appena fatta put di action Disconnected, ");
               for (VirtualView v: clients){
                   if(disconnectedClient.contains(v.getNickname())){
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

                if (!disconnectedClient.isEmpty() ) {
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
            }
            else {
                for(String c : disconnectedClient){
                    System.err.println("Dentro for Virtualview nell'else di !gamestarted, elenco dei client:");
                    //System.out.println(c + ": ping --> " + c.getPing() + "         online --> " + c.getOnline());
                    if(numStarter == clients.size()){
                        controller.disconnection(c);
                        waitingRoutineOneUser();
                    }
                    else{
                        //thread 60 sec
                        for(VirtualView v: clients){
                            if(v.getNickname().equalsIgnoreCase(c)){
                                if(!v.getStarter()){
                                    waitingRoutineChoiceAchi(v.getNickname());
                                }
                            }
                        }


                    }
                }

                // game is started


                //FORSE E' MEGLIO RACCHIUDERE TUTTA STA ROBA SOTTO IN UN ALTRO METODO A PARTE (PUO' TORNARE UTILE ANCHE PER TIMER FASE INIZIALE)

                /*

                //mettere client in attesa se è l'ultimo connesso
                if(clients.size() - offlineNumber == 1){
                    VirtualView lastOnline = null;
                    for(VirtualView c : clients){
                        if(c.getOnline())
                            lastOnline = c;
                    }
                    if (lastOnline == null){
                        System.out.println("******BLOCCATO IN if(lastOnline == null)*******");
                        break;
                    }

                    if(!gameInWait){
                        //invio azione per stato WAIT
                        try {
                            Action act = new WaitAction(lastOnline.getNickname());
                            clientActions.put(act);
                        } catch (RemoteException e) {
                            System.err.println("Error sending WaitAction");
                            throw new RemoteException();
                        }
                    } //else????????????

                    gameInWait = true;
                    waitingRoutine(lastOnline);
                }
                else {
                    //il numero di players online non è più 1, ma si è connesso un altro player
                    gameInWait = false;

                }

                */


            }
        }
    }


    /*public void waitingRoutineChoiceAchi(String nickname){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            int seconds = 60;

            @Override
            public void run() {
                //NB: bisogna sincronizzare l'accesso a clients
                try {
                    if(clients.size() - countOnlinePlayer() == 0){
                        //tutti i giocatori sono online (blocca countdown)
                        timer.cancel();

                    } else {
                        //almeno un player è disconnesso
                        if (seconds > 0) {
                            System.out.println("Seconds remaining before shutdown: " + seconds);
                            seconds--;
                        } else {
                            //timer finito, butta giù tutto
                            timer.cancel();
                            //da fare eventualmente
                            shutdown();
                        }
                    }

                } catch (RemoteException e) {
                    throw new RuntimeException(e);
                }
            }

        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }*/

    public void waitingRoutineChoiceAchi(String nickname) {
        Runnable task = new Runnable() {
            int attempts = 12;

            @Override
            public void run() {
                try {
                    // NB: bisogna sincronizzare l'accesso a clients
                    while (attempts != 0) {
                        Thread.sleep(5000);
                        synchronized (clients) {
                            for (VirtualView c : clients) {
                                if (c.getNickname().equalsIgnoreCase(nickname) && c.getOnline()) {
                                    return;
                                }
                            }
                        }
                        attempts--;
                        System.out.println("Tentativi rimanenti: " + attempts);
                    }
                    shutdown("Partita finita utente non ha fatto riconessione in fase starter");
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }
    public void waitingRoutineOneUser() {
        Runnable task = new Runnable() {
            int attempts = 5;

            @Override
            public void run() {
                try {
                    // NB: bisogna sincronizzare l'accesso a clients
                    while (attempts != 0) {
                        Thread.sleep(5000);
                        if(countOnlinePlayer()<=1) {
                            attempts--;
                        }
                        System.out.println("Tentativi rimanenti: " + attempts);
                    }
                    shutdown("You Won, all Users disconnected");
                } catch (InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }

    //butta giù clients e server
    public void shutdown(String messaggio) throws IOException {
        synchronized (clients){
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



