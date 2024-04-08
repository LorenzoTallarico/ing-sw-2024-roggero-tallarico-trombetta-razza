package rmi;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.model.Card;

import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

//NB: qua NON è sufficiente gestire le eccezioni con throws ma si dovrà usare try-catch correttamente
public class RmiServer implements VirtualServer {

    final GameController controller;
    final ArrayList<VirtualView> clients = new ArrayList<>();
    BlockingQueue<Integer> updates = new LinkedBlockingDeque<>();
    // struttura per migliorare la comunicazione tra i client e il server, sono delle code che mi permettono di facilitare
    // la gestione degli update al client in quanto con queste è possibile ritornare prima che tutti i client abbiano ricevuto l'update
    // e inoltre gli update verranno mandati in sequenza (così le richieste possono tornare subito senza aspettare che l'update venga mandato a tutti
    private void broadcastUpdateThread() throws InterruptedException, RemoteException {
        while(true){
            Integer update = updates.take();
            synchronized (this.clients){
                for(VirtualView c : clients){
                    c.showUpdate(update);
                }
            }
        }
    }

    public RmiServer(GameController controller){
        this.controller = controller;
    }

    public static void main(String[] args) throws RemoteException{

        final String serverName = "GameServer";
        VirtualServer server = new RmiServer(new GameController());
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server,0 );
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(serverName, stub);
        System.out.println("server bound.");
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
        try {
            updates.put(currentState);
        } catch(InterruptedException e) {
            throw new RuntimeException();
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
