package it.polimi.ingsw;

import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.networking.rmi.RmiClient;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.util.Print;
import it.polimi.ingsw.networking.rmi.VirtualView;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class ClientApp extends UnicastRemoteObject implements VirtualView {


    enum State {
        COMMANDS,
        GAMESIZE,
        STARTERCARD,
        DRAW,
        PLACE,
        ACHIEVEMENTSCHOICE,
        END
    }

    //ADD OBJECT USERINTERFACE
    private static final int PORT_RMI = 6969;
    private static final int PORT_SOCKET = 7171; //it's 69 plus 2 fingers
    private Player p;
    private String nickname;
    private ClientApp.State state;
    private  VirtualServer server;
    private ArrayList<Player> allPlayers;
    private StarterCard starterCard;
    private final Print customPrint;
    private ArrayList<AchievementCard> achievements; // the first element is the secret achievement
    private ArrayList<AchievementCard> choosableAchievements;
    private ArrayList<ResourceCard> commonResource;
    private ArrayList<GoldCard> commonGold;
    private boolean goldDeck;
    private boolean resourceDeck;
    boolean repeatDraw;

    public ClientApp(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = ClientApp.State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        customPrint = new Print();
        achievements = new ArrayList<>();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {

        String AsciiArt =
                "  ______                   __                            __    __              __                                   __  __            \n"+
                " /      \\                 /  |                          /  \\  /  |            /  |                                 /  |/  |           \n"+
                "/$$$$$$  |  ______    ____$$ |  ______   __    __       $$  \\ $$ |  ______   _$$ |_    __    __   ______   ______  $$ |$$/   _______  \n"+
                "$$ |  $$/  /      \\  /    $$ | /      \\ /  \\  /  |      $$$  \\$$ | /      \\ / $$   |  /  |  /  | /      \\ /      \\ $$ |/  | /       | \n"+
                "$$ |      /$$$$$$  |/$$$$$$$ |/$$$$$$  |$$  \\/$$/       $$$$  $$ | $$$$$$  |$$$$$$/   $$ |  $$ |/$$$$$$  |$$$$$$  |$$ |$$ |/$$$$$$$/  \n"+
                "$$ |   __ $$ |  $$ |$$ |  $$ |$$    $$ | $$  $$<        $$ $$ $$ | /    $$ |  $$ | __ $$ |  $$ |$$ |  $$/ /    $$ |$$ |$$ |$$      \\  \n"+
                "$$ \\__/  |$$ \\__$$ |$$ \\__$$ |$$$$$$$$/  /$$$$  \\       $$ |$$$$ |/$$$$$$$ |  $$ |/  |$$ \\__$$ |$$ |     /$$$$$$$ |$$ |$$ | $$$$$$  | \n"+
                "$$    $$/ $$    $$/ $$    $$ |$$       |/$$/ $$  |      $$ | $$$ |$$    $$ |  $$  $$/ $$    $$/ $$ |     $$    $$ |$$ |$$ |/     $$/  \n"+
                " $$$$$$/   $$$$$$/   $$$$$$$/  $$$$$$$/ $$/   $$/       $$/   $$/  $$$$$$$/    $$$$/   $$$$$$/  $$/       $$$$$$$/ $$/ $$/ $$$$$$$/   \n";
        boolean checkChoice= false;
        Scanner scan = new Scanner(System.in);
        String line;
        StringTokenizer st;
        int connectionChoice;
        do{
            System.out.println("> Select connection method:");
            System.out.println("   [1] RMI Connection");
            System.out.println("   [2] Socket Connection");
            scan = new Scanner(System.in);
            line = scan.nextLine();
            st = new StringTokenizer(line);
            connectionChoice = Integer.parseInt(st.nextToken());
            if(connectionChoice == 1 || connectionChoice == 2)
                checkChoice = true;
        }while(!checkChoice);
        if(connectionChoice ==1){
            final String serverName = "GameServer";
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT_RMI);
            VirtualServer server = (VirtualServer) registry.lookup(serverName);
            new ClientApp(server).run();
        } else {

        }
    }

    private void run() throws RemoteException {
        System.out.print("> Enter nickname: ");
        Scanner scan = new Scanner(System.in);
        nickname = scan.nextLine();
        if(!this.server.connect(this)) {
            System.err.println("> Connection failed, max number of players already reached or name already taken.");
            System.exit(0);
        }
        p = new Player(nickname, false);
        this.runCli();
    }

    @Override
    public void reportError(String details) throws RemoteException {

    }

    @Override
    public void showAction(Action act) throws RemoteException {

    }

    @Override
    public String getNickname() throws RemoteException {
        return "";
    }

}

