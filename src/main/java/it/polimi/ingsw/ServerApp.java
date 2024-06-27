package it.polimi.ingsw;

import it.polimi.ingsw.networking.WebServer;

/**
 * ServerApp is the entry point for starting the server of the game.
 * It initializes the server with specified or default ports for socket and RMI connections.
 */
public class ServerApp {

    /**
     * The default port number for the server socket connection.
     */
    private static final int SOCKET_DEFAULT_PORT = 7171;

    /**
     * The default port number for the server RMI connection.
     */
    private static final int RMI_DEFAULT_PORT = 6969;

    /**
     * The main method to start the server.
     * It reads command-line arguments to set the ports for socket and RMI connections.
     * If the arguments are not correctly provided, it uses the default port values.
     *
     * @param args the command-line arguments for specifying ports. The expected format is "--p <socketPort> <rmiPort>".
     */
    public static void main(String[] args) {
        int numParameters = args.length;
        int socketPort = SOCKET_DEFAULT_PORT;
        int rmiPort = RMI_DEFAULT_PORT;

        if (numParameters == 3 && args[0].equals("--p")) {
            socketPort = Integer.parseInt(args[1]);
            rmiPort = Integer.parseInt(args[2]);
        } else {
            System.out.println("Warning: Bad input parameters. Using default values.");
        }

        try {
            int[] ports = {rmiPort, socketPort};
            WebServer webServer = new WebServer(ports);
        } catch (Exception ignored) {
            System.out.println("Fatal Error! Unknown error occurred while running the web server.");
        }
    }
}
