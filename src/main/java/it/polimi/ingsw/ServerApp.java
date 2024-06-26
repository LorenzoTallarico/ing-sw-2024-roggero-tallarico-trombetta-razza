package it.polimi.ingsw;

import it.polimi.ingsw.networking.WebServer;

public class ServerApp {

    private static final int SOCKET_DEFAULT_PORT = 7171; // Default server socket port
    private static final int RMI_DEFAULT_PORT = 6969; // Default server RMI port


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
        } catch (Exception e) {
            System.out.println("Fatal Error! Unknown error occurred while running the web server.");
            e.printStackTrace();
        }
    }
}
