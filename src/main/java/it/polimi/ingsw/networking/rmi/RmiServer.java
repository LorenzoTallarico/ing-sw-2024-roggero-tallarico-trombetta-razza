package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.networking.action.toclient.AskingPlayersNumberAction;
import it.polimi.ingsw.networking.action.toclient.JoiningPlayerAction;
import it.polimi.ingsw.networking.action.toclient.PlacedErrorAction;
import it.polimi.ingsw.networking.action.toclient.WholeChatAction;
import it.polimi.ingsw.networking.action.toserver.*;

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
        while (connectionFlagClient) {
            try {
                Action a = clientActions.take();
                for(VirtualView client : clients) {
                    client.showAction(a);
                }/* REMOVED - USE ONLY IF DON'T WANT CLIENTS RECEIVING ALL ACTIONS
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
                }*/
            } catch (InterruptedException e) {
                connectionFlagClient = false;
            }
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
                        if(!this.controller.placeCard(((PlacingCardAction)action).getCard(), ((PlacingCardAction)action).getSide(), ((PlacingCardAction)action).getRow(), ((PlacingCardAction)action).getColumn())){
                            newAction = new PlacedErrorAction(action.getAuthor());
                            clientActions.put(newAction);
                        }
                        break;
                    default:
                        break;
                }
            }  catch (InterruptedException e) {
                connectionFlagServer = false;
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


}
