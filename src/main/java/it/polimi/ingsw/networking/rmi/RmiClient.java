package it.polimi.ingsw.networking.rmi;

import it.polimi.ingsw.action.*;
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
        GAMESIZE,
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
                            server.sendAction(new Action(ActionType.CHOSENPLAYERSNUMBER, playnum, null, null));
                            System.out.println("> Game's size set to " + playnum + ".");
                            state = State.COMMANDS;
                        } else {
                            System.err.println("> Permission denied.");
                        }
                        break;
                    case "chat":
                        if (line.length() > 5) {
                            msg = new Message(line.substring(5), nickname);
                            a = new Action(ActionType.CHATMESSAGE, msg, nickname, null);
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
                        //System.out.printf("%-30s%s",  ,   );
                        /*System.out.println("chat ...\t\tto send a public message to everyone.");
                        System.out.println("whisper x ...\t\tto send a private message to x.");
                        System.out.println("getchat\t\tto retrieve a full log of the public chat.");
                        System.out.println("gamesize\t\tto choose the number of players who will play the game.");*/
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
                ArrayList<Message> chat = ((WholeChatAction)act).getMessages();
                if (chat.isEmpty()) {
                    System.out.println("\033[1m" + ">>> " + "Public chat is empty" + "\033[0m");
                } else {
                    System.out.println("\033[1m" + ">>> " + "-------- PUBLIC CHAT --------" + "\033[0m");
                    for (Message m : chat) {
                        System.out.println("\033[1m" + ">>> " + m.toString() + "\033[0m");
                    }
                }
                break;
            case JOININGPLAYER:
                System.out.println("> Player " + act.getObject() + " joined the game.");
                break;
            case ASKINGPLAYERSNUMBER:
                state = State.GAMESIZE;
                System.out.println("> Enter desired players number with command \"gamesize x\".");
                break;
            case CHATMESSAGE:
                Message m = ((ChatMessageAction)act).getMessage();
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
