package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.model.Player;

import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RmiClient extends UnicastRemoteObject implements VirtualView {

    static int PORT = 1234;

    Player p;

    String nickname;

    final VirtualServer server;

    public RmiClient(VirtualServer server) throws RemoteException{
        this.server = server;
        this.p = new Player();
        this.nickname = "";
    }

    //run() e runCli() sono specifici all'istanza del rmiClient creato, va bene anche private il metodo tanto non lo dobbiamo esporre
    private void run() throws RemoteException {
        if(!this.server.connect(this)) {
            System.err.println("> Connection failed, max number of players already reached.");
            System.exit(0);
        }
        System.out.print("> Enter nickname: ");
        Scanner scan = new Scanner(System.in);
        nickname = scan.nextLine();
        p = new Player(nickname, false);
        //
        //System.out.println("CLIENT 39 - nome del player: " + p.getName() + " is null? " + (p == null));
        server.addPlayer(p);
        this.runCli();
    }

    private void runCli() throws RemoteException{
        Scanner scan = new Scanner(System.in);
        while(true){
            String line = scan.nextLine();
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken();
            command.toLowerCase();
            switch(command) {
                case "chat":
                    if(line.length() > 5)
                      server.sendChatMessage(line.substring(5), nickname);
                    break;
                case "getchat":
                    server.getWholeChat();
                    break;
                default:
                    System.out.println("Command unknown");
            }
        }
    }


    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        new RmiClient(server).run();
    }

    //siamo un remote object quindi possono arrivare anche invocazioni remote (qui sotto mostriamo i cambiamenti)
    //NB:   Qui vanno gestite le sincronizzazioni dei thread!
    //      Infatti se consideriamo il gioco può essere che l'utente interagisca con la view e faccia altro mentre siamo in questo metodo

    public void showUpdate(Object o) throws RemoteException {
        //synchronized...
        if(o.getClass().equals(Player.class)){
            System.out.println("> Player " + ((Player) o).getName() + " joined the game.\u001B[0m");
        } else if(o.getClass().equals(String.class)){
            System.out.println("> "+((String) o));
        } else if(o.getClass().equals(Message.class)) {
            System.out.println("\033[1m" + ">>> " + o.toString() + "\033[0m");
        } else if(o.getClass().equals(ArrayList.class)) {
            ArrayList<Message> chat = ((ArrayList<Message>) o);
            for(Message m : chat){
                System.out.println("\033[1m" + ">>> " + m.toString() + "\033[0m");
            }
        } else {
            System.err.println("showUpdate error");
        }
        //else if o switch....
    }

    @Override
    public void reportError(String details) throws RemoteException {
        System.out.println("\n[ERROR]= " + "\n> ");

    }

}
