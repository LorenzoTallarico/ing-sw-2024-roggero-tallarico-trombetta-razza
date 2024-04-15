package it.polimi.ingsw.networking.rmi;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.StringTokenizer;

public class RmiClient extends UnicastRemoteObject implements VirtualView {

    static int PORT = 1234;

    String nickname;

    final VirtualServer server;

    public RmiClient(VirtualServer server) throws RemoteException{
        this.server = server;
    }

    //run() e runCli() sono specifici all'istanza del rmiClient creato, va bene anche private il metodo tanto non lo dobbiamo esporre
    private void run() throws RemoteException {
        this.server.connect(this);
        System.out.println("Inserire nickname giocatore: ");
        Scanner scan = new Scanner(System.in);
        nickname = scan.nextLine();
        this.runCli();

    }

    private void runCli() throws RemoteException{
        System.out.println("Inserire nickname giocatore: ");
        Scanner scan = new Scanner(System.in);
        nickname = scan.nextLine();

        while(true){
            System.out.println("> ");
            String line = scan.nextLine();
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken();
            if(command.equals("lista")) {
                server.getNicknames();
            } else if(command.equals("num")){
                int num = Integer.parseInt(st.nextToken());
                if(num == 0) {
                    server.reset();
                } else {
                    server.addState(num);
                }
            }
        }



//        Scanner scan = new Scanner(System.in);
//        while (true){
//            System.out.println("> ");
//            int command = scan.nextInt();
//
//            if (command == 0){
//                server.reset();
//            } else {
//                server.addState(command);
//            }
//        }

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
    @Override
    public void showUpdateNumber(Integer number) throws RemoteException {
        //synchronized ...
        System.out.println("\n= " + number + "\n> ");
    }

    @Override
    public void reportError(String details) throws RemoteException {
        System.out.println("\n[ERROR]= " + "\n> ");

    }

    @Override
    public String getNickname() throws RemoteException{
        return this.nickname;
    }
}
