package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Card;
import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.model.Player;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

//NB: qua NON è sufficiente gestire le eccezioni con throws ma si dovrà usare try-catch correttamente
public class RmiServer implements VirtualServer {

    static int PORT = 1234;
    final GameController controller;
    final ArrayList<VirtualView> clients = new ArrayList<>();
    final BlockingQueue<Object> updates = new LinkedBlockingQueue<>();
    // struttura per migliorare la comunicazione tra i client e il server, sono delle code che mi permettono di facilitare
    // la gestione degli update al client in quanto con queste è possibile ritornare prima che tutti i client abbiano ricevuto l'update
    // e inoltre gli update verranno mandati in sequenza (così le richieste possono tornare subito senza aspettare che l'update venga mandato a tutti)
    public void broadcastUpdateThread() throws InterruptedException, RemoteException {
        while(true) {
            Object o = updates.take();
            synchronized (this.clients){
                for (VirtualView c : clients) {
                    c.showUpdate(o);
                }
            }
        }
    }

    public RmiServer(GameController controller){
        this.controller = controller;
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {

        final String serverName = "GameServer";
        System.out.print("> Enter desired players number: ");
        Scanner scan = new Scanner(System.in);
        int tempnum = scan.nextInt();
        //salvare nel parametro main dei model
        VirtualServer server = new RmiServer(new GameController(tempnum));
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server,0 );
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind(serverName, stub);
        System.out.println("> Server bound.");
        try {
            ((RmiServer)server).broadcastUpdateThread();
        }
        catch (InterruptedException e){
            System.err.println("> Interrupted while waiting for updates: \n" + e.getMessage());
        }
    }

    @Override
    public boolean connect(VirtualView client) throws RemoteException {
        synchronized (this.clients) {
            String nick = client.getNickname();
            System.out.println("> Adding player " + nick + ".");
            if(!clients.isEmpty())
                for(VirtualView v : this.clients) {
                    if(v.getNickname().equalsIgnoreCase(nick)) {
                        System.out.println("> Denied connection to a new client, user \"" + nick + "\" already existing.");
                        return false;
                    }
                }
            if(this.controller.getCurrPlayersNumber() == this.controller.getMaxPlayersNumber()) {
                System.out.println("> Denied connection to a new client, max number of players already reached.");
                return false;
            } else {
                this.clients.add(client);
                addPlayer(new Player(nick, false));
                return true;
            }
        }
    }

    @Override
    public void addPlayer(Player p) throws RemoteException {
        synchronized (this.clients){
            System.err.println("> Join request received");
            this.controller.addPlayer(p);
            String textUpdate = "Player " + p.getName() + " joined the game. " + this.controller.getCurrPlayersNumber() + "/" + this.controller.getMaxPlayersNumber();
            System.out.println(textUpdate);
            try {
                updates.put(textUpdate);
            } catch(InterruptedException e) {
                throw new RuntimeException();
            }
        }
    }

    @Override
    public void sendChatMessage(String msg, String author) throws RemoteException {
        Message mex = new Message(msg, author);
        mex = this.controller.sendChatMessage(mex);
        try {
            updates.put(mex);
        } catch(InterruptedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void sendChatWhisper(String msg, String author, String recipient) throws RemoteException {
        Message mex = new Message(msg, author, recipient);
        mex = this.controller.sendChatMessage(mex);
        try {
            updates.put(mex);
        } catch(InterruptedException e) {
            throw new RuntimeException();
        }
    }


    @Override
    public void getWholeChat() throws RemoteException {
        try {
            updates.put(this.controller.getWholeChat());
        } catch (InterruptedException e){
            throw new RuntimeException();
        }
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
