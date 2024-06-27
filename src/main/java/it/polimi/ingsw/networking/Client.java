
package it.polimi.ingsw.networking;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.Action;
import it.polimi.ingsw.networking.action.ActionType;
import it.polimi.ingsw.networking.action.ChatMessageAction;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.util.Print;
import it.polimi.ingsw.gui.GUIView;
import it.polimi.ingsw.gui.PlayController;
import it.polimi.ingsw.gui.LoginController;
import javafx.application.Platform;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.Socket;
import java.rmi.ConnectException;
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


    /**
     * Enumerates the possible states of the client during the game.
     */
    enum State {
        COMMANDS,
        GAMESIZE,
        STARTERCARD,
        DRAW,
        PLACE,
        ACHIEVEMENTSCHOICE,
        END
    }

    /**
     * Default RMI port used by the client to connect to the server.
     */
    private final static int PORT = 6969;

    /**
     * Player object representing the client's player in the game.
     */
    private Player p;

    /**
     * Nickname chosen by the client for identification.
     */
    private String nickname;

    /**
     * Current state of the client in the game state machine.
     */
    private Client.State state;

    /**
     * Reference to the remote server object.
     */
    private VirtualServer server;

    /**
     * List of all players currently in the game.
     */
    private ArrayList<Player> allPlayers;

    /**
     * Starter card assigned to the client's player.
     */
    private StarterCard starterCard;

    /**
     * List of achievement cards held by the client's player.
     * The first element is the secret achievement.
     */
    private ArrayList<AchievementCard> achievements;

    /**
     * List of achievement cards available for choice by the client's player.
     */
    private ArrayList<AchievementCard> choosableAchievements;

    /**
     * List of resource cards on the table, available to all players.
     */
    private ArrayList<ResourceCard> commonResource;

    /**
     * List of gold cards on the table, available to all players.
     */
    private ArrayList<GoldCard> commonGold;

    /**
     * Deck of gold cards used in the game, represents the resource of the first gold card on top of the gold deck.
     */
    private Resource goldDeck;

    /**
     * Deck of resource cards used in the game, represents the resource of the first resource card on top of the resource deck.
     */
    private Resource resourceDeck;

    /**
     * Queue for actions received from the server.
     */
    private BlockingQueue<Action> serverActionsReceived = new LinkedBlockingQueue<>();

    /**
     * Queue for actions to be sent to the server.
     */
    private BlockingQueue<Action> clientActionsToSend = new LinkedBlockingQueue<>();

    /**
     * Flag indicating whether the client is using a GUI.
     */
    private final boolean gui;

    /**
     * Flag indicating if the client is connected to the server (online).
     */
    private boolean connected = false;

    /**
     * Flag indicating the connection status with the server (server side).
     */
    private boolean connectionFlagServer = true;

    /**
     * Flag indicating the connection status with the server (client side).
     */
    private boolean connectionFlagClient = true;

    /**
     * Flag indicating if the client is waiting.
     */
    private boolean wait = false;

    /**
     * Flag indicating if the client is in wait routine.
     */
    private boolean startWaitRoutine = false;

    /**
     * Reference to the GUI view object.
     */
    private GUIView guiView;

    /**
     * Reference to the login controller object (GUI).
     */
    private LoginController loginController;

    /**
     * Reference to the play controller object (GUI).
     */
    private PlayController playController;

    /**
     * Threads used for different GUI purposes
     */
    private Thread threadChatListener, threadStartListener, threadAchievementListener, threadPlaceListener, threadRePlaceListener, threadDrawListener;

    /**
     * Constructor for the Client class. Initializes a new player, sets up the connection to the server based on the
     * specified parameters (RMI or Socket), initializes the GUI if enabled, starts the client update threads,
     * and finalizes the connection process.
     *
     * @param connectionChoice Type of connection chosen by the client (1 for RMI, 2 for Socket).
     * @param portChoice Port number to connect to (RMI or Socket).
     * @param ip IP address of the server.
     * @param gui Boolean indicating whether the client uses a GUI.
     * @param nickname Nickname chosen by the client for identification.
     * @throws IOException If an I/O error occurs during socket connection setup.
     * @throws NotBoundException If the RMI registry lookup fails.
     */
    public Client (int connectionChoice, int portChoice, String ip, boolean gui, String nickname)  throws IOException, NotBoundException {
        this.p = new Player();
        this.nickname = nickname;
        state = Client.State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        achievements = new ArrayList<>();
        //connection
        if(connectionChoice == 1) { //RMI
            final String serverName = "GameServer";
            try {
                Registry registry = LocateRegistry.getRegistry(ip, portChoice);
                this.server = (VirtualServer) registry.lookup(serverName);
            } catch (UnknownHostException e) {
                System.out.println("\nWrong ip address or port");
                System.exit(0);
            } catch (ConnectException e) {
                System.out.println("\nConnection error, server might be down");
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
                System.out.println("\nWrong ip address or port");
                System.exit(0);
            }  catch (java.net.ConnectException e) {
                System.out.println("\nConnection error, server might be down");
                System.exit(0);
            }
        }
        // GUI
        this.gui = gui;
        if(gui) {
            guiView = new GUIView();
            try {
                guiView.init();
            } catch (Exception ignored) { }
            Platform.startup(() -> {
                Stage stage = new Stage();
                try {
                    guiView.start(stage);
                } catch (IOException ignored) { }
            });
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            } while (guiView.getLoginController() == null);
            loginController = guiView.getLoginController();
            Platform.runLater(() -> loginController.setNickname(nickname));
        }
        //update threads
        new Thread(() -> {
            try {
                clientUpdateThread();
            } catch (RemoteException | InterruptedException ignored) { }
        }).start();
        new Thread(() -> {
            try {
                serverUpdateThread();
            } catch (RemoteException | InterruptedException ignored) { }
        }).start();
        // connecting
        finalizeConnection(connectionChoice);

    }

    /**
     * Finalizes the connection process based on the chosen connection type (RMI or Socket).
     * If using RMI, attempts to connect to the server and handles connection/reconnection outcomes.
     * If using Socket, sends a SetNicknameAction to the server after successful connection.
     *
     * @param connectionChoice Type of connection chosen by the client (1 for RMI, 2 for Socket).
     * @throws RemoteException If a remote exception occurs during the connection process.
     */
    private void finalizeConnection(int connectionChoice) throws RemoteException {
        if (connectionChoice == 1) {
            if(!this.server.connect(this)) {
                // connection / reconnection RMI
                if(gui) {
                    Platform.runLater(loginController::invalidNickname);
                    Platform.exit();
                } else {
                    System.out.println("> Connection failed, max number of players already reached or name already taken.");
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
            Action act = new SetNicknameAction(nickname);
            server.sendAction(act);
        }
        if(!gui)
            runCommandLine();
    }


    /**
     * Starts a command-line interface (CLI) thread for handling command-line inputs and interactions.
     *
     * @throws RemoteException   If a remote exception occurs during the CLI thread execution.
     */
    private void runCommandLine() throws RemoteException {
        // Start runCli Thread
        new Thread(() -> {
            try {
                runCli();
            } catch (RemoteException | InterruptedException ignored) { }
        }).start();
    }


    /**
     * Runs the command-line interface (CLI) for the client, continuously reading user input
     * and executing corresponding commands until the client disconnects.
     *
     * @throws RemoteException    If a remote exception occurs during communication with the server.
     * @throws InterruptedException If the thread is interrupted while waiting for user input.
     */
    private void runCli() throws RemoteException, InterruptedException {
        Scanner scan = new Scanner(System.in);

        while(connectionFlagClient) {
            String line = scan.nextLine();
            if (line.trim().isEmpty())
                continue;
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken().toLowerCase();
            switch (command) {
                case "gamesize":
                    commandGamesize(st);
                    break;
                case "chat":
                    commandChat(line);
                    break;
                case "getchat":
                    commandGetchat();
                    break;
                case "whisper":
                    commandWhisper(line, st);
                    break;
                case "help":
                    commandHelp();
                    break;
                case "hand":
                    commandHand();
                    break;
                case "place":
                    commandPlace();
                    break;
                case "start":
                    commandStart();
                    break;
                case "achievement":
                    commandAchievement();
                    break;
                case "draw":
                    commandDraw();
                    break;
                case "playground":
                    commandPlayground(line, st);
                    break;
                case "list":
                    commandList();
                    break;
                case "scoreboard":
                    commandScoreboard();
                    break;
                case "table":
                    commandTable();
                    break;
                default:
                    System.out.println(Print.ANSI_RED + "> Command unknown, write \"help\" for a list of commands." + Print.ANSI_RESET);
                    break;
            }
        }
    }


    /**
     * Thread that continuously listens for actions received from the server and updates the client's state
     * and interface accordingly.
     *
     * @throws RemoteException    If a remote exception occurs during communication with the server.
     * @throws InterruptedException If the thread is interrupted while waiting for actions from the server.
     */
    public void clientUpdateThread() throws RemoteException, InterruptedException {
        while(connectionFlagServer) {
            Action act = serverActionsReceived.take();
            if(connected) {
                switch(act.getType()) {
                    case WAIT:
                        updateWait(act);
                        break;
                    case RECONNECTIONSUCCESS:
                        updateReconnectionSuccess1(act);
                        break;
                    case WHOLECHAT:
                        updateWholeChat(act);
                        break;
                    case JOININGPLAYER:
                        updateJoiningPlayer(act);
                        break;
                    case ASKINGPLAYERSNUMBER:
                        updateAskingPlayersNumber(act);
                        break;
                    case CHATMESSAGE:
                        updateChatMessage(act);
                        break;
                    case CHOOSESIDESTARTERCARD:
                        updateChooseSideStarterCard(act);
                        break;
                    case CHOOSEABLEACHIEVEMENTS:
                        updateChoosableAchievements(act);
                        break;
                    case ASKINGPLACE:
                        updateAskingPlace(act);
                        break;
                    case PLACEDCARD:
                        updatePlacedCard(act);
                        break;
                    case PLACEDCARDERROR:
                        updatePlacedCardError(act);
                        break;
                    case STARTERROR:
                        updateStartError(act);
                        break;
                    case ASKINGDRAW:
                        updateAskingDraw(act);
                        break;
                    case CARDDRAWN:
                        updateCardDrawn(act);
                        break;
                    case WINNERS:
                        updateWinners(act);
                        break;
                    case ASKINGSTART:
                        updateAskingStart(act);
                        break;
                    case PING:
                        updatePing();
                        break;
                    case DISCONNECTEDPLAYER:
                        updateDisconnectedPlayer(act);
                        break;
                    case ERROR:
                        updateError(act);
                        break;
                    default:
                        break;
                }
            } else {
                switch (act.getType()) {
                    case RECONNECTIONSUCCESS:
                        updateReconnectionSuccess2(act);
                        break;
                    case SETNICKNAME: //only for Socket
                        updateSetNickname(act);
                        break;
                    case PING:
                        updatePing2();
                        break;
                    case ERROR:
                        updateError2(act);
                        break;
                    default:
                        break;
                }
            }
        }
    }

    //client update methods

    /**
     * Updates the client state upon receiving a WaitAction from the server, indicating that all other players
     * have disconnected and the client must wait to finish their turn before the game ends.
     *
     * @param act The WaitAction received from the server.
     */
    private void updateWait(Action act) {
        if(((WaitAction) act).getNickname().equalsIgnoreCase(nickname)){
            if(!gui) {
                System.out.println("> All the other players disconnected. After you finish your turn in 30s the game will end, if no client reconnects in the meantime.");
            } else {
                Platform.runLater(() -> playController.genericAlert("> All the other players disconnected. After you finish your turn in 30s the game will end, if no client reconnects in the meantime."));
            }
            wait = true;
        }
    }

    /**
     * Updates the client state upon receiving a ReconnectionSuccessAction from the server, indicating a successful
     * reconnection of a player with the specified nickname. Updates player information, game state, and GUI if applicable.
     *
     * @param act The ReconnectionSuccessAction received from the server.
     */
    private void updateReconnectionSuccess1(Action act) {
        if(((ReconnectionSuccessAction) act).getRecipient().equalsIgnoreCase(nickname)) {
            this.commonGold = ((ReconnectionSuccessAction) act).getCommonGold();
            this.goldDeck = ((ReconnectionSuccessAction) act).getGoldDeck();
            this.commonResource = ((ReconnectionSuccessAction) act).getCommonResource();
            this.resourceDeck = ((ReconnectionSuccessAction) act).getResourceDeck();
            this.achievements = ((ReconnectionSuccessAction) act).getCommonAchievement();
            state = State.COMMANDS;
            for(Player player : ((ReconnectionSuccessAction) act).getPlayers()) {
                if(player.getName().equalsIgnoreCase(nickname)){
                    p = player;
                    starterCard = (StarterCard) p.getArea().getSpace(40, 40).getCard();
                } else {
                    refreshPlayers(player);
                }
            }
            starterCard = (StarterCard) p.getArea().getSpace(40,40).getCard();
            achievements.add(0, p.getSecretAchievement().get(0));
            if(gui) {
                Platform.runLater(() -> guiView.playScene(nickname));
                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) { }
                } while (guiView.getPlayController() == null);
                playController = guiView.getPlayController();
                playController.setNickname(nickname);
                if (threadChatListener == null) {
                    threadChatListener = createThreadChatListener();
                    threadChatListener.start();
                }
                Platform.runLater(() -> playController.setupStartedGame(p, allPlayers, achievements, commonGold, goldDeck, commonResource, resourceDeck));
            }
            setOnline(true);
            setPing(true);
        } else {
            if(wait) {
                wait = false;
                startWaitRoutine = false;
            }
            if(!gui) {
                System.out.println("> User " + ((ReconnectionSuccessAction) act).getRecipient() + " reconnected!");
            } else {
                Platform.runLater(() -> playController.genericAlert("> User " + ((ReconnectionSuccessAction) act).getRecipient() + " reconnected!"));
            }
        }
    }


    /**
     * Updates the client upon receiving a WholeChatAction from the server, displaying the entire public chat
     * history if the recipient matches the client's nickname.
     *
     * @param act The WholeChatAction received from the server.
     */
    private void updateWholeChat(Action act) {
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
    }

    /**
     * Updates the client upon receiving a JoiningPlayerAction from the server, notifying the client about a player
     * joining the game. Displays the information in the command line interface or updates the GUI accordingly.
     *
     * @param act The JoiningPlayerAction received from the server.
     */
    private void updateJoiningPlayer(Action act) {
        if(((JoiningPlayerAction) act).getPlayer() != null){
            if(!gui) {
                if (!((JoiningPlayerAction) act).getPlayer().equals(nickname))
                    System.out.println("> Player " + ((JoiningPlayerAction) act).getPlayer() + " joined the game. " + ((JoiningPlayerAction) act).getCurrentPlayersNumber() + "/" + (((JoiningPlayerAction) act).getGameSize() == 0 ? "?" : ((JoiningPlayerAction) act).getGameSize()));
            } else {
                Platform.runLater(() -> loginController.notifyJoiningPlayer(((JoiningPlayerAction)act).getPlayer(), ((JoiningPlayerAction)act).getCurrentPlayersNumber(), ((JoiningPlayerAction)act).getGameSize()));
            }
        }
    }

    /**
     * Updates the client upon receiving an AskingPlayersNumberAction from the server, prompting the client to enter
     * the desired number of players for the game. Displays a menu in the command line interface or the GUI accordingly.
     *
     * @param act The AskingPlayersNumberAction received from the server.
     * @throws RemoteException If a remote exception occurs.
     */
    private void updateAskingPlayersNumber(Action act) throws RemoteException {
        if (act.getRecipient().equalsIgnoreCase(nickname)) {
            state = State.GAMESIZE;
            if(!gui) {
                System.out.println("> Enter desired players number with command \"gamesize x\".");
            } else {
                loginController.showPlayersNumberMenu();
                do {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) { }
                } while(loginController.getPlayersNumber() == 0);
                int playnum = loginController.getPlayersNumber();
                server.sendAction(new ChosenPlayersNumberAction(playnum));
                Platform.runLater(loginController::waitForOtherPlayers);
                state = State.COMMANDS;
            }
        }
    }


    /**
     * Updates the client with a chat message received from the server. If the message is addressed to the current client
     * or is a public message (recipient field is empty), it displays the message either in the command line interface
     * (CLI) or through the graphical user interface (GUI).
     *
     * @param act The action containing the chat message to be processed.
     */
    private void updateChatMessage(Action act) {
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
    }

    /**
     * Updates the client upon receiving a ChooseSideStarterCardAction from the server, initializing the starter card
     * selection phase. Updates client's resources and game state accordingly. If the recipient matches the client's nickname,
     * displays instructions in the command line interface or initializes GUI scene and controllers for starter card selection.
     *
     * @param act The ChooseSideStarterCardAction received from the server.
     */
    private void updateChooseSideStarterCard(Action act) {
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
                    } catch (InterruptedException ignored) { }
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
    }

    /**
     * Updates the client upon receiving a ChooseableAchievementsAction from the server, providing the client with
     * a list of achievements. Updates client's achievements and game state accordingly. If the recipient matches
     * the client's nickname, prompts the client to choose a secret achievement via command or initializes GUI for achievement
     * selection.
     *
     * @param act The ChooseableAchievementsAction received from the server.
     */
    private void updateChoosableAchievements(Action act) {
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
    }

    /**
     * Updates the client when it's asked to place a card during the game. If the action is directed to the current client,
     * it sets the player and changes the state to indicate it's the player's turn to place a card. In GUI mode, it updates
     * the interface accordingly and starts a listener thread for card placement. If the action is for another player, it refreshes
     * the player list and prints a message in the CLI.
     *
     * @param act The action containing the player and request for card placement.
     */
    private void updateAskingPlace(Action act) {
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
    }

    /**
     * Updates the client after a card has been successfully placed by either the current client or another player.
     * If the action is directed to the current client, it updates the player's state and optionally prints details of
     * the placed card and updates the playground in CLI mode. In GUI mode, it updates the player's state and notifies
     * the GUI controller about the successful card placement.
     * If the action is for another player, it refreshes the player list and optionally prints details of the placed card
     * in CLI mode.
     *
     * @param act The action containing details of the placed card.
     */
    private void updatePlacedCard(Action act) {
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
    }

    /**
     * Updates the client when an error occurs during card placement. If the action is directed to the current client and
     * the state is in COMMANDS, it sets the state to PLACE and handles the error message, printing it in CLI mode.
     * In GUI mode, it starts a listener thread for re-placement.
     *
     * @param act The action containing details of the placement error.
     */
    private void updatePlacedCardError(Action act) {
        if (act.getRecipient().equalsIgnoreCase(nickname) && state.equals(Client.State.COMMANDS)) {
            state = Client.State.PLACE;
            if(!gui) {
                System.out.println(((PlacedErrorAction) act).getError());
            } else {
                threadRePlaceListener = createThreadRePlaceListener();
                threadRePlaceListener.start();
            }
        }
    }

    /**
     * Updates the client when an error occurs during the start of the game. If the action is directed to the current client,
     * it prints the error message in the CLI.
     *
     * @param act The action containing details of the start error.
     */
    private void updateStartError(Action act) {
        if(act.getRecipient().equalsIgnoreCase(nickname)){
            System.out.println(((StartErrorAction) act).getError());
        }
    }

    /**
     * Updates the client when it's asked to draw a card during the game. Sets the client's state to DRAW and updates
     * the client's resource and gold decks based on the action received. If the action is directed to the current client,
     * it prompts the client to choose a card to draw in CLI mode or starts a listener thread for drawing in GUI mode.
     * If the action is for another player, it notifies them it's their turn to draw in CLI mode or updates the table cards
     * in GUI mode.
     *
     * @param act The action containing details of the draw request and updated game decks.
     */
    private void updateAskingDraw(Action act) {
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
    }


    /**
     * Updates the client when a card is drawn during the game. Sets the client's state and updates the client's
     * resource and gold decks based on the action received. If the action is directed to the current client,
     * it updates the client's player information and displays the drawn card information in CLI mode or updates
     * the player and displays the drawn card in GUI mode. If the action is for another player, it refreshes their
     * information in CLI mode or updates the table cards in GUI mode.
     *
     * @param act The action containing details of the drawn card and updated game state.
     */
    private void updateCardDrawn(Action act) {
        this.commonGold = ((CardDrawnAction) act).getCommonGold();
        this.goldDeck = ((CardDrawnAction) act).getGoldDeck();
        this.commonResource = ((CardDrawnAction) act).getCommonResource();
        this.resourceDeck = ((CardDrawnAction) act).getResourceDeck();
        if (act.getRecipient().equalsIgnoreCase(nickname)) {
            p = ((CardDrawnAction) act).getPlayer();
            if(gui) {
                Platform.runLater(() -> playController.setPlayer(p));
            } else {
                System.out.println("> You drew the following card:");
                Print.largeCardBothSidesPrinter(((CardDrawnAction) act).getCard());
                System.out.println("> Your turn is over.");
            }
            if(wait){
                startWaitRoutine = true;
                waitingRoutineOneUser();
            }
        } else {
            refreshPlayers(((CardDrawnAction) act).getPlayer());
            if(!gui)
                System.out.println("> " + act.getRecipient() + " drew a card.");
        }
        if(gui) {
            Platform.runLater(() -> playController.displaySuccessfulDrawn(act.getRecipient(), ((CardDrawnAction)act).getCard()));
            Platform.runLater(() -> playController.updateTableCards(commonGold, goldDeck, commonResource, resourceDeck));
        }
    }

    /**
     * Updates the client when the winners of the game are announced. Updates the player information for each winner
     * received in the action. If the current client is one of the winners, it updates the client's player information
     * and displays the result in CLI mode or updates the GUI to show the results. If the current client is not a winner,
     * it refreshes the information of the other players in CLI mode or prepares the GUI to display the final results.
     *
     * @param act The action containing details of the winners and their scores.
     */
    private void updateWinners(Action act) {
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
    }

    /**
     * Updates the client when it's asked to start the game. If the action is directed to the current client,
     * it displays the current number of online players and prompts them to start the game in CLI mode. If the
     * action is not directed to the current client, it does nothing.
     *
     * @param act The action containing details of the game start request.
     */
    private void updateAskingStart(Action act) {
        if(((AskingStartAction)act).getRecipient().equalsIgnoreCase(nickname)){
            System.out.println("> Players online: " + ((AskingStartAction)act).getPlayerNumber() + " - Type \"startgame\" to start the game");
            state = Client.State.GAMESIZE;
        }
    }


    /**
     * Responds to a ping from the server by sending a pong action with the client's nickname.
     * If an IOException occurs during the sending of the pong action, it is ignored.
     */
    private void updatePing() {
        try {
            server.sendAction(new PongAction(nickname));
        } catch(IOException ignored) { };
    }

    /**
     * Updates the client when a player disconnects from the game. If in CLI mode, it prints a message
     * showing the disconnected player's nickname and the number of players still online. If in GUI mode,
     * it displays an alert with the disconnected player's nickname.
     *
     * @param act The action containing details of the disconnected player.
     */
    private void updateDisconnectedPlayer(Action act) {
        if(!gui)
            System.out.println("> Disconnected Player: " + ((DisconnectedPlayerAction) act).getNickname() + "\n> Players Online: " + ((DisconnectedPlayerAction) act).getNumberOnline());
        else
            Platform.runLater(() -> playController.genericAlert("> Disconnected Player: " + ((DisconnectedPlayerAction) act).getNickname()));

    }

    /**
     * Updates the client when an error action is received from the server. Prints the error message
     * to the console. If the error action indicates that the client should end the connection, it
     * prints a system exit message and terminates the client.
     *
     * @param act The action containing details of the error message and whether to end the connection.
     */
    private void updateError(Action act) {
        System.out.println("> " + ((ErrorAction) act).getMessage());
        if(((ErrorAction) act).getEndConnection()){
            System.out.println("> System Exit");
            System.exit(0);
        }
    }

    //if client is not online


    /**
     * Handles the reconnection success action from the server for Socket clients. Updates the client's
     * state and data upon successful reconnection. If the reconnection is for the current client
     * (recipient matches nickname), updates local data such as gold, resources, players, and achievements.
     * If in GUI mode, updates the play scene and starts necessary threads. If another player reconnects,
     * refreshes their data. Sets the client online and initiates ping if GUI mode.
     *
     * @param act The action containing details of the reconnection success.
     */
    private void updateReconnectionSuccess2(Action act) {
        if(((ReconnectionSuccessAction) act).getRecipient().equalsIgnoreCase(nickname)){
            this.commonGold = ((ReconnectionSuccessAction) act).getCommonGold();
            this.goldDeck = ((ReconnectionSuccessAction) act).getGoldDeck();
            this.commonResource = ((ReconnectionSuccessAction) act).getCommonResource();
            this.resourceDeck = ((ReconnectionSuccessAction) act).getResourceDeck();
            state = State.COMMANDS;
            for(Player player : ((ReconnectionSuccessAction) act).getPlayers()) {
                if(player.getName().equalsIgnoreCase(nickname)) {
                    p = player;
                    starterCard = (StarterCard) p.getArea().getSpace(40, 40).getCard();
                } else {
                    refreshPlayers(player);
                }
            }
            starterCard = (StarterCard) p.getArea().getSpace(40,40).getCard();
            achievements.add(0, p.getSecretAchievement().get(0));
            if(gui) {
                Platform.runLater(() -> guiView.playScene(nickname));
                do {
                    try {
                        Thread.sleep(200);
                    } catch (InterruptedException ignored) { }
                } while (guiView.getPlayController() == null);
                playController = guiView.getPlayController();
                playController.setNickname(nickname);
                if (threadChatListener == null) {
                    threadChatListener = createThreadChatListener();
                    threadChatListener.start();
                }
                Platform.runLater(() -> playController.setupStartedGame(p, allPlayers, achievements, commonGold, goldDeck, commonResource, resourceDeck));
            }
            setOnline(true);
            setPing(true);
        } else {
            if(!gui)
                System.out.println("> User " + ((ReconnectionSuccessAction) act).getRecipient() + " reconnected!");
            for(Player player : allPlayers) {
                if(player.getName().equalsIgnoreCase(nickname))
                    refreshPlayers(player);
            }
            if(gui && playController != null) {
                playController.initializeChatOptions(allPlayers);
            }
        }
    }

    /**
     * Handles the action from the server confirming or rejecting the set nickname operation for Socket clients.
     * If the nickname is set successfully (not null), sets the client as connected. If in GUI mode,
     * waits for other players to connect. If the nickname is rejected or an error occurs, handles the
     * appropriate actions such as displaying an error message and exiting the system.
     *
     * @param act The action containing details of the set nickname operation.
     */
    private void updateSetNickname(Action act) {
        if (((SetNicknameAction)act).getNickname()!= null) {
            connected = true;
            if(gui) {
                Platform.runLater(loginController::waitForOtherPlayers);
            } else {
                System.out.println("> Login successful " + nickname);
            }
        } else{
            if(gui) {
                Platform.runLater(loginController::invalidNickname);
                Platform.exit();
            } else {
                System.out.println("> Login Error!");
            }
            System.exit(0);
        }
    }

    /**
     * Sends a PongAction to the server in response to a PingAction, confirming the client's connection status.
     * This method is used in Socket mode.
     */
    private void updatePing2() {
        try {
            server.sendAction(new PongAction(nickname));
        } catch(IOException ignored) { };
    }

    /**
     * Handles an error action received from the server in Socket mode. Prints the error message to the console.
     * If the error action signals an end of connection, prints a system exit message and terminates the client program.
     *
     * @param act The action containing details of the error.
     */
    private void updateError2(Action act) {
        System.out.println("> " + ((ErrorAction) act).getMessage());
        if(((ErrorAction) act).getEndConnection()){
            System.out.println("> System Exit");
            System.exit(0);
        }
    }

    //command line methods

    /**
     * Choose the size of the game, the right amount of player who can join and play (1<x<5)
     * @param st
     */
    private void commandGamesize(StringTokenizer st) {
        Scanner scan = new Scanner(System.in);
        if (state == Client.State.GAMESIZE) {
            try {
                int playnum = Integer.parseInt(st.nextToken());
                while(playnum < 2 || playnum > 4) {
                    System.out.print("> The number must be between 2 and 4: ");
                    playnum = Integer.parseInt(scan.nextLine());
                }
                Action a = new ChosenPlayersNumberAction(playnum);
                clientActionsToSend.put(a);
                System.out.println("> Game's size set to " + playnum + ".");
                System.out.println("> Waiting for other players...");
                state = Client.State.COMMANDS;
            } catch (NoSuchElementException | NumberFormatException | InterruptedException e) {
                System.out.println("> Invalid command syntax.");
            }
        } else {
            System.out.println(Print.ANSI_RED + "> Permission denied, game's size already set." + Print.ANSI_RESET);
        }
    }

    /**
     * Sends a public message to everyone
     * @param line original command line
     */
    private void commandChat(String line) {
        if (line.length() > 5) {
            Message msg = new Message(line.substring(5), nickname);
            Action a = new ChatMessageAction(nickname, null, msg);
            try {
                clientActionsToSend.put(a);
            } catch(InterruptedException ignored) { };
        }
    }

    /**
     * Displays the whole chat log
     */
    private void commandGetchat() {
        Action a = new AskingChatAction(nickname);
        try {
            clientActionsToSend.put(a);
        } catch(InterruptedException ignored) { };
    }

    /**
     * Sends a private message to another player
     * @param line original command line
     * @param st original string tokenizer
     */
    private void commandWhisper(String line, StringTokenizer st) {
        String command = st.nextToken();
        Message msg = new Message(line.substring(7 + command.length() + 1), nickname, command);
        Action a = new ChatMessageAction(nickname, command, msg);
        try {
            clientActionsToSend.put(a);
        } catch(InterruptedException ignored) { };
        System.out.println(Print.ANSI_BOLD + ">>> PRIVATE to " + msg.getRecipient() + " > " + msg.toString() + Print.ANSI_BOLD_RESET);
    }

    /**
     * Displays the list of all the possible commands the player can use
     */
    private void commandHelp() {
        System.out.println("> ------- COMMANDS LIST -------");
        System.out.printf("> %-30s%s\n","chat [...]","to send a public message to everyone.");
        System.out.printf("> %-30s%s\n","whisper [x] [...]","to send a private message to x.");
        System.out.printf("> %-30s%s\n","getchat","to retrieve a full log of the public chat.");
        System.out.printf("> %-30s%s\n","gamesize [x]","to choose the number x of players who will play the game.");
        System.out.printf("> %-30s%s\n","start","to choose the side of the starter card."); //deprecated
        System.out.printf("> %-30s%s\n","achievement","to choose your secret achievement");
        System.out.printf("> %-30s%s\n","place","to place a card from your hand.");
        System.out.printf("> %-30s%s\n","draw","to draw a new card.");
        System.out.printf("> %-30s%s\n","playground x","to look at the playground of the player x, if you leave x blank, it will show yours.");
        System.out.printf("> %-30s%s\n","hand","to look at the cards you have in your hand and the achievements you shall fulfill.");
        System.out.printf("> %-30s%s\n","table","to look at the cards and the decks in the middle of the table.");
        System.out.printf("> %-30s%s\n","list","to know the full list of players currently playing and their score.");
        System.out.printf("> %-30s%s\n","scoreboard","to look at the scoreboard.");
    }

    /**
     * Displays the hand of the player and their achievements through TUI
     */
    private void commandHand() {
        System.out.println("> This is your hand:");
        Print.largeHandPrinter(p.getHand(), achievements);
    }

    /**
     * Displays the playground, the hand and the achievements of the player through TUI,
     * then waits for the player to place a card in a specific position
     */
    private void commandPlace() {
        if(state.equals(Client.State.PLACE)) {
            Scanner scan = new Scanner(System.in);
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
                String line = scan.nextLine();
                try {
                    StringTokenizer st = new StringTokenizer(line);
                    index = (Integer.parseInt(st.nextToken())) - 1;
                    side = st.nextToken();
                    row = Integer.parseInt(st.nextToken());
                    column = Integer.parseInt(st.nextToken());
                } catch (NoSuchElementException | NumberFormatException e) {
                    continue;
                }
                if (index >= 0 && index <= p.getHand().size())
                    checkIndex = true;
                else {
                    System.out.println("> Index of the card not valid, enter a number between 1 and " + p.getHand().size() + ".");
                    continue;
                }
                if(side.equals("1") || side.equalsIgnoreCase("f") || side.equalsIgnoreCase("front")) {
                    checkSide = true;
                    chosenSide = true;
                } else if(side.equals("0") || side.equalsIgnoreCase("b") || side.equalsIgnoreCase("back")) {
                    checkSide = true;
                } else {
                    System.out.println("> Side not valid, enter \"1\" / \"F\" / \"Front\" for the front side or \"0\" / \"B\" / \"Back\" for the back side.");
                }
            } while(!checkIndex && !checkSide);
            Action a = new PlacingCardAction(index, chosenSide, row, column, nickname);
            try {
                clientActionsToSend.put(a);
            } catch(InterruptedException ignored) { }
            state = Client.State.COMMANDS;
        } else {
            System.out.println(Print.ANSI_RED + "> Permission denied, you can't place a card right now." + Print.ANSI_RESET);
        }
    }

    /**
     * Displays the two sides of the starter card through TUI and wait for the player to choose one
     * Then sends the action to the queue
     */
    private void commandStart() {
        if(state.equals(Client.State.STARTERCARD)) {
            Scanner scan = new Scanner(System.in);
            String line;
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
            try {
                clientActionsToSend.put(new ChosenSideStarterCardAction(nickname, starterCard.isFront()));
            } catch(InterruptedException ignored) { };
            p.setArea(new Playground());
            p.getArea().setSpace(starterCard, 40, 40);
            state = Client.State.COMMANDS;
        } else {
            System.out.println(Print.ANSI_RED + "> Permission denied, you can't choose the starter card right now." + Print.ANSI_RESET);
        }
    }

    /**
     * Displays the common achievements and the secret one through TUI
     */
    private void commandAchievement() {
        if(state.equals(Client.State.ACHIEVEMENTSCHOICE)) {
            Scanner scan = new Scanner(System.in);
            System.out.println(Print.ANSI_BOLD + "      1° Option               2° Option      " + Print.ANSI_BOLD_RESET);
            Print.inLineAchievementPrinter(choosableAchievements);
            int achChoice = 0;
            while(achChoice < 1 || achChoice > 2) {
                System.out.print("> Enter \"1\" or \"2\" to choose your secret achievement: ");
                try {
                    achChoice = Integer.parseInt(scan.nextLine());
                } catch (NoSuchElementException | NumberFormatException ignored) { }
            }
            Action a = new ChosenAchievementAction(nickname, achChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1));
            achievements.add(0, achChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1));
            try {
                clientActionsToSend.put(a);
            } catch(InterruptedException ignored) { };
            System.out.println("> Waiting for other players to choose their starter card and secret achievement.");
            state = Client.State.COMMANDS;
        } else {
            System.out.println(Print.ANSI_RED + "> Permission denied, you can't choose the secret achievement right now." + Print.ANSI_RESET);
        }

    }

    /**
     * Displays the cards on the table through TUI and wait for the player to choose one
     * Then sends the action to the queue
     */
    private void commandDraw() {
        if(state.equals(Client.State.DRAW)) {
            Scanner scan = new Scanner(System.in);
            Print.drawChoicePrinter(commonGold, commonResource, goldDeck,resourceDeck);
            int drawChoice = 0;
            boolean repeatDraw;
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
            Action a = new ChosenDrawCardAction(nickname, drawChoice);
            try {
                clientActionsToSend.put(a);
            } catch(InterruptedException ignored) { };
            state = Client.State.COMMANDS;
        } else {
            System.out.println(Print.ANSI_RED + "> Permission denied, you can't draw a card right now." + Print.ANSI_RESET);
        }
    }

    /**
     * Displays the playground of the local player or possibly of other players through TUI
     * @param line original command line
     * @param st original stringtokenizer
     */
    private void commandPlayground(String line, StringTokenizer st) {
        if(line.trim().equalsIgnoreCase("playground")) {
            System.out.println("> This is your playground:");
            Print.playgroundPrinter(p.getArea());
        } else {
            boolean tempCheck = false;
            String command = st.nextToken();
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
    }

    /**
     * Displays the list of the players in the game and their current score
     */
    private void commandList() {
        System.out.println("> -------- Players list --------");
        for(Player player : allPlayers)
            System.out.println("> " + Print.getPlayerColor(player.getName(), allPlayers, p) + player.getName() + ": " + player.getPoints() + " pts;" + Print.ANSI_RESET);
        System.out.println("> " + Print.getPlayerColor(nickname, allPlayers, p) + "(You) " + nickname + ": " + p.getPoints() + " pts;" + Print.ANSI_RESET);

    }

    /**
     * Displays the scoreboard through TUI
     */
    private void commandScoreboard() {
        Print.scoreboardPrinter(allPlayers, p);
    }

    /**
     * Displays the cards on the table through TUI
     */
    private void commandTable() {
        System.out.println("> These are the cards on the table right now:");
        Print.drawChoicePrinter(commonGold, commonResource, goldDeck,resourceDeck);
    }

    // connection methods

    /**
     * Retrieves the nickname of the user
     * @return String representing a name
     */
    @Override
    public String getNickname() {
        return nickname;
    }

    /**
     * Retrieve the status of the user (online / offline)
     * @return True for online, false otherwise
     */
    @Override
    public boolean getOnline() {
        return connected;
    }

    /**
     * Retrieve the status of the user (online / offline)
     * @param b true to set the client online, false to set the client offline.
     */
    @Override
    public void setOnline(boolean b) {
        connected = b;
    }

    /**
     * Sets the nickname of the user
     * @param nick the nickname to set for the client.
     */
    @Override
    public void setNickname(String nick) {
        nickname = nick;
    }

    /**
     * Not implemented in this class by VirtualView
     */
    @Override
    public void setPing(boolean b) {

    }

    /**
     * Not implemented in this class by VirtualView
     */
    @Override
    public boolean getPing() {
        return false;
    }

    /**
     * Sets the starter status of the client. Not implemented in this class.
     */
    @Override
    public void setStarter(boolean b) throws RemoteException {
    }

    /**
     * Gets the nickname of the first client.
     *
     * @return the nickname of the first client.
     * @throws RemoteException if a remote communication error occurs.
     */
    @Override
    public String getNicknameFirst() throws RemoteException {
        return nickname;
    }

    /**
     * Gets the starter status of the client. Not implemented in this class
     */
    @Override
    public boolean getStarter() throws RemoteException {
        return false;
    }

    //util methods

    /**
     * Updates the values of a player tha is not the local player
     * @param player Player to update
     */
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
            allPlayers.add(player);
        }
    }

    // general connection methods

    /**
     * Receives an action from the server and put it in the queue or reply with a pong if it's a ping
     * @param actionFromServer the action to display handle
     * @throws RemoteException If there is a communication-related issue with the RMI server.
     */
    public void showAction(Action actionFromServer) throws RemoteException {
        try {
            if(actionFromServer.getType().equals(ActionType.PING)){
                server.sendAction(new PongAction(nickname));
            } else {
                serverActionsReceived.put(actionFromServer);
            }
        } catch (InterruptedException | NullPointerException ignored) { }
    }

    /**
     * Check the currente queue of actions and sends em to server, first in first out
     * @throws InterruptedException If the thread is interrupted while waiting for actions in the clientActionsToSend queue.
     * @throws RemoteException If there is a communication-related issue with the RMI server.
     */
    public void serverUpdateThread() throws InterruptedException, RemoteException {
        try {
            while (connectionFlagServer) {
                Action a = clientActionsToSend.take();
                server.sendAction(a);
            }
        } catch (InterruptedException | IOException e) {
            connectionFlagServer = false;
        }
    }

    /**
     * Countdown starting when all the players disconnect but one
     * After 30s if noone reconnects, the last player is automatically declared as the winner
     * and the game ends
     */
    public void waitingRoutineOneUser() {
        Runnable task = new Runnable() {
            int attempts = 30;
            volatile boolean restart = false;
            boolean stop = false;

            @Override
            public void run() {
                stop = false;
                while (!stop) {
                    try {
                        while (attempts != 0 && !restart) {
                            Thread.sleep(1000);
                            if (startWaitRoutine) {
                                attempts--;
                                if(!gui)
                                    System.out.println("Seconds before shutdown " + attempts + "...");
                                else
                                    Platform.runLater(() -> playController.genericAlert("Attempts remaining: " + attempts));
                            } else {
                                restart = true;
                                stop = true;
                                if(!gui)
                                    System.out.println("> Game resumed, minimum number of users is online.");
                                else
                                    Platform.runLater(() -> playController.genericAlert("Game resumed, minimum number of users is online."));
                            }
                        }
                        if (!restart) {
                            if(!gui)
                                Print.resultAsciiArt(true, Print.ANSI_YELLOW);
                            else {
                                Platform.runLater(() -> playController.genericAlert("You Won, all Users disconnected."));
                                p.setWinner(true);
                                Platform.runLater(() -> playController.setPlayer(p));
                                Platform.runLater(() -> playController.showResult());
                            }
                            System.exit(0);
                            break;
                        } else {
                            // Restart threads with default values
                            attempts = 10;
                            restart = false;
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };

        Thread thread = new Thread(task);
        thread.start();
    }


    //listening threads for gui

    /**
     * Creates a thread that keeps listening to the graphical application during the game
     * to determine when the player sends new messages
     * Then it sends the actions to the queue
     * @return The thread ready to be started
     */
    private Thread createThreadChatListener() {
        return new Thread(() -> {
            while (playController == null) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ignored) { }
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
        });
    }
    
    /**
     * Creates a thread that keeps listening to the graphical application during the starter card
     * choice phase to determine when the player has chosen the side of the starter card
     * Then it sends the action to the queue
     * @return The thread ready to be started
     */
    private Thread createThreadStartListener() {
        return new Thread(() -> {
            do {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ignored) { }
            } while (playController.starterChoice == 0);
            try {
                clientActionsToSend.put(new ChosenSideStarterCardAction(nickname, (playController.starterChoice == 1)));
            } catch (InterruptedException ignored) { }
            state = State.COMMANDS;
        });
    }

    /**
     * Creates a thread that keeps listening to the graphical application during the achievement choice phase
     * to determine when the player has chosen the secret achievement
     * Then it sends the action to the queue
     * @return The thread ready to be started
     */
    private Thread createThreadAchievementListener() {
        return new Thread(() -> {
            do {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            } while (playController.achievementChoice == 0);
            try {
                clientActionsToSend.put(new ChosenAchievementAction(nickname, playController.achievementChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1)));
            } catch (InterruptedException ignored) { }
            state = State.COMMANDS;
        });
    }

    /**
     * Creates a thread that keeps listening to the graphical application during the place phase
     * to determine when the player has chosen the card to place and where
     * Then it sends the action to the queue
     * @return The thread ready to be started
     */
    private Thread createThreadPlaceListener() {
        return new Thread(() -> {
            Platform.runLater(() -> playController.alertToPlace());
            PlacingCardAction a;
            do {
                a = playController.discoverPlace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            } while (a == null);
            try {
                clientActionsToSend.put(a);
            } catch (InterruptedException ignored) { }
            state = State.COMMANDS;
        });
    }

    /**
     * Creates a thread that keeps listening to the graphical application during the place phase after an
     * invalid placing by the player to determine when the player has chosen the card to place and where
     * Then it sends the action to the queue
     * @return The thread ready to be started
     */
    private Thread createThreadRePlaceListener() {
        return new Thread(() -> {
            Platform.runLater(() -> playController.alertToRePlace());
            PlacingCardAction a;
            do {
                a = playController.discoverPlace();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            } while (a == null);
            try {
                clientActionsToSend.put(a);
            } catch (InterruptedException ignored) { }
            state = State.COMMANDS;
        });
    }

    /**
     * Creates a thread that keeps listening to the graphical application during the draw phase
     * to determine when the player has chosen the card to draw in the drawing
     *  Then it sends the action to the queue
     * @return The thread ready to be started
     */
    private Thread createThreadDrawListener() {
        return new Thread(() -> {
            Platform.runLater(() -> playController.updateTableCards(commonGold, goldDeck, commonResource, resourceDeck));
            Platform.runLater(() -> playController.alertToDraw());
            ChosenDrawCardAction a;
            do {
                a = playController.discoverDraw();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException ignored) { }
            } while (a == null);
            try {
                clientActionsToSend.put(a);
            } catch (InterruptedException ignored) { }
        });
    }

}
