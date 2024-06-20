
package it.polimi.ingsw.clientProva;

import it.polimi.ingsw.gui.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.ChatMessageAction;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.networking.rmi.RmiClient;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.networkingProva.ClientRmi;
import it.polimi.ingsw.util.Print;
import it.polimi.ingsw.gui.GUIView;
import it.polimi.ingsw.gui.PlayController;
import it.polimi.ingsw.gui.LoginController;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.net.SocketOption;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client extends UnicastRemoteObject implements VirtualView {

    enum State {
        COMMANDS,
        GAMESIZE,       //lo uso come stato quando si deve chiamare "startgame"
        STARTERCARD,
        DRAW,
        PLACE,
        ACHIEVEMENTSCHOICE,
        WAIT,   //QAUNDO RIMANE SOLO 1 CLIENT CONNESSO (DEVE PARTIRE UN TIMER???)
        END
    }

    private final static int PORT = 6969;
    private Player p;
    private String nickname;
    private Client.State state;
    private Client.State stateBeforeDisconnection;
    private VirtualServer server;
    private ArrayList<Player> allPlayers;
    private StarterCard starterCard;
    private ArrayList<AchievementCard> achievements; // the first element is the secret achievement
    private ArrayList<AchievementCard> choosableAchievements;
    private ArrayList<ResourceCard> commonResource;
    private ArrayList<GoldCard> commonGold;
    private Resource goldDeck;
    private Resource resourceDeck;
    boolean repeatDraw;
    private BlockingQueue<Action> serverActionsReceived = new LinkedBlockingQueue<>(); //Action arrivate da Server
    private BlockingQueue<Action> clientActionsToSend = new LinkedBlockingQueue<>(); //Action da mandare Server
    private boolean gui;
    private boolean connected = false;
    private boolean connectionFlagServer=true;
    private boolean connectionFlagClient=true; //da sistemare

    private GUIView guiView;
    private LoginController loginController;
    private PlayController playController;
    private Thread threadChatListener, threadStartListener, threadAchievementListener, threadPlaceListener, threadRePlaceListener, threadDrawListener;



    /*public Client(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = Client.State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        achievements = new ArrayList<>();
    }*/

    public Client (int connectionChoice, int portChoice, String ip, boolean gui, String nickname)  throws IOException, NotBoundException {
        this.p = new Player();
        this.nickname = nickname;
        //check
        state = Client.State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        achievements = new ArrayList<>();
        //connection
        if(connectionChoice == 1){ //RMI
            // new RmiClient(server).init();
            final String serverName = "GameServer";
            try {
                Registry registry = LocateRegistry.getRegistry(ip, portChoice);
                VirtualServer server = (VirtualServer) registry.lookup(serverName);
                this.server = server;
            } catch (UnknownHostException e) {
                System.err.println("\nWrong ip address or port");
                System.exit(0);
            }
        } else { //Socket
            try {
                Socket socket = new Socket(ip, portChoice);
                VirtualServer serverSocket = (VirtualServer) new ServerSocket(socket, serverActionsReceived);
                this.server = serverSocket;
                Thread serverSocketThread = new Thread((Runnable) serverSocket); // Crea un nuovo thread di ascolto per i messaggi in arrivo dal server
                serverSocketThread.start();
            } catch (java.net.UnknownHostException e) {
                System.err.println("\nWrong ip address or port");
                System.exit(0);
            }
        }
        //gui
        this.gui = gui;
        if(gui) {
            guiView = new GUIView();
            try {
                guiView.init();
            } catch (Exception e) {
                System.out.println("!!! ERROR INIT GUI-VIEW !!!");
                throw new RuntimeException(e);
            }
            Platform.startup(() -> {
                Stage stage = new Stage();
                try {
                    guiView.start(stage);
                } catch (IOException e) {
                    System.out.println("!!! ERROR START GUI-VIEW !!!");
                    throw new RuntimeException(e);
                }
            });
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP GETCONTROLLER !!!");
                }
            } while (guiView.getLoginController() == null);
            loginController = guiView.getLoginController();
            Platform.runLater(() -> loginController.setNickname(nickname));
        }
        //update threads
        new Thread(() -> {
            try {
                clientUpdateThread();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        new Thread(() -> {
            try {
                serverUpdateThread();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
        // connecting
        finalizeConnection(connectionChoice);

    }

    private void finalizeConnection(int connectionChoice) throws RemoteException {
        if (connectionChoice == 1) {
            if(!this.server.connect(this)) {
                // connection / reconnection RMI
                if(gui) {
                    Platform.runLater(loginController::invalidNickname);
                    Platform.exit();
                } else {
                    System.err.println("> Connection failed, max number of players already reached or name already taken.");
                }
                System.exit(0);
            }
            connected = true;
            if(gui) {
                Platform.runLater(loginController::waitForOtherPlayers);
            } else {
                System.out.println("> Login successful " + nickname);
            }
        } else {
            // connection / reconnection Socket
            Action act = new SetNicknameAction(nickname, gui);
            server.sendAction(act);
        }
        if(!gui)
            runCommandLine();
    }

   /* public void init(int port, String nickname, boolean gui) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        new RmiClient(server).run();
    }*/

   /* private void run() throws RemoteException {
        System.out.print("> Enter nickname: ");
        Scanner scan = new Scanner(System.in);
        nickname = scan.nextLine();
        if(!this.server.connect(this)) { //si puo dividere le casistiche facendo tornare un int
            System.err.println("> Connection failed, max number of players already reached or name already taken.");
            System.exit(0);
        }
        p = new Player(nickname, false);
        this.runCli();
    }*/


    private void runCommandLine() throws RemoteException {
        // Start runCli Thread
        new Thread(() -> {
            try {
                runCli();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runCli() throws RemoteException, InterruptedException {
        Scanner scan = new Scanner(System.in);
        Message msg;
        Action a;

        while(connectionFlagClient) {
            String line = scan.nextLine();
            if (line.trim().isEmpty())
                continue;
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken().toLowerCase();
            switch (command) {
                case "gamesize":
                    if (state == Client.State.GAMESIZE) {
                        try {
                            int playnum = Integer.parseInt(st.nextToken());
                            while(playnum < 2 || playnum > 4) {
                                System.out.print("> The number must be between 2 and 4: ");
                                playnum = Integer.parseInt(scan.nextLine());
                            }
                            a = new ChosenPlayersNumberAction(playnum);
                            clientActionsToSend.put(a);
                            System.out.println("> Game's size set to " + playnum + ".");
                            state = Client.State.COMMANDS;
                        } catch (NoSuchElementException | NumberFormatException e) {
                            System.out.println("> Invalid command syntax.");
                            continue;
                        }
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, game's size already set." + Print.ANSI_RESET);
                    }
                    break;
                case "startgame":
                    if(state.equals(Client.State.GAMESIZE)){
                        a = new StartAction(nickname);
                        clientActionsToSend.put(a);
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't start a game." + Print.ANSI_RESET);
                    }
                    break;
                case "chat":
                    if (line.length() > 5) {
                        msg = new Message(line.substring(5), nickname);
                        a = new ChatMessageAction(nickname, null, msg);
                        clientActionsToSend.put(a);
                    }
                    break;
                case "getchat":
                    a = new AskingChatAction(nickname);
                    clientActionsToSend.put(a);
                    break;
                case "whisper":
                    command = st.nextToken();
                    msg = new Message(line.substring(7 + command.length() + 1), nickname, command);
                    a = new ChatMessageAction(nickname, command, msg);
                    clientActionsToSend.put(a);
                    System.out.println(Print.ANSI_BOLD + ">>> PRIVATE to " + msg.getRecipient() + " > " + msg.toString() + Print.ANSI_BOLD_RESET);
                    break;
                case "help":
                    System.out.println("> ------- COMMANDS LIST -------");
                    System.out.printf("> %-30s%s\n","chat [...]","to send a public message to everyone.");
                    System.out.printf("> %-30s%s\n","whisper [x] [...]","to send a private message to x.");
                    System.out.printf("> %-30s%s\n","getchat","to retrieve a full log of the public chat.");
                    System.out.printf("> %-30s%s\n","startgame","to start a new game with the players now connected.");
                    //System.out.printf("> %-30s%s\n","gamesize [x]","to choose the number x of players who will play the game.");
                    System.out.printf("> %-30s%s\n","start","to choose the side of the starter card.");
                    System.out.printf("> %-30s%s\n","achievement","to choose your secret achievement");
                    System.out.printf("> %-30s%s\n","place","to place a card from your hand.");
                    System.out.printf("> %-30s%s\n","draw","to draw a new card.");
                    System.out.printf("> %-30s%s\n","playground x","to look at the playground of the player x, if you leave x blank, it will show yours.");
                    System.out.printf("> %-30s%s\n","hand","to look at the cards you have in your hand and the achievements you shall fulfill.");
                    System.out.printf("> %-30s%s\n","table","to look at the cards and the decks in the middle of the table.");
                    System.out.printf("> %-30s%s\n","list","to know the full list of players currently playing and their score.");
                    System.out.printf("> %-30s%s\n","scoreboard","to look at the scoreboard.");
                    break;
                case "hand":
                    System.out.println("> This is your hand:");
                    Print.largeHandPrinter(p.getHand(), achievements);
                    break;
                case "place":
                    //qui quando si riconnette il client state si ha "this.state = null"
                    if(state.equals(Client.State.PLACE)) {
                        System.out.println("> This is your playground:");
                        Print.playgroundPrinter(p.getArea());
                        System.out.println("> This is your hand:");
                        Print.largeHandPrinter(p.getHand(), achievements);
                        boolean checkIndex = false;
                        boolean checkSide = false;
                        boolean chosenSide = false;
                        int index = 0;
                        String side;
                        int row = 0;
                        int column = 0;
                        do {
                            System.out.print("> Enter the number of the card, the side you prefer, the row and the column where you want to place the card." +
                                    "\n> Follow this order [card] [side] [row] [column]: ");
                            line = scan.nextLine();
                            try {
                                st = new StringTokenizer(line);
                                index = (Integer.parseInt(st.nextToken())) - 1;
                                side = st.nextToken();
                                row = Integer.parseInt(st.nextToken());
                                column = Integer.parseInt(st.nextToken());
                            } catch (NoSuchElementException | NumberFormatException e) {
                                continue;
                            }
                            if(index >= 0 && index <= p.getHand().size())
                                checkIndex = true;
                            else
                                System.out.println("> Index of the card not valid, enter a number between 1 and " + p.getHand().size() + ".");
                            if(side.equals("1") || side.equalsIgnoreCase("f") || side.equalsIgnoreCase("front")) {
                                checkSide = true;
                                chosenSide = true;
                            } else if(side.equals("0") || side.equalsIgnoreCase("b") || side.equalsIgnoreCase("back")) {
                                checkSide = true;
                            } else {
                                System.out.println("> Side not valid, enter \"1\" / \"F\" / \"Front\" for the front side or \"0\" / \"B\" / \"Back\" for the back side.");
                            }
                        } while(!checkIndex && !checkSide);
                        a = new PlacingCardAction(index, chosenSide, row, column, nickname);
                        clientActionsToSend.put(a);
                        state = Client.State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't place a card right now." + Print.ANSI_RESET);
                    }
                    break;
                case "start":
                    if(state.equals(Client.State.STARTERCARD)) {
                        System.out.println(Print.ANSI_BOLD + "      Front side           Back side      " + Print.ANSI_BOLD_RESET);
                        Print.largeCardBothSidesPrinter(starterCard);
                        do {
                            System.out.print("> Enter \"front\" or \"back\": ");
                            line = scan.nextLine();
                        } while(!(line.equalsIgnoreCase("front") || (line.equalsIgnoreCase("f"))
                                || (line.equalsIgnoreCase("back")) || (line.equalsIgnoreCase("b"))
                                || (line.equalsIgnoreCase("1")) || (line.equalsIgnoreCase("0"))));
                        if(line.equalsIgnoreCase("front") || line.equalsIgnoreCase("f") ||  line.equalsIgnoreCase("1"))
                            starterCard.setFront(true);
                        else
                            starterCard.setFront(false);
                        server.sendAction(new ChosenSideStarterCardAction(nickname, starterCard.isFront()));
                        p.setArea(new Playground());
                        p.getArea().setSpace(starterCard, 40, 40);
                        state = Client.State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't choose the starter card right now." + Print.ANSI_RESET);
                    }
                    break;
                case "achievement":
                    if(state.equals(Client.State.ACHIEVEMENTSCHOICE)) {
                        System.out.println(Print.ANSI_BOLD + "      1° Option               2° Option      " + Print.ANSI_BOLD_RESET);
                        Print.inLineAchievementPrinter(choosableAchievements);
                        int achChoice = 0;
                        while(achChoice < 1 || achChoice > 2) {
                            System.out.print("> Enter \"1\" or \"2\" to choose your secret achievement: ");
                            try {
                                achChoice = Integer.parseInt(scan.nextLine());
                            } catch (NoSuchElementException | NumberFormatException ignored) { }
                        }
                        a = new ChosenAchievementAction(nickname, achChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1));
                        achievements.add(0, achChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1));
                        clientActionsToSend.put(a);
                        System.out.println("> Waiting for other players to choose their starter card and secret achievement.");
                        state = Client.State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't choose the secret achievement right now." + Print.ANSI_RESET);
                    }
                    break;
                case "draw":
                    if(state.equals(Client.State.DRAW)) {
                        Print.drawChoicePrinter(commonGold, commonResource, goldDeck,resourceDeck);
                        int drawChoice = 0;
                        do {
                            repeatDraw = false;
                            try {
                                if (goldDeck != null && resourceDeck != null) {
                                    System.out.print("> Enter 1, 2, 3, 4, 5 or 6 to draw your card: ");
                                    drawChoice = Integer.parseInt(scan.nextLine());
                                    if (drawChoice < 1 || drawChoice > 6) {
                                        System.out.println("> Please enter a valid number");
                                        repeatDraw = true;
                                    }
                                } else if (goldDeck == null && resourceDeck != null) {
                                    System.out.print("> Enter 1, 2, 3, 4 or 6 to draw your card: ");
                                    drawChoice = Integer.parseInt(scan.nextLine());
                                    if (drawChoice < 1 || drawChoice > 6 || drawChoice == 5) {
                                        System.out.println("> Please enter a valid number");
                                        repeatDraw = true;
                                    }
                                } else if (goldDeck != null && resourceDeck == null) {
                                    System.out.print("> Enter 1, 2, 3, 4 or 5 to draw your card: ");
                                    drawChoice = Integer.parseInt(scan.nextLine());
                                    if (drawChoice < 1 || drawChoice > 5) {
                                        System.out.println("> Please enter a valid number");
                                        repeatDraw = true;
                                    }
                                } else if (goldDeck == null && resourceDeck == null) {
                                    System.out.print("> Enter 1, 2, 3 or 4 to draw your card: ");
                                    drawChoice = Integer.parseInt(scan.nextLine());
                                    if (drawChoice < 1 || drawChoice > 4) {
                                        System.out.println("> Please enter a valid number.");
                                        repeatDraw = true;
                                    }
                                }
                            } catch (NoSuchElementException | NumberFormatException e) {
                                repeatDraw = true;
                            }
                        } while(repeatDraw);
                        a = new ChosenDrawCardAction(nickname, drawChoice);
                        clientActionsToSend.put(a);
                        state = Client.State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't draw a card right now." + Print.ANSI_RESET);
                    }
                    break;
                case "playground":
                    if(line.trim().equalsIgnoreCase("playground")) {
                        System.out.println("> This is your playground:");
                        Print.playgroundPrinter(p.getArea());
                    } else {
                        boolean tempCheck = false;
                        command = st.nextToken();
                        for(Player player : allPlayers) {
                            if(player.getName().equalsIgnoreCase(command)) {
                                tempCheck = true;
                                System.out.println("> This is " + player.getName() + "'s playground:");
                                Print.playgroundPrinter(player.getArea());
                            }
                        }
                        if(!tempCheck) {
                            if (command.equalsIgnoreCase(nickname)) {
                                System.out.println("> This is your playground:");
                                Print.playgroundPrinter(p.getArea());
                            } else
                                System.out.println("> " + command + " is not playing in this game.");
                        }
                    }
                    break;
                case "list":
                    System.out.println("> -------- Players list --------");
                    for(Player player : allPlayers)
                        System.out.println("> " + Print.getPlayerColor(player.getName(), allPlayers, p) + player.getName() + ": " + player.getPoints() + " pts;" + Print.ANSI_RESET);
                    System.out.println("> " + Print.getPlayerColor(nickname, allPlayers, p) + "(You) " + nickname + ": " + p.getPoints() + " pts;" + Print.ANSI_RESET);
                    break;
                case "scoreboard":
                    Print.scoreboardPrinter(allPlayers, p);
                    break;
                case "table":
                    System.out.println("> These are the cards on the table right now:");
                    Print.drawChoicePrinter(commonGold, commonResource, goldDeck,resourceDeck);
                    break;
                default:
                    System.out.println(Print.ANSI_RED + "> Command unknown, write \"help\" for a list of commands." + Print.ANSI_RESET);
                    break;
            }
        }

    }


    public void clientUpdateThread() throws RemoteException, InterruptedException {
        while(connectionFlagServer) {
            Action act = serverActionsReceived.take();
            if (connected) {
                switch (act.getType()) {
                    case RECONNECTIONSUCCESS:
                        if(((ReconnectionSuccessAction) act).getRecipient().equalsIgnoreCase(nickname)){
                            this.commonGold = ((ReconnectionSuccessAction) act).getCommonGold();
                            this.goldDeck = ((ReconnectionSuccessAction) act).getGoldDeck();
                            this.commonResource = ((ReconnectionSuccessAction) act).getCommonResource();
                            this.resourceDeck = ((ReconnectionSuccessAction) act).getResourceDeck();
                            this.allPlayers = ((ReconnectionSuccessAction) act).getPlayers();
                            this.achievements = ((ReconnectionSuccessAction) act).getCommonAchievement();
                            state = State.COMMANDS;
                            for(Player player : allPlayers){
                                if(player.getName().equalsIgnoreCase(nickname)){
                                    p = player;
                                    starterCard = (StarterCard) p.getArea().getSpace(40, 40).getCard();
                                }
                            }
                            //game.getGameState() firstRound, vedere la dashboard e secret per capire quale state mettere
                            starterCard = (StarterCard) p.getArea().getSpace(40,40).getCard();
                            achievements.add(0, p.getSecretAchievement().get(0));   //controllare se è il primo settato
                        }
                        else{
                            System.out.println("> User " + ((ReconnectionSuccessAction) act).getRecipient() + " reconnected!");
                        }
                        break;
                    case WHOLECHAT:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            ArrayList<Message> chat = ((WholeChatAction) act).getMessages();
                            if (chat.isEmpty()) {
                                System.out.println(Print.ANSI_BOLD + ">>> " + "Public chat is empty" + Print.ANSI_BOLD_RESET);
                            } else {
                                System.out.println(Print.ANSI_BOLD + ">>> " + "-------- PUBLIC CHAT --------" + Print.ANSI_BOLD_RESET);
                                for (Message m : chat) {
                                    System.out.println(Print.ANSI_BOLD + ">>> " + m.toString() + Print.ANSI_BOLD_RESET);
                                }
                            }
                        }
                        break;
                    case JOININGPLAYER:
                        if(((JoiningPlayerAction) act).getPlayer() != null){
                            if(!gui) {
                                if (!((JoiningPlayerAction) act).getPlayer().equals(nickname))
                                    System.out.println("> Player " + ((JoiningPlayerAction) act).getPlayer() + " joined the game. " + ((JoiningPlayerAction) act).getCurrentPlayersNumber() + "/" + (((JoiningPlayerAction) act).getGameSize() == 0 ? "?" : ((JoiningPlayerAction) act).getGameSize()));
                            } else {
                                Platform.runLater(() -> loginController.notifyJoiningPlayer(((JoiningPlayerAction)act).getPlayer(), ((JoiningPlayerAction)act).getCurrentPlayersNumber(), ((JoiningPlayerAction)act).getGameSize()));
                            }
                        }
                        break;
                    case ASKINGPLAYERSNUMBER:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            state = State.GAMESIZE;
                            if(!gui) {
                                System.out.println("> Enter desired players number with command \"gamesize x\".");
                            } else {
                                loginController.showPlayersNumberMenu();
                                do {
                                    try {
                                        Thread.sleep(1000);
                                    } catch (InterruptedException e) {
                                        System.out.println("!!! ERROR SLEEP GETPLAYERSNUMBER !!!");
                                    }
                                } while(loginController.getPlayersNumber() == 0);
                                int playnum = loginController.getPlayersNumber();
                                server.sendAction(new ChosenPlayersNumberAction(playnum));
                                Platform.runLater(loginController::waitForOtherPlayers);
                                state = State.COMMANDS;
                            }
                        }
                        break;
                    case CHATMESSAGE:
                        if (act.getRecipient().isEmpty() || act.getRecipient().equalsIgnoreCase(nickname)) {
                            Message m = ((ChatMessageAction) act).getMessage();
                            if(!gui) {
                                if (m.getRecipient().isEmpty()) {
                                    System.out.println(Print.ANSI_BOLD + ">>> " + m.toString() + Print.ANSI_BOLD_RESET);
                                } else {
                                    System.out.println(Print.ANSI_BOLD + ">>> PRIVATE > " + m.toString() + Print.ANSI_BOLD_RESET);
                                }
                            } else {
                                if(playController != null) {
                                    playController.displayChatMessage(m);
                                }
                            }
                        }
                        break;
                    case CHOOSESIDESTARTERCARD:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            this.commonGold = ((ChooseSideStarterCardAction) act).getCommonGold();
                            this.goldDeck = ((ChooseSideStarterCardAction) act).getGoldDeck();
                            this.commonResource = ((ChooseSideStarterCardAction) act).getCommonResource();
                            this.resourceDeck = ((ChooseSideStarterCardAction) act).getResourceDeck();
                            starterCard = ((ChooseSideStarterCardAction) act).getCard();
                            state = State.STARTERCARD;
                            p = ((ChooseSideStarterCardAction) act).getPlayer();
                            if(gui) {
                                Platform.runLater(() -> guiView.playScene(nickname));
                                do {
                                    try {
                                        Thread.sleep(200);
                                    } catch (InterruptedException e) {
                                        System.out.println("!!! ERROR SLEEP GETCONTROLLER !!!");
                                    }
                                } while (guiView.getPlayController() == null);
                                playController = guiView.getPlayController();
                                playController.setNickname(nickname);
                                if(threadChatListener == null) {
                                    threadChatListener = createThreadChatListener();
                                    threadChatListener.start();
                                }
                                Platform.runLater(() -> playController.passStarterCard(starterCard, p, commonGold, goldDeck, commonResource, resourceDeck));
                                threadStartListener = createThreadStartListener();
                                threadStartListener.start();
                            } else {
                                System.out.println("> Choose the side you want to place your starter card with command \"start\".");
                            }
                        } else {
                            refreshPlayers(((ChooseSideStarterCardAction) act).getPlayer());
                        }
                        if(gui && playController != null) {
                            playController.initializeChatOptions(allPlayers);
                        }
                        break;
                    case CHOOSEABLEACHIEVEMENTS:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            choosableAchievements = ((ChooseableAchievementsAction) act).getAchievements();
                            achievements.addAll(((ChooseableAchievementsAction) act).getCommonGoals());
                            state = State.ACHIEVEMENTSCHOICE;
                            if(!gui) {
                                System.out.println("> Choose your secret achievement with the command \"achievement\".");
                            } else {
                                Platform.runLater(() -> playController.passAchievement(choosableAchievements, achievements));
                                threadAchievementListener = createThreadAchievementListener();
                                threadAchievementListener.start();
                            }
                        }
                        break;
                    case ASKINGPLACE:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            p = ((AskingPlaceAction) act).getPlayer();
                            state = State.PLACE;
                            if(gui) {
                                Platform.runLater(() -> playController.setPlayer(p));
                                threadPlaceListener = createThreadPlaceListener();
                                threadPlaceListener.start();
                            } else {
                                System.out.println("> It's your time to play, enter \"place\" to place a card.");
                            }
                        } else {
                            refreshPlayers(((AskingPlaceAction) act).getPlayer());
                            if(!gui)
                                System.out.println("> It's " + act.getRecipient() + "'s turn to place a card.");
                        }
                        break;
                    case PLACEDCARD:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            p = ((PlacedCardAction) act).getPlayer();
                            if(!gui) {
                                System.out.println("> You placed a " + (((PlacedCardAction) act).getCard().getClass() == ResourceCard.class ? "resource" : "gold") + " card in [" +
                                        ((PlacedCardAction) act).getRow() + "][" + ((PlacedCardAction) act).getColumn() + "]" + (((PlacedCardAction) act).getScore() == 0 ? "" : (" gaining " + ((PlacedCardAction) act).getScore() + " pts")) + ".");
                                Print.playgroundPrinter(p.getArea());
                            } else {
                                Platform.runLater(() -> playController.setPlayer(p));
                            }
                        } else {
                            refreshPlayers(((PlacedCardAction) act).getPlayer());
                            if(!gui)
                                System.out.println("> " + ((PlacedCardAction) act).getPlayer().getName() + " just placed a " +
                                    (((PlacedCardAction) act).getCard().getClass() == ResourceCard.class ? "resource" : "gold") + " card in [" +
                                    ((PlacedCardAction) act).getRow() + "][" + ((PlacedCardAction) act).getColumn() + "]" + (((PlacedCardAction) act).getScore() == 0 ? "" : (" gaining " + ((PlacedCardAction) act).getScore() + " pts")) + ".");
                        }
                        if(gui) {
                            Platform.runLater(() -> playController.displaySuccessfulPlace(act.getRecipient(), ((PlacedCardAction)act).getScore()));
                        }
                        break;
                    case PLACEDCARDERROR:
                        if (act.getRecipient().equalsIgnoreCase(nickname) && state.equals(Client.State.COMMANDS)) {
                            state = Client.State.PLACE;
                            if(!gui) {
                                System.out.println(((PlacedErrorAction) act).getError());
                            } else {
                                threadRePlaceListener = createThreadRePlaceListener();
                                threadRePlaceListener.start();
                            }
                        }
                        break;
                    case STARTERROR:
                        if(act.getRecipient().equalsIgnoreCase(nickname)){
                            System.out.println(((StartErrorAction) act).getError());
                        }
                        break;
                    case ASKINGDRAW:
                        this.commonGold = ((AskingDrawAction) act).getCommonGold();
                        this.goldDeck = ((AskingDrawAction) act).getGoldDeck();
                        this.commonResource = ((AskingDrawAction) act).getCommonResource();
                        this.resourceDeck = ((AskingDrawAction) act).getResourceDeck();
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            state = Client.State.DRAW;
                            if(!gui) {
                                System.out.println("> Choose the card you want to draw with the command \"draw\".");
                            } else {
                                threadDrawListener = createThreadDrawListener();
                                threadDrawListener.start();
                            }
                        } else {
                            if(!gui) {
                                System.out.println("> It's " + act.getRecipient() + "'s turn to draw a card.");
                            } else {
                                Platform.runLater(() -> playController.updateTableCards(commonGold, goldDeck, commonResource, resourceDeck));
                            }
                        }
                        break;
                    case CARDDRAWN:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            p = ((CardDrawnAction) act).getPlayer();
                            if(gui) {
                                Platform.runLater(() -> playController.setPlayer(p));
                            } else {
                                System.out.println("> You drew the following card:");
                                Print.largeCardBothSidesPrinter(((CardDrawnAction) act).getCard());
                                System.out.println("> Your turn is over.");
                            }
                        } else {
                            refreshPlayers(((CardDrawnAction) act).getPlayer());
                            if(!gui)
                                System.out.println("> " + act.getRecipient() + " drew a card.");
                        }
                        if(gui) {
                            Platform.runLater(() -> playController.displaySuccessfulDrawn(act.getRecipient(), ((CardDrawnAction)act).getCard()));
                        }
                        break;
                    case WINNERS:
                        for (Player tempPlayer : ((WinnersAction) act).getPlayers())
                            if (nickname.equalsIgnoreCase(tempPlayer.getName())) {
                                p = tempPlayer;
                                if(gui)
                                    Platform.runLater(() -> playController.setPlayer(p));
                            } else
                                refreshPlayers(tempPlayer);
                        if(!gui) {
                            Print.resultAsciiArt(p.isWinner(), Print.getPlayerColor(nickname, allPlayers, p));
                            Print.scoreboardPrinter(allPlayers, p);
                        } else {
                            Platform.runLater(() -> playController.showResult());
                        }
                        break;
                    case ASKINGSTART:
                        if(((AskingStartAction)act).getRecipient().equalsIgnoreCase(nickname)){
                            System.out.println("> Players online: " + ((AskingStartAction)act).getPlayerNumber() + " - Type \"startgame\" to start the game");
                            state = Client.State.GAMESIZE;
                        }
                        break;
                    case PING:
                        server.sendAction(new PongAction(nickname, state.toString()));
                        break;
                    case DISCONNECTEDPLAYER:
                        System.out.println("> Disconnected Player: " + ((DisconnectedPlayerAction) act).getNickname());
                        //System.out.println("> Players online: " + ((DisconnectedPlayerAction) act).getNumberOnline());
                        break;
                    default:
                        break;
                }
            } else {
                switch (act.getType()) {
                    case RECONNECTIONSUCCESS:
                        if(((ReconnectionSuccessAction) act).getRecipient().equalsIgnoreCase(nickname)){
                            this.commonGold = ((ReconnectionSuccessAction) act).getCommonGold();
                            this.goldDeck = ((ReconnectionSuccessAction) act).getGoldDeck();
                            this.commonResource = ((ReconnectionSuccessAction) act).getCommonResource();
                            this.resourceDeck = ((ReconnectionSuccessAction) act).getResourceDeck();
                            this.allPlayers = ((ReconnectionSuccessAction) act).getPlayers();
                            state = State.COMMANDS;
                            for(Player player : allPlayers){
                                if(player.getName().equalsIgnoreCase(nickname)){
                                    p = player;
                                    starterCard = (StarterCard) p.getArea().getSpace(40, 40).getCard();
                                }
                            }
                            //game.getGameState() firstRound, vedere la dashboard e secret per capire quale state mettere
                            starterCard = (StarterCard) p.getArea().getSpace(40,40).getCard();
                            achievements.add(0, p.getSecretAchievement().get(0));   //controllare se è il primo settato
                        }
                        else{
                            System.out.println("> User " + ((ReconnectionSuccessAction) act).getRecipient() + " reconnected!");
                            for(Player player : allPlayers){
                                if(player.getName().equalsIgnoreCase(nickname))
                                    refreshPlayers(player);
                            }
                        }
                        break;
                    case SETNICKNAME: //only for Socket
                        if (((SetNicknameAction)act).getNickname()!= null) {
                            nickname = ((SetNicknameAction)act).getNickname();
                            connected = true;
                            System.out.println("Login successful " + nickname);
                        }
                        else{
                            System.out.println("Login Error!");
                            System.exit(0);
                        }
                        break;
                    case PING:
                        server.sendAction(new PongAction(nickname, state.toString()));
                        break;
                    default:
                        break;
                }
            }
        }
    }



    // DA SISTEMARE


    @Override
    public void reportError(String details) throws RemoteException {
        System.out.println("\n[ERROR]= " + "\n> ");
    }

    @Override
    public String getNickname() {
        return nickname;
    }

    @Override
    public boolean getOnline() {
        return connected;
    }

    @Override
    public boolean getGui() {
        return gui;
    }

    @Override
    public void setOnline(boolean b) {
        connected=b;
    }

    @Override
    public void setNickname(String nick) {
        nickname = nick;
    }

    @Override
    public void setPing(boolean b) {

    }

    @Override
    public boolean getPing() {
        return false;
    }

    @Override
    public void setInTurn(boolean b) throws RemoteException {

    }

    @Override
    public boolean getInTurn() throws RemoteException {
        return false;
    }

    @Override
    public String getNicknameFirst() throws RemoteException {
        return nickname;
    }

    private void refreshPlayers(Player player) {
        boolean check = false;
        for(int i = 0; i < allPlayers.size() && !check; i++) {
            if(player.getName().equalsIgnoreCase(allPlayers.get(i).getName())) {
                allPlayers.remove(i); //removing the outdated player from the list
                allPlayers.add(player); //adding the update player to the list
                check = true;
            }
        }
        if(!check) {//the player is being refreshed for the first time
            //proviamo a
            allPlayers.add(player);
        }
    }

    public void showAction(Action actionFromServer) throws RemoteException {
        try {
            if(actionFromServer.getType().equals(ActionType.PING)){
                server.sendAction(new PongAction(nickname, state.toString()));
                //System.out.println("Inviato Pong!");
            }
            else {
                serverActionsReceived.put(actionFromServer);
            }
        } catch (InterruptedException | NullPointerException e) {
            //da controllare
//            System.err.println("NULL POINTER EXCEPTION - Only player connected disconnected, terminating task...");
//            System.exit(0);
            throw new RuntimeException(e);
        }
// era così:
//        } catch (InterruptedException e) {
//            throw new RuntimeException(e);
//        }
    }

    // Sending actions to server
    public void serverUpdateThread() throws InterruptedException, RemoteException {
        // It sends all the actions in the queue to the server
        try {
            while (connectionFlagServer) {
                Action a = clientActionsToSend.take();
                server.sendAction(a);
            }
        } catch (InterruptedException | IOException e) {
            // Gestione dell'eccezione
            connectionFlagServer = false;
            e.printStackTrace();
        }
    }

    //listening threads for gui

    private Thread createThreadChatListener() {
        return new Thread(() -> {
            while (playController == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP GETCONTROLLER FOR CHAT !!!");
                }
            }
            boolean listenToChat = true;
            while(listenToChat) {
                try {
                    if(!playController.messagesToSendQueue.isEmpty()) {
                        Message message = playController.messagesToSendQueue.get(0);
                        playController.messagesToSendQueue.remove(0);
                        clientActionsToSend.put(new ChatMessageAction(nickname, message.getRecipient(), message));
                    } else {
                        Thread.sleep(200);
                    }
                } catch(InterruptedException e) {
                    listenToChat = false;
                }
            }
            System.out.println("!!! ERROR STOP LISTENING CHAT !!!");
        });
    }

    private Thread createThreadStartListener() {
        return new Thread(() -> {
            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP GET STARTER CHOICE !!!");
                }
            } while (playController.starterChoice == 0);
            try {
                clientActionsToSend.put(new ChosenSideStarterCardAction(nickname, (playController.starterChoice == 1)));
            } catch (InterruptedException e) {
                System.out.println("!!! ERROR THREAD STARTER CHOICE couldn't send action !!!");
                throw new RuntimeException(e);
            }
            state = State.COMMANDS;
        });
    }

    private Thread createThreadAchievementListener() {
        return new Thread(() -> {
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP GET ACHIEVEMENT CHOICE !!!");
                }
            } while (playController.achievementChoice == 0);
            try {
                clientActionsToSend.put(new ChosenAchievementAction(nickname, playController.achievementChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1)));
            } catch (InterruptedException e) {
                System.out.println("!!! ERROR THREAD ACHIEVEMENT CHOICE couldn't send action !!!");
                throw new RuntimeException(e);
            }
            state = State.COMMANDS;
        });
    }

    private Thread createThreadPlaceListener() {
        return new Thread(() -> {
            Platform.runLater(() -> playController.alertToPlace());
            PlacingCardAction a;
            do {
                a = playController.discoverPlace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP HAS PLACED !!!");
                }
            } while (a == null);
            try {
                clientActionsToSend.put(a);
            } catch (InterruptedException e) {
                System.out.println("!!! ERROR THREAD PLACE CARD couldn't send action !!!");
                throw new RuntimeException(e);
            }
            state = State.COMMANDS;
        });
    }

    private Thread createThreadRePlaceListener() {
        return new Thread(() -> {
            Platform.runLater(() -> playController.alertToRePlace());
            PlacingCardAction a;
            do {
                a = playController.discoverPlace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP HAS PLACED !!!");
                }
            } while (a == null);
            try {
                clientActionsToSend.put(a);
            } catch (InterruptedException e) {
                System.out.println("!!! ERROR THREAD REPLACE CARD couldn't send action !!!");
                throw new RuntimeException(e);
            }
            state = State.COMMANDS;
        });
    }

    private Thread createThreadDrawListener() {
        return new Thread(() -> {
            Platform.runLater(() -> playController.updateTableCards(commonGold, goldDeck, commonResource, resourceDeck));
            Platform.runLater(() -> playController.alertToDraw());
            ChosenDrawCardAction a;
            do {
                a = playController.discoverDraw();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    System.out.println("!!! ERROR SLEEP HAS DRAWN !!!");
                }
            } while (a == null);
            try {
                clientActionsToSend.put(a);
            } catch (InterruptedException e) {
                System.out.println("!!! ERROR THREAD DRAW CARD couldn't send action !!!");
                throw new RuntimeException(e);
            }
        });
    }

}
