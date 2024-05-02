package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.action.Action;
import it.polimi.ingsw.action.ActionType;
import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.model.Player;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//NB: qua NON è sufficiente gestire le eccezioni con throws ma si dovrà usare try-catch correttamente
public class RmiServer implements VirtualServer {

    private static int PORT = 1234;
    private final GameController controller;
    private final ArrayList<VirtualView> clients = new ArrayList<>();
    private final BlockingQueue<Action> serverActions = new LinkedBlockingQueue<>();
    private final BlockingQueue<Action> clientActions = new LinkedBlockingQueue<>();
    private boolean connectionFlagServer = true, connectionFlagClient = true;

    public RmiServer(GameController controller){
        this.controller = controller;
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {

        final String serverName = "GameServer";
        VirtualServer server = new RmiServer(new GameController());
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server,0 );
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind(serverName, stub);
        System.out.println("> Server successfully started.");

        Thread threadClientQueue = new Thread(new Runnable() {
            @Override
            public void run() {
                // Code executed in the thread
                System.out.println("> Running ClientsUpdateThread ...");
                try {
                    ((RmiServer)server).clientsUpdateThread();
                } catch (InterruptedException | RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        threadClientQueue.start();

        try {
            System.out.println("> Running ServerUpdateThread ...");
            ((RmiServer)server).serverUpdateThread();
        } catch (InterruptedException e) {
            System.err.println("> ERROR: Updates queue for server interrupted:\n" + e.getMessage());
        }

    }

    public void clientsUpdateThread() throws InterruptedException, RemoteException  {
        while (connectionFlagServer) {
            try {
                Action a = clientActions.take();
                if (a.getRecipient().isEmpty()) { //to all clients
                    for(VirtualView client : clients) {
                        client.showAction(a);
                    }
                } else { //to a single client
                    for(VirtualView client : clients) {
                        if (client.getNickname().equalsIgnoreCase(a.getRecipient())) {
                            client.showAction(a);
                        }
                    }
                }
            } catch (InterruptedException e) {
                connectionFlagServer = false;
            }
        }

    }

    public void serverUpdateThread() throws InterruptedException, RemoteException {
        while(connectionFlagClient) {
            try {
                Action a = serverActions.take();
                System.out.println("> Handling action, action type \"" + a.getType().toString() + "\".");
                Action newAct = null;
                switch (a.getType()) {
                    case CHOSENPLAYERSNUMBER:
                        this.controller.setPlayersNumber((int) a.getObject());
                        break;
                    case WHOLECHAT:
                        break;
                    case ASKINGCHAT:
                        newAct = new Action(ActionType.WHOLECHAT, this.controller.getWholeChat(), null, a.getAuthor());
                        clientActions.put(newAct);
                        break;
                    case CHATMESSAGE:
                        this.controller.sendChatMessage((Message) a.getObject());
                        clientActions.put(a);
                        break;
                    case CHOSENACHIEVEMENT:
                        break;
                    case CHOOSEABLEACHIEVEMENTS:
                        break;
                    case HAND:
                        break;
                    default:
                        break;
                }
            }  catch (InterruptedException e) {
                connectionFlagClient = false;
            }
        }
    }

    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            System.err.println("> Join request received.");
            String nick = client.getNickname();
            if(!clients.isEmpty())
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
                        clientActions.put(new Action(ActionType.ASKINGPLAYERSNUMBER, null, null, nick));
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                return true;
            }
        }
    }

    public void addToClientsQueue(Action action) throws InterruptedException {
        clientActions.put(action);
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

    @Override
    public void addPlayer(Player p, VirtualView c) throws RemoteException {
        synchronized (this.clients){
            this.controller.addPlayer(p, c);
            String textUpdate = "> Player " + p.getName() + " joined the game. " + this.controller.getCurrPlayersNumber() + "/" + (this.controller.getMaxPlayersNumber() == 0 ? "?" : this.controller.getMaxPlayersNumber());
            System.out.println(textUpdate);
            try {
                clientActions.put(new Action(ActionType.JOININGPLAYER, p.getName(), null, null));
            } catch(InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public ArrayList<Player> getPlayers() throws RemoteException{
        return this.controller.getPlayers();
    }

    //da completare v
    @Override
    public void placeCard(Card card, int row, int column) throws RemoteException {
        this.controller.placeCard(card,row,column);
        // currentState = this.controller.getState();
        synchronized (this.clients){
            for(VirtualView c : this.clients){
                //c.showUpdate(currentState);
            }
        }
    }


    @Override
    public void drawCard(int index) throws RemoteException {
        this.controller.drawCard(index);
        // currentState = this.controller.getState();
        synchronized (this.clients) {
            for (VirtualView c : this.clients) {
                // c.showUpdate(currentState);
            }
        }
    }

    @Override
    public void selectAchievementCard (int position) throws RemoteException {
        this.controller.selectAchievementCard(position);
        // currentState = this.controller.getState();
        synchronized (this.clients) {
            for (VirtualView c : this.clients) {
                //c.showUpdate(currentState);
            }
        }

    }

}
