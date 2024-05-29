package it.polimi.ingsw.gui;


import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.networking.rmi.RmiServer;
import it.polimi.ingsw.networking.rmi.VirtualServer;
import it.polimi.ingsw.networking.rmi.VirtualView;
import it.polimi.ingsw.util.Print;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.rmi.ConnectException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RMItoGUI extends UnicastRemoteObject implements VirtualView {


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
    private State state;
    private final VirtualServer server;
    private ArrayList<Player> allPlayers;
    private StarterCard starterCard;
    private ArrayList<AchievementCard> achievements; // the first element is the secret achievement
    private ArrayList<AchievementCard> choosableAchievements;
    private ArrayList<ResourceCard> commonResource;
    private ArrayList<GoldCard> commonGold;
    private Resource goldDeck;
    private Resource resourceDeck;
    boolean repeatDraw;
    private static final String LOCAL_HOST = "127.0.0.1";

    private final GUIView guiView;
    private final LoginController loginController;
    private PlayController playController;
    private Thread threadChatListener, threadStartListener, threadAchievementListener, threadPlaceListener, threadRePlaceListener, threadDrawListener;

    public RMItoGUI(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        achievements = new ArrayList<>();
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
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        // qui teoricamente non ci sarà più un main perché il client verrà lanciato da ClientApp, quindi si userà direttamente il metodo "init()" quando si istanzierà RMItoGUI
        final String serverName = "GameServer";
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            VirtualServer server = (VirtualServer) registry.lookup(serverName);
            new RMItoGUI(server).run();
        } catch(ConnectException | NotBoundException e) {
            System.out.println(Print.ANSI_RED + "> Server might be down." + Print.ANSI_RESET);
            System.exit(0);
        }
    }

    //Metodo che ci serve public, verrà usato una volta che viene lanciato un client e viene scelta la tecnologia RMI
/*
    public void init() throws RemoteException, NotBoundException{
        final String serverName = "GameServer";
        try {
            Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
            VirtualServer server = (VirtualServer) registry.lookup(serverName);
            new RMItoGUI(server).run();
        } catch(ConnectException | NotBoundException e) {
            System.out.println(Print.ANSI_RED + "> Server might be down." + Print.ANSI_RESET);
            System.exit(0);
        }
    }
*/

    private void run() throws RemoteException {
        boolean repeat = true;
        while(repeat) {
            do {
                this.nickname = loginController.getNickname();
            } while (this.nickname.isEmpty());
            if(this.server.connect(this))
                repeat = false;
            else
                loginController.invalidNickname();
        }
        Platform.runLater(loginController::waitForOtherPlayers);
        this.runCli();
    }

    private void runCli() throws RemoteException {
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
                    if (state == State.GAMESIZE) {
                        try {
                            int playnum = Integer.parseInt(st.nextToken());
                            while(playnum < 2 || playnum > 4) {
                                System.out.print("> The number must be between 2 and 4: ");
                                playnum = Integer.parseInt(scan.nextLine());
                            }
                            a = new ChosenPlayersNumberAction(playnum);
                            server.sendAction(a);
                            System.out.println("> Game's size set to " + playnum + ".");
                            state = State.COMMANDS;
                        } catch (NoSuchElementException | NumberFormatException e) {
                            System.out.println("> Invalid command syntax.");
                            continue;
                        }
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, game's size already set." + Print.ANSI_RESET);
                    }
                    break;
                case "chat":
                    if (line.length() > 5) {
                        msg = new Message(line.substring(5), nickname);
                        a = new ChatMessageAction(nickname, null, msg);
                        server.sendAction(a);
                    }
                    break;
                case "getchat":
                    a = new AskingChatAction(nickname);
                    server.sendAction(a);
                    break;
                case "whisper":
                    command = st.nextToken();
                    msg = new Message(line.substring(7 + command.length() + 1), nickname, command);
                    a = new ChatMessageAction(nickname, command, msg);
                    server.sendAction(a);
                    System.out.println(Print.ANSI_BOLD + ">>> PRIVATE to " + msg.getRecipient() + " > " + msg.toString() + Print.ANSI_BOLD_RESET);
                    break;
                case "help":
                    System.out.println("> ------- COMMANDS LIST -------");
                    System.out.printf("> %-30s%s\n","chat [...]","to send a public message to everyone.");
                    System.out.printf("> %-30s%s\n","whisper [x] [...]","to send a private message to x.");
                    System.out.printf("> %-30s%s\n","getchat","to retrieve a full log of the public chat.");
                    System.out.printf("> %-30s%s\n","gamesize [x]","to choose the number x of players who will play the game.");
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
                    if(state.equals(State.PLACE)) {
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
                        server.sendAction(a);
                        state = State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't place a card right now." + Print.ANSI_RESET);
                    }
                    break;
                case "start":
                    if(state.equals(State.STARTERCARD)) {
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
                        state = State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't choose the starter card right now." + Print.ANSI_RESET);
                    }
                    break;
                case "achievement":
                    if(state.equals(State.ACHIEVEMENTSCHOICE)) {
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
                        server.sendAction(a);
                        System.out.println("> Waiting for other players to choose their starter card and secret achievement.");
                        state = State.COMMANDS;
                    } else {
                        System.out.println(Print.ANSI_RED + "> Permission denied, you can't choose the secret achievement right now." + Print.ANSI_RESET);
                    }
                    break;
                case "draw":
                    if(state.equals(State.DRAW)) {
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
                        server.sendAction(a);
                        state = State.COMMANDS;
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

    @Override
    public void showAction(Action act) throws RemoteException{
        System.out.println(Print.ANSI_YELLOW + ">>>>>>> Client " + Print.ANSI_RESET + nickname + Print.ANSI_YELLOW  + " handling " + Print.ANSI_RED + act.getType() + Print.ANSI_RESET + Print.ANSI_YELLOW  + " -> " + Print.ANSI_RESET + act.getRecipient());
        switch(act.getType()){
            case WHOLECHAT:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
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
                System.out.println("> Player " + ((JoiningPlayerAction)act).getPlayer() + " joined the game. " + ((JoiningPlayerAction)act).getCurrentPlayersNumber() + "/" + (((JoiningPlayerAction)act).getGameSize() == 0 ? "?" : ((JoiningPlayerAction)act).getGameSize()));
                Platform.runLater(() -> loginController.notifyJoiningPlayer(((JoiningPlayerAction)act).getPlayer(), ((JoiningPlayerAction)act).getCurrentPlayersNumber(), ((JoiningPlayerAction)act).getGameSize()));
                break;
            case ASKINGPLAYERSNUMBER:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    state = State.GAMESIZE;
                    int playnum;
                    System.out.println("> Enter desired players number with command \"gamesize x\".");
                    loginController.showPlayersNumberMenu();
                    do {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            System.out.println("!!! ERROR SLEEP GETPLAYERSNUMBER !!!");
                        }
                    } while(loginController.getPlayersNumber() == 0);
                    playnum = loginController.getPlayersNumber();
                    server.sendAction(new ChosenPlayersNumberAction(playnum));
                    Platform.runLater(loginController::waitForOtherPlayers);
                    state = State.COMMANDS;
                }
                break;
            case CHATMESSAGE:
                if(act.getRecipient().isEmpty() || act.getRecipient().equalsIgnoreCase(nickname)) {
                    Message m = ((ChatMessageAction) act).getMessage();
                    if (m.getRecipient().isEmpty()) {
                        System.out.println(Print.ANSI_BOLD + ">>> " + m.toString() + Print.ANSI_BOLD_RESET);
                    } else {
                        System.out.println(Print.ANSI_BOLD + ">>> PRIVATE > " + m.toString() + Print.ANSI_BOLD_RESET);
                    }
                    if(playController != null) {
                        playController.displayChatMessage(m);
                    }
                }
                break;
            case CHOOSESIDESTARTERCARD:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    commonGold = ((ChooseSideStarterCardAction)act).getCommonGold();
                    goldDeck = ((ChooseSideStarterCardAction)act).getGoldDeck();
                    commonResource = ((ChooseSideStarterCardAction)act).getCommonResource();
                    resourceDeck = ((ChooseSideStarterCardAction)act).getResourceDeck();
                    starterCard = ((ChooseSideStarterCardAction)act).getCard();
                    state = State.STARTERCARD;
                    p = ((ChooseSideStarterCardAction) act).getPlayer();
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
                    Platform.runLater(() ->playController.passStarterCard(starterCard, p, commonGold, goldDeck, commonResource, resourceDeck));
                    threadStartListener = createThreadStartListener();
                    threadStartListener.start();
                } else {
                    refreshPlayers(((ChooseSideStarterCardAction)act).getPlayer());
                }
                if(playController != null) {
                    playController.initializeChatOptions(allPlayers);
                }
                break;
            case CHOOSEABLEACHIEVEMENTS:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    choosableAchievements = ((ChooseableAchievementsAction)act).getAchievements();
                    achievements.addAll(((ChooseableAchievementsAction) act).getCommonGoals());
                    state = State.ACHIEVEMENTSCHOICE;
                    System.out.println("> Choose your secret achievement with the command \"achievement\".");
                    Platform.runLater(() -> playController.passAchievement(choosableAchievements, achievements));
                    threadAchievementListener = createThreadAchievementListener();
                    threadAchievementListener.start();
                }
                break;
            case ASKINGPLACE:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    p = ((AskingPlaceAction)act).getPlayer();
                    Platform.runLater(() -> playController.setPlayer(p));
                    state = State.PLACE;
                    threadPlaceListener = createThreadPlaceListener();
                    threadPlaceListener.start();
                    System.out.println("> It's your time to play, enter \"place\" to place a card.");
                } else {
                    refreshPlayers(((AskingPlaceAction)act).getPlayer());
                    System.out.println("> It's " + act.getRecipient() + "'s turn to place a card.");
                }
                break;
            case PLACEDCARD:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    System.out.println("> You placed a " + (((PlacedCardAction)act).getCard().getClass() == ResourceCard.class ? "resource" : "gold") + " card in [" +
                            ((PlacedCardAction)act).getRow() + "][" + ((PlacedCardAction)act).getColumn() + "]" + (((PlacedCardAction)act).getScore() == 0 ? "" : (" gaining " + ((PlacedCardAction)act).getScore() + " pts")) + ".");
                    p = ((PlacedCardAction)act).getPlayer();
                    Platform.runLater(() -> playController.setPlayer(p));
                    Print.playgroundPrinter(p.getArea());
                } else {
                    refreshPlayers(((PlacedCardAction)act).getPlayer());
                    System.out.println("> " + ((PlacedCardAction)act).getPlayer().getName() + " just placed a " +
                            (((PlacedCardAction)act).getCard().getClass() == ResourceCard.class ? "resource" : "gold") + " card in [" +
                            ((PlacedCardAction)act).getRow() + "][" + ((PlacedCardAction)act).getColumn() + "]" + (((PlacedCardAction)act).getScore() == 0 ? "" : (" gaining " + ((PlacedCardAction)act).getScore() + " pts")) + ".");
                }
                Platform.runLater(() -> playController.displaySuccessfulPlace(act.getRecipient(), ((PlacedCardAction)act).getScore()));
                break;
            case PLACEDCARDERROR:
                if(act.getRecipient().equalsIgnoreCase(nickname) && state.equals(State.COMMANDS)) {
                    System.out.println(((PlacedErrorAction)act).getError());
                    state = State.PLACE;
                    threadRePlaceListener = createThreadRePlaceListener();
                    threadRePlaceListener.start();
                }
                break;
            case ASKINGDRAW:
                this.commonGold = ((AskingDrawAction)act).getCommonGold();
                this.goldDeck = ((AskingDrawAction)act).getGoldDeck();
                this.commonResource = ((AskingDrawAction)act).getCommonResource();
                this.resourceDeck = ((AskingDrawAction)act).getResourceDeck();
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    System.out.println("> Choose the card you want to draw with the command \"draw\".");
                    state = State.DRAW;
                    threadDrawListener = createThreadDrawListener();
                    threadDrawListener.start();
                } else {
                    Platform.runLater(() -> playController.updateTableCards(commonGold, goldDeck, commonResource, resourceDeck));
                    System.out.println("> It's " + act.getRecipient() + "'s turn to draw a card.");
                }
                break;
            case CARDDRAWN:
                if(act.getRecipient().equalsIgnoreCase(nickname)){
                    p = ((CardDrawnAction)act).getPlayer();
                    Platform.runLater(() -> playController.setPlayer(p));
                    System.out.println("> You drew the following card:");
                    Print.largeCardBothSidesPrinter(((CardDrawnAction)act).getCard());
                    System.out.println("> Your turn is over.");
                } else {
                    refreshPlayers(((CardDrawnAction)act).getPlayer());
                    System.out.println("> " + act.getRecipient() + " drew a card.");
                }
                Platform.runLater(() -> playController.displaySuccessfulDrawn(act.getRecipient(), ((CardDrawnAction)act).getCard()));
                break;
            case WINNERS:
                for(Player tempPlayer : ((WinnersAction)act).getPlayers())
                    if(nickname.equalsIgnoreCase(tempPlayer.getName())) {
                        p = tempPlayer;
                        Platform.runLater(() -> playController.setPlayer(p));
                    } else {
                        refreshPlayers(tempPlayer);
                    }
                Print.resultAsciiArt(p.isWinner(), Print.getPlayerColor(nickname, allPlayers, p));
                Print.scoreboardPrinter(allPlayers, p);
                break;
            default:
                break;
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
    public boolean getOnline() throws RemoteException  {
        return false;
    }

    @Override
    public boolean getGui() throws RemoteException  {
        return false;
    }

    private void refreshPlayers(Player player) {
        boolean check = false;
        for(int i = 0; i < allPlayers.size() && !check; i++) {
            if(player.getName().equalsIgnoreCase(allPlayers.get(i).getName())) {
                allPlayers.remove(i); //removing the outdated player from the list
                allPlayers.add(player); //adding the update player to the list
                if(playController != null)
                    Platform.runLater(() -> playController.setOtherPlayers(allPlayers));
                check = true;
            }
        }
        if(!check) //the player is being refreshed for the first time
            allPlayers.add(player);
    }

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
                        server.sendAction(new ChatMessageAction(nickname, message.getRecipient(), message));
                    } else {
                        Thread.sleep(200);
                    }
                } catch(InterruptedException | RemoteException e) {
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
                server.sendAction(new ChosenSideStarterCardAction(nickname, (playController.starterChoice == 1)));
            } catch (RemoteException e) {
                System.out.println("!!! ERROR THREAD STARTER CHOICE couldn't send action !!!");
                throw new RuntimeException(e);
            }
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
                server.sendAction(new ChosenAchievementAction(nickname, playController.achievementChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1)));
            } catch (RemoteException e) {
                System.out.println("!!! ERROR THREAD ACHIEVEMENT CHOICE couldn't send action !!!");
                throw new RuntimeException(e);
            }
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
                server.sendAction(a);
            } catch (RemoteException e) {
                System.out.println("!!! ERROR THREAD PLACE CARD couldn't send action !!!");
                throw new RuntimeException(e);
            }
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
                server.sendAction(a);
            } catch (RemoteException e) {
                System.out.println("!!! ERROR THREAD REPLACE CARD couldn't send action !!!");
                throw new RuntimeException(e);
            }
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
                server.sendAction(a);
            } catch (RemoteException e) {
                System.out.println("!!! ERROR THREAD DRAW CARD couldn't send action !!!");
                throw new RuntimeException(e);
            }
        });
    }

}
