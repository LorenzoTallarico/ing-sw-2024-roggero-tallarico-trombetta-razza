package it.polimi.ingsw.clientProva;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ChatMessageAction;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.networking.rmi.RmiClient;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.util.Print;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.StringTokenizer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Client implements VirtualView, Serializable {

    enum State {
        COMMANDS,
        GAMESIZE,
        STARTERCARD,
        DRAW,
        PLACE,
        ACHIEVEMENTSCHOICE,
        END
    }

    private final static int PORT = 6969;
    private Player p;
    private String nickname;
    private Client.State state;
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
    private final BlockingQueue<Action> serverActionsReceived = new LinkedBlockingQueue<>(); //Action arrivate da Server
    private final BlockingQueue<Action> clientActionsToSend = new LinkedBlockingQueue<>(); //Action da mandare Server
    private boolean gui;
    private boolean connected = false;
    private boolean connectionFlagServer=true;
    private boolean connectionFlagClient=true;


    /*public Client(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = Client.State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        achievements = new ArrayList<>();
    }*/

    public Client (int connectionChoice, int portChoice, String ip, boolean gui) throws IOException, NotBoundException {
        this.p = new Player();
        this.nickname = "";
        state = Client.State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        achievements = new ArrayList<>();
        this.gui = gui;
        if(connectionChoice == 1){ //RMI
            // new RmiClient(server).init();

            final String serverName = "GameServer";
            //try-catch
            Registry registry = LocateRegistry.getRegistry(ip, portChoice);
            VirtualServer server = (VirtualServer) registry.lookup(serverName);
            this.server = server;
            finalizeConnectionRmi();
        }
        else { //Socket

            //
            Socket socket = new Socket(ip, portChoice);
            VirtualServer serverSocket = (VirtualServer) new ServerSocket(socket, serverActionsReceived);
            this.server = serverSocket;
            Thread serverSocketThread = new Thread((Runnable) serverSocket); // Crea un nuovo thread di ascolto per i messaggi in arrivo dal server
            serverSocketThread.start();
            run();
        }

    }

    public void finalizeConnectionRmi() throws RemoteException {
        System.out.print("> Enter nickname: ");
        Scanner scan = new Scanner(System.in);
        nickname = scan.nextLine();
        if(!this.server.connect(this)) {
            System.err.println("> Connection failed, max number of players already reached or name already taken.");
            System.exit(0);
        }
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

    private void run(){
        // Start runCli Thread
        new Thread(() -> {
            try {
                runCli();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        // Start ClientUpdateThread
        new Thread(() -> {
            try {
                clientUpdateThread();
            } catch (RemoteException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void runCli() throws RemoteException, InterruptedException {
        Scanner scan = new Scanner(System.in);
        Message msg;
        Action a;

        while(true) {
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
                    a = new StartAction(nickname);
                    clientActionsToSend.put(a);
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
                        System.out.println("> Player " + ((JoiningPlayerAction) act).getPlayer() + " joined the game. " + ((JoiningPlayerAction) act).getCurrentPlayersNumber() + "/" + (((JoiningPlayerAction) act).getGameSize() == 0 ? "?" : ((JoiningPlayerAction) act).getGameSize()));
                        break;
                    case ASKINGPLAYERSNUMBER:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            state = Client.State.GAMESIZE;
                            System.out.println("> Enter desired players number with command \"gamesize x\".");
                        }
                        break;
                    case CHATMESSAGE:
                        if (act.getRecipient().isEmpty() || act.getRecipient().equalsIgnoreCase(nickname)) {
                            Message m = ((ChatMessageAction) act).getMessage();
                            if (m.getRecipient().isEmpty()) {
                                System.out.println(Print.ANSI_BOLD + ">>> " + m.toString() + Print.ANSI_BOLD_RESET);
                            } else {
                                System.out.println(Print.ANSI_BOLD + ">>> PRIVATE > " + m.toString() + Print.ANSI_BOLD_RESET);
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
                            state = Client.State.STARTERCARD;
                            synchronized (p) {
                                p = ((ChooseSideStarterCardAction) act).getPlayer();
                            }
                            System.out.println("> Choose the side you want to place your starter card with command \"start\".");
                        } else {
                            refreshPlayers(((ChooseSideStarterCardAction) act).getPlayer());
                        }
                        break;
                    case CHOOSEABLEACHIEVEMENTS:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            choosableAchievements = ((ChooseableAchievementsAction) act).getAchievements();
                            achievements.addAll(((ChooseableAchievementsAction) act).getCommonGoals());
                            state = Client.State.ACHIEVEMENTSCHOICE;
                            System.out.println("> Choose your secret achievement with the command \"achievement\".");
                        }
                        break;
                    case ASKINGPLACE:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            p = ((AskingPlaceAction) act).getPlayer();
                            state = Client.State.PLACE;
                            System.out.println("> It's your time to play, enter \"place\" to place a card.");
                        } else {
                            refreshPlayers(((AskingPlaceAction) act).getPlayer());
                            System.out.println("> It's " + act.getRecipient() + "'s turn to place a card.");
                        }
                        break;
                    case PLACEDCARD:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            System.out.println("> You placed a " + (((PlacedCardAction) act).getCard().getClass() == ResourceCard.class ? "resource" : "gold") + " card in [" +
                                    ((PlacedCardAction) act).getRow() + "][" + ((PlacedCardAction) act).getColumn() + "]" + (((PlacedCardAction) act).getScore() == 0 ? "" : (" gaining " + ((PlacedCardAction) act).getScore() + " pts")) + ".");
                            p = ((PlacedCardAction) act).getPlayer();
                            Print.playgroundPrinter(p.getArea());
                        } else {
                            refreshPlayers(((PlacedCardAction) act).getPlayer());
                            System.out.println("> " + ((PlacedCardAction) act).getPlayer().getName() + " just placed a " +
                                    (((PlacedCardAction) act).getCard().getClass() == ResourceCard.class ? "resource" : "gold") + " card in [" +
                                    ((PlacedCardAction) act).getRow() + "][" + ((PlacedCardAction) act).getColumn() + "]" + (((PlacedCardAction) act).getScore() == 0 ? "" : (" gaining " + ((PlacedCardAction) act).getScore() + " pts")) + ".");
                        }
                        break;
                    case PLACEDCARDERROR:
                        if (act.getRecipient().equalsIgnoreCase(nickname) && state.equals(Client.State.COMMANDS)) {
                            System.out.println(((PlacedErrorAction) act).getError());
                            state = Client.State.PLACE;
                        }
                        break;
                    case ASKINGDRAW:
                        this.commonGold = ((AskingDrawAction) act).getCommonGold();
                        this.goldDeck = ((AskingDrawAction) act).getGoldDeck();
                        this.commonResource = ((AskingDrawAction) act).getCommonResource();
                        this.resourceDeck = ((AskingDrawAction) act).getResourceDeck();
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            System.out.println("> Choose the card you want to draw with the command \"draw\".");
                            state = Client.State.DRAW;
                        } else {
                            System.out.println("> It's " + act.getRecipient() + "'s turn to draw a card.");
                        }
                        break;
                    case CARDDRAWN:
                        if (act.getRecipient().equalsIgnoreCase(nickname)) {
                            p = ((CardDrawnAction) act).getPlayer();
                            System.out.println("> You drew the following card:");
                            Print.largeCardBothSidesPrinter(((CardDrawnAction) act).getCard());
                            System.out.println("> Your turn is over.");
                        } else {
                            refreshPlayers(((CardDrawnAction) act).getPlayer());
                            System.out.println("> " + act.getRecipient() + " drew a card.");
                        }
                        break;
                    case WINNERS:
                        for (Player tempPlayer : ((WinnersAction) act).getPlayers())
                            if (nickname.equalsIgnoreCase(tempPlayer.getName()))
                                p = tempPlayer;
                            else
                                refreshPlayers(tempPlayer);
                        Print.resultAsciiArt(p.isWinner(), Print.getPlayerColor(nickname, allPlayers, p));
                        Print.scoreboardPrinter(allPlayers, p);
                        break;
                    case ASKINGSTART:
                        if(((AskingStartAction)act).getRecipient().equalsIgnoreCase(nickname)){
                            System.out.println("> Players online: " +((AskingStartAction)act).getPlayerNumber());
                            System.out.println("> Type \"start\" to start the game");
                        }
                    default:
                        break;
                }
            } else {
                switch (act.getType()) {
                    case SETNICKNAME: //only for Socket
                        if (((SetNicknameAction)act).getNickname()!= null) {
                            nickname = ((SetNicknameAction)act).getNickname();
                            connected =true;
                        }
                        else{
                            System.exit(0);
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }


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
        return false;
    }

    @Override
    public boolean getGui() {
        return gui;
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
        if(!check) //the player is being refreshed for the first time
            allPlayers.add(player);
    }

    public void showAction(Action actionFromServer) throws RemoteException {
        try {
            serverActionsReceived.put(actionFromServer);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    //Invio delle action al server
    public void serverUpdateThread() throws InterruptedException, RemoteException {
        // Invia l'azione al server appena la trova in coda
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


}
