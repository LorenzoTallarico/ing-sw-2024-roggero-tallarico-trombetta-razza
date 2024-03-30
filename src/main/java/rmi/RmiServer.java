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

public class RmiServer implements VirtualServer{

    final GameController controller;
    final ArrayList<VirtualView> clients = new ArrayList<>();
    public RmiServer(GameController controller){
        this.controller = controller;
    }


    public static void main(String[] args) throws RemoteException{



        final String serverName = "gameServer";
        VirtualServer server = new RmiServer(new GameController());
        VirtualServer stub = (VirtualServer) UnicastRemoteObject.exportObject(server,0 );
        Registry registry = LocateRegistry.createRegistry(1234);
        registry.rebind(serverName, stub);
        System.out.println("server bound.");
    }

    @Override
    public void connect(VirtualView client) throws RemoteException {
        this.clients.add(client);
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
    }
