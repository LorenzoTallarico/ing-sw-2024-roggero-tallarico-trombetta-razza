package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.networking.action.*;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.networking.action.toclient.*;
import it.polimi.ingsw.networking.action.toserver.*;
import it.polimi.ingsw.util.Print;

import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RmiClient extends UnicastRemoteObject implements VirtualView {


    enum State {
        COMMANDS,
        GAMESIZE,
        STARTERCARD,
        DRAW,
        PLACE,
        ACHIEVEMENTSCHOICE,
        END
    }

    private final static int PORT = 1234;
    private Player p;
    private String nickname;
    private State state;
    private final VirtualServer server;
    private ArrayList<Player> allPlayers;
    private StarterCard starterCard;
    private final Print customPrint;
    private ArrayList<AchievementCard> achievements; // the first element is the secret achievement
    private ArrayList<AchievementCard> choosableAchievements;
    private ArrayList<ResourceCard> commonResource;
    private ArrayList<GoldCard> commonGold;
    private boolean goldDeck;
    private boolean resourceDeck;


    public RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = State.COMMANDS;
        this.allPlayers = new ArrayList<>();
        customPrint = new Print();
        achievements = new ArrayList<>();
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        new RmiClient(server).run();
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

    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        Message msg = null;
        Action a = null;

        while(true) {
            String line = scan.nextLine();
            if (line.trim().isEmpty())
                continue;
            StringTokenizer st = new StringTokenizer(line);
            String command = st.nextToken().toLowerCase();
            switch (command) {
                case "gamesize":
                    if (state == State.GAMESIZE) {
                        int playnum = Integer.parseInt(st.nextToken());
                        while(playnum < 2 || playnum > 4) {
                            System.out.print("> The number must be between 2 and 4: ");
                            playnum = Integer.parseInt(scan.nextLine());
                        }
                        a = new ChosenPlayersNumberAction(playnum);
                        server.sendAction(a);
                        System.out.println("> Game's size set to " + playnum + ".");
                        state = State.COMMANDS;
                    } else {
                        System.err.println("> Permission denied, game's size already set.");
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
                    System.out.println("\033[1m" + ">>> PRIVATE to " + msg.getRecipient() + " > " + msg.toString() + "\033[0m");
                    break;
                case "help":
                    System.out.println("> ------- COMMANDS LIST -------");
                    System.out.printf("> %-30s%s\n","chat ...","to send a public message to everyone.");
                    System.out.printf("> %-30s%s\n","whisper x ...","to send a private message to x.");
                    System.out.printf("> %-30s%s\n","getchat","to retrieve a full log of the public chat.");
                    System.out.printf("> %-30s%s\n","gamesize x","to choose the number x of players who will play the game.");
                    System.out.printf("> %-30s%s\n","start","to choose the side of the starter card.");
                    //System.out.printf("%-30s%s",  ,   );
                    /*System.out.println("chat ...\t\tto send a public message to everyone.");
                    System.out.println("whisper x ...\t\tto send a private message to x.");
                    System.out.println("getchat\t\tto retrieve a full log of the public chat.");
                    System.out.println("gamesize\t\tto choose the number of players who will play the game.");*/
                    break;
                case "place":
                    if(state.equals(State.PLACE)) {
                        System.out.println("> This is your playground:");
                        customPrint.playgroundPrinter(p.getArea());
                        System.out.println("> This is your hand:");
                        //to do function that prints hand and achievements in line
                        for(Card card : p.getHand()) {
                            customPrint.cardPrinter(card, true);
                            customPrint.cardPrinter(card, false);
                        }
                        for(AchievementCard achievement : achievements)
                            customPrint.cardPrinter(achievement, true);
                        boolean checkIndex = false;
                        boolean checkSide = false;
                        boolean chosenSide = false;
                        int index = 0;
                        int side = 0;    //DA CONTROLLARE --> FRONT = 1, BACK = 0
                        int row = 0;
                        int column = 0;
                        do {
                            System.out.print("> Enter the number of the card, the row and the column where you want to place the card." +
                                            "\n Follow this format -card -side -row -column: ");
                            line = scan.nextLine();
                            st = new StringTokenizer(line);
                            index = Integer.parseInt(st.nextToken());
                            side = Integer.parseInt(st.nextToken());
                            row = Integer.parseInt(st.nextToken());
                            column = Integer.parseInt(st.nextToken());
                            if(index >= 0 && index <= 2){
                                checkIndex = true;
                            } else {
                                System.out.println("> Index not valid.");
                            }
                            if(side >= 0 && side <= 1){
                                checkSide = true;
                            } else {
                                System.out.println("> Side not valid.");
                            }
                        } while(!checkIndex && !checkSide);
                        //DA CAMBIARE NEL MODEL IL METODO PLACE?????? --> DOVREBBE SETTARE IL SIDE DELLA CARTA ALL'INIZIO (MODIFICARE TUTTE LE CHIAMATE)
                        //qua in realtà potrebbe semplicemente settare il side della carta prima di passarla, come qui sotto
                        if(side == 1)
                            chosenSide = true;
                        // qui passerà sceglierà un valore (index) che corrisponde a una delle 3 carte in mano e indicherà la posizione nella tabella
                        a = new PlacingCardAction(p.getHand().get(index), chosenSide, row, column, nickname);
                        server.sendAction(a);
                        //non posso cambiare qua lo state perché non so se va a buon fine il place()
                        state = State.COMMANDS;
                    } else {
                        System.err.println("> Permission denied, you can't place a card right now.");
                    }
                    break;
                case "start":
                    if(state.equals(State.STARTERCARD)) {
                        customPrint.cardPrinter(starterCard, true);
                        customPrint.cardPrinter(starterCard, false);
                        do {
                            System.out.print("> Enter \"front\" or \"back\": ");
                            line = scan.nextLine();
                        } while(!(line.equalsIgnoreCase("front") || (line.equalsIgnoreCase("f"))
                                || (line.equalsIgnoreCase("back")) || (line.equalsIgnoreCase("b"))));
                        if(line.equalsIgnoreCase("front") || (line.equalsIgnoreCase("f")))
                            starterCard.setFront(true);
                        else
                            starterCard.setFront(false);
                        server.sendAction(new ChosenSideStarterCardAction(nickname, starterCard.isFront()));
                        p.getArea().setSpace(starterCard, 40, 40);
                        state = State.COMMANDS;
                    } else {
                        System.err.println("> Permission denied, you can't choose the starter card right now.");
                    }
                    break;
                case "achievement":
                    if(state.equals(State.ACHIEVEMENTSCHOICE)) {
                        customPrint.cardPrinter(choosableAchievements.get(0), true);
                        customPrint.cardPrinter(choosableAchievements.get(1), true);
                        int achChoice = 0;
                        while(achChoice < 1 || achChoice > 2) {
                            System.out.print("> Enter 1 or 2 to choose your secret achievement: ");
                            achChoice = Integer.parseInt(scan.nextLine());
                        }
                        a = new ChosenAchievementAction(nickname, achChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1));
                        achievements.add(0, achChoice == 1 ? choosableAchievements.get(0) : choosableAchievements.get(1));
                        server.sendAction(a);
                        state = State.COMMANDS;
                    } else {
                        System.err.println("> Permission denied, you can't choose the secret achievement right now.");
                    }
                    break;
                case "draw":
                    if(state.equals(State.DRAW)) {
                        for(Card c : commonGold)
                            customPrint.cardPrinter(c, true);
                        for(Card c : commonResource)
                            customPrint.cardPrinter(c, true);
                        //stampare qualcosa che rappresenti
                        int drawChoice = 0;
                        do {
                            //da stampare poi in TUI la corrispondenza tra drawChoice e carta disegnata
                            System.out.print("> Enter 1, 2, 3, 4, 5 or 6 to draw your card: ");
                            drawChoice = Integer.parseInt(scan.nextLine());
                            if(drawChoice < 1 || drawChoice > 6)
                                System.out.println("> Please enter a valid number");
                        } while(drawChoice < 1 || drawChoice > 6);
                        //qua non ho ancora capito se volete Choose o Chosen nel dubbio metto Chosen
                       // !!!!----------- CHOSEN, è CHOSEN !!!!😘😘😘
                        //a = new ChosenDrawCardAction(nickname, ????????);
                        //qua o con uno switch o un lungo if-elseif vedere che parametri passare in Action 'a'
                        // il problema è decidere come fare se il player decide di pescare una carta da un deck (magari un altra action o far passare altri parametri)
                        server.sendAction(a);
                        state = State.COMMANDS;
                        //con l'action che poi viene mandata va poi anche gestito il passaggio dal turno di un giocatore al successivo
                    } else {
                        System.err.println("> Permission denied, you can't draw a card right now.");
                    }
                    break;
                default:
                    System.err.println("> Command unknown, write \"help\" for a list of commands.");
                    break;
            }
        }

    }

    @Override
    public void showAction(Action act) throws RemoteException{
        switch(act.getType()){
            case WHOLECHAT:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    ArrayList<Message> chat = ((WholeChatAction) act).getMessages();
                    if (chat.isEmpty()) {
                        System.out.println("\033[1m" + ">>> " + "Public chat is empty" + "\033[0m");
                    } else {
                        System.out.println("\033[1m" + ">>> " + "-------- PUBLIC CHAT --------" + "\033[0m");
                        for (Message m : chat) {
                            System.out.println("\033[1m" + ">>> " + m.toString() + "\033[0m");
                        }
                    }
                }
                break;
            case JOININGPLAYER:
                System.out.println("> Player " + ((JoiningPlayerAction)act).getPlayer() + " joined the game. " + ((JoiningPlayerAction)act).getCurrentPlayersNumber() + "/" + (((JoiningPlayerAction)act).getGameSize() == 0 ? "?" : ((JoiningPlayerAction)act).getGameSize()));
                break;
            case ASKINGPLAYERSNUMBER:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    state = State.GAMESIZE;
                    System.out.println("> Enter desired players number with command \"gamesize x\".");
                }
                break;
            case CHATMESSAGE:
                if(act.getRecipient().isEmpty() || act.getRecipient().equalsIgnoreCase(nickname)) {
                    Message m = ((ChatMessageAction) act).getMessage();
                    if (m.getRecipient().isEmpty()) {
                        System.out.println("\033[1m" + ">>> " + m.toString() + "\033[0m");
                    } else {
                        System.out.println("\033[1m" + ">>> PRIVATE > " + m.toString() + "\033[0m");
                    }
                }
                break;
            case CHOOSESIDESTARTERCARD:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    starterCard = ((ChooseSideStarterCardAction)act).getCard();
                    state = State.STARTERCARD;
                    System.out.println("> Choose the side you want to place your starter card with command \"start\".");}
                break;
            case CHOOSEABLEACHIEVEMENTS:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    choosableAchievements = ((ChooseableAchievementsAction)act).getAchievements();
                    achievements.addAll(((ChooseableAchievementsAction) act).getCommonGoals());
                    state = State.ACHIEVEMENTSCHOICE;
                    System.out.println("> Choose your secret achievement with the command \"achievement\".");
                }
                break;
            case HAND:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    p.setHand(((HandAction)act).getHand());
                }
                break;
            case ASKINGPLACE:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    p = ((AskingPlaceAction)act).getPlayer();
                    state = State.PLACE;
                    System.out.println("> It's your time to play, enter \"place\" to place a card.");
                }
                break;
            case PLACEDCARD:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    System.out.println("> You placed a " + ((PlacedCardAction)act).getCard().getClass().toString() + "in [" +
                            ((PlacedCardAction)act).getRow() + "][" + ((PlacedCardAction)act).getColumn() + "]");
                    p = ((PlacedCardAction)act).getPlayer();
                    // QUI SI STAMPERA' IL PLAYGROUNG
                } else {
                    // someone else place a card
                    System.out.println("> Player " + ((PlacedCardAction)act).getPlayer().getName() + " placed a " +
                            ((PlacedCardAction)act).getCard().getClass().toString() + " in [" + ((PlacedCardAction)act).getRow() + "][" + ((PlacedCardAction)act).getColumn() + "]");
                }
                break;
            case PLACEDCARDERROR:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                    System.out.println(((PlacedErrorAction)act).getError());
                    state = State.PLACE;
                }
                break;
            case ASKINGDRAW:
                if(act.getRecipient().equalsIgnoreCase(nickname)){
                    System.out.println("> Choose the card you want to draw with the command \"draw\".");
                    state = State.DRAW;
                    this.commonGold = ((AskingDrawAction)act).getCommonGold();
                    this.goldDeck = ((AskingDrawAction)act).isCommonGoldEmpty();
                    this.commonResource = ((AskingDrawAction)act).getCommonResource();
                    this.resourceDeck = ((AskingDrawAction)act).isCommonResourceEmpty();
                }
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

}
