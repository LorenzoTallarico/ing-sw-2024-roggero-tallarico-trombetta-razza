package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Card;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

//NB: qua NON è sufficiente gestire le eccezioni con throws ma si dovrà usare try-catch correttamente
public class RmiServer implements VirtualServer {

    static int PORT = 1234;
    final GameController controller;
    final ArrayList<VirtualView> clients = new ArrayList<>();
    BlockingQueue<Integer> updatesNumber = new LinkedBlockingDeque<>();
    BlockingQueue<ArrayList<String>> updateNames = new LinkedBlockingDeque<>();

    // struttura per migliorare la comunicazione tra i client e il server, sono delle code che mi permettono di facilitare
    // la gestione degli update al client in quanto con queste è possibile ritornare prima che tutti i client abbiano ricevuto l'update
    // e inoltre gli update verranno mandati in sequenza (così le richieste possono tornare subito senza aspettare che l'update venga mandato a tutti)
    public void broadcastUpdateThread() throws InterruptedException, RemoteException {
        while(true){
            Integer updateNum = updatesNumber.take();
            ArrayList<String> names = updateNames.take();
            synchronized (this.clients){
                for(VirtualView c : clients){
                    c.showUpdateNumber(updateNum);
                    c.showUpdateNames(names);
                    System.out.println("Qui sono nel broadcast");
                }
            }
        }
    }

    public RmiServer(GameController controller){
        this.controller = controller;
    }

    public static void main(String[] args) throws RemoteException, InterruptedException {

        final String serverName = "GameServer";
        VirtualServer server = new RmiServer(new GameController());
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server,0 );
        Registry registry = LocateRegistry.createRegistry(PORT);
        registry.rebind(serverName, stub);
        System.out.println("server bound.");

        try {
            ((RmiServer)server).broadcastUpdateThread();
        }
        catch (InterruptedException e){
            System.err.println("Interrupted while waiting for updates: \n" + e.getMessage());
        }
    }

    @Override
    public void connect(VirtualView client) throws RemoteException {
        synchronized (this.clients){
            this.clients.add(client);
        }
    }

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


    /* ########## INIZIO METODI DA RIMUOVERE, UTILI SOLO AL TESTING DEL NETOWRK ############# */
    @Override
    public void addState(Integer number) throws RemoteException{
        System.err.println("add request received");
        this.controller.addState(number);
        Integer currentState = this.controller.getState();
        System.out.println("Qui prende getstate");
        // le richieste tornano subito, non aspettano che siano ricevute da tutti
        try {
            updatesNumber.put(currentState);
//            for(Integer e : updates){
//                updates.
//            }
        } catch(InterruptedException e) {
            throw new RuntimeException();
        }
    }

    @Override
    public void getNicknames() throws RemoteException {
        System.out.println("Elenco di tutti i giocatori");
        for (VirtualView v : clients){
            v.getNickname();
        }
    }


    @Override
    public void reset() throws RemoteException{
        System.err.println("reset request received");
        this.controller.reset();
        ///////////
    }
    /* ########## FINE METODI DA RIMUOVERE, UTILI SOLO AL TESTING DEL NETOWRK ############# */





}
