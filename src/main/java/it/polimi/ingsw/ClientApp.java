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
/*
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
    private static final int PORTRMI = 6969;
    private static final int PORTSOCKET = 7171; //it's 69 plus 2 fingers
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

    public static void main(String[] args) throws RemoteException, NotBoundException {

        final String serverName = "GameServer";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        new RmiClient(server).run();
    }


}

 */