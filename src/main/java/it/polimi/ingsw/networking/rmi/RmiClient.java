package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.action.*;
import it.polimi.ingsw.action.Action;
import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.StarterCard;
import it.polimi.ingsw.util.Print;

import javax.swing.*;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

public class RmiClient extends UnicastRemoteObject implements VirtualView {

    enum State {
        WAIT,
        COMMANDS,
        GAMESIZE,
        STARTERCARD,
        DRAW,
        PLACE,
        CHOICE,
        END
    }

    private static int PORT = 1234;
    private Player p;
    private String nickname;
    private State state;
    private final VirtualServer server;
    private ArrayList<Player> allPlayers;
    private StarterCard starterCard;
    private Print customPrint;

    public RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = State.WAIT;
        this.allPlayers = new ArrayList<>();
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
        state = State.COMMANDS;
        this.runCli();
    }

    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        Message msg = null;
        Action a = null;
        try {
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
                            System.err.println("> Permission denied.");
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
                            //qui stamperò le carte con l'indice corrispondente
                            boolean checkIndex = false;
                            boolean checkSide = false;
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
                            if(side == 1){
                                p.getHand().get(index).setFront(true);
                            } else{
                                //mezzo overkill però non so bene come rimangono memorizzati i side delle carte dopo un po' che vengono usate
                                p.getHand().get(index).setFront(false);
                            }

                            // qui passerà sceglierà un valore (index) che corrisponde a una delle 3 carte in mano e indicherà la posizione nella tabella
                            a = new PlacingCardAction(p.getHand().get(index), row, column, nickname);
                            server.sendAction(a);
                            state = State.COMMANDS;
                        } else {
                            System.err.println("> Permission denied.");
                        }
                        break;
                    case "start":
                        if(state.equals(State.STARTERCARD)) {
                            customPrint.cardPrinter(starterCard, true);
                            customPrint.cardPrinter(starterCard, false);
                            do {
                                System.out.println("> Enter \"front\" or \"back\".");
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
                            System.err.println("> Permission denied.");
                        }
                        break;
                    default:
                        System.err.println("> Command unknown, write \"help\" for a list of commands.");
                        break;
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Invalid input.");
        }

    }

    //vanno cambiati tutti
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
                System.out.println("> Player " + ((JoiningPlayerAction)act).getPlayer() + " joined the game. " + ((JoiningPlayerAction)act).getCurrentPlayersNumber() + "/" + ((JoiningPlayerAction)act).getGameSize());
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
                    System.out.println("> Choose the side you want to place your starter card with command \"start\".");
                }
                break;
            case CHOSENACHIEVEMENT:
                break;
            case CHOOSEABLEACHIEVEMENTS:
                break;
            case HAND:
                break;
            case PLACEDCARD:
                if(act.getRecipient().equalsIgnoreCase(nickname)) {
                        // you placed a card
                } else {
                    // someone else place a card
                }
                break;
            case PLACEDCARDERROR:
                if(act.getAuthor().equalsIgnoreCase(nickname)) {
                    System.out.println(((PlacedErrorAction)act).getError());
                    state = State.PLACE;
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
