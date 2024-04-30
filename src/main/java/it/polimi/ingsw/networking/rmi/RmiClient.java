package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.action.Action;
import it.polimi.ingsw.action.ActionType;
import it.polimi.ingsw.model.Message;
import it.polimi.ingsw.model.Player;

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
    private boolean reqChat;

    public RmiClient(VirtualServer server) throws RemoteException {
        this.server = server;
        this.p = new Player();
        this.nickname = "";
        state = State.WAIT;
    }

    public static void main(String[] args) throws RemoteException, NotBoundException {
        final String serverName = "GameServer";
        Registry registry = LocateRegistry.getRegistry("127.0.0.1", PORT);
        VirtualServer server = (VirtualServer) registry.lookup(serverName);
        new RmiClient(server).run();
    }

    //run() e runCli() sono specifici all'istanza del rmiClient creato, va bene anche private il metodo tanto non lo dobbiamo esporre
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

    //synch
    private void runCli() throws RemoteException {
        Scanner scan = new Scanner(System.in);
        Message msg = null;
        Action a = null;
        try {
            while(true) {
                if(state == State.COMMANDS) {
                    String line = scan.nextLine();
                    if (state != State.COMMANDS) {
                        System.out.print("Sorry, re-enter your answer: ");
                        continue;
                    }
                    if (line.trim().isEmpty())
                        continue;
                    StringTokenizer st = new StringTokenizer(line);
                    String command = st.nextToken();
                    command.toLowerCase();
                    switch (command) {
                        case "chat":
                            if (line.length() > 5) {
                                msg = new Message(line.substring(5), nickname);
                                a = new Action(ActionType.CHATMESSAGE, msg, nickname, null);
                                server.sendAction(a);
                            }
                            break;
                        case "getchat":
                            a = new Action(ActionType.ASKINGCHAT, null, nickname, null);
                            server.sendAction(a);
                            break;
                        case "whisper":
                            command = st.nextToken();
                            msg = new Message(line.substring(7 + command.length() + 1), nickname, command);
                            a = new Action(ActionType.CHATMESSAGE, msg, nickname, command);
                            server.sendAction(a);
                            System.out.println("\033[1m" + ">>> PRIVATE to " + msg.getRecipient() + " > " + msg.toString() + "\033[0m");
                            break;
                        default:
                            System.out.println("Command unknown");
                    }
                }
            }
        } catch (NoSuchElementException e) {
            System.out.println("Invalid input");
        }

    }

    @Override
    //synch
    public void showAction(Action act) throws RemoteException{
        //synchronized (this)
        Scanner scan = new Scanner(System.in);
        //da capire la sincronizzazione
        switch(act.getType()){
            case WHOLECHAT:
                ArrayList<Message> chat = ((ArrayList<Message>) act.getObject());
                if (chat.isEmpty()) {
                    System.out.println("\033[1m" + ">>> " + "Public chat is empty" + "\033[0m");
                } else {
                    System.out.println("\033[1m" + ">>> " + "------- PUBLIC CHAT -------" + "\033[0m");
                    for (Message m : chat) {
                        System.out.println("\033[1m" + ">>> " + m.toString() + "\033[0m");
                    }
                }
                break;

            case JOININGPLAYER:
                System.out.println("> Player " + act.getObject() + " joined the game. ");
                break;

            case ASKINGPLAYERSNUMBER:
                state = State.WAIT;
                System.out.print("> Enter desired players number: ");
                int playnum = Integer.parseInt(scan.nextLine());
                while(playnum < 2 || playnum > 4) {
                    System.out.print("> The number must be between 2 and 4: ");
                    playnum = Integer.parseInt(scan.nextLine());
                }
                server.sendAction(new Action(ActionType.CHOSENPLAYERSNUMBER, playnum, null, null));
                state = State.COMMANDS;
                break;
            case CHATMESSAGE:
                Message m = (Message) act.getObject();
                if (m.getRecipient().isEmpty()) {
                    System.out.println("\033[1m" + ">>> " + m.toString() + "\033[0m");
                } else {
                    System.out.println("\033[1m" + ">>> PRIVATE > "  + m.toString() + "\033[0m");
                }
                break;
            case CHOSENACHIEVEMENT:
                break;
            case CHOOSEABLEACHIEVEMENTS:
                break;
            case HAND:
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
