package it.polimi.ingsw.networking.socket;

import it.polimi.ingsw.controller.GameController;
import it.polimi.ingsw.networking.socket.VirtualServerSocket;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.net.Socket;

public class SocketClientHandler implements VirtualServerSocket {
    final GameController controller;
    final SocketServer server;
    final BufferedReader input;
    final VirtualViewSocket view;

    public SocketClientHandler(GameController controller, SocketServer server, BufferedReader input, BufferedWriter output) {
        this.controller = controller;
        this.server = server;
        this.input = input;
        this.view = new ClientProxy(output);
    }

    public void runVirtualView() throws IOException {
        String line;
        // Read message type
        while ((line = input.readLine()) != null) {
            // Read message and perform action
            switch (line) {
                case "connect" -> {}
                case "add" -> {
                    this.controller.add(Integer.parseInt(input.readLine()));
                    this.server.broadcastUpdate(this.controller.getCurrent());
                }
                case "reset" -> {
                    this.controller.reset();
                    this.server.broadcastUpdate(this.controller.getCurrent());
                }
                default -> System.err.println("[INVALID MESSAGE]");
            }
        }
    }

    @Override
    public void showValue(Integer number) {
        synchronized (this.view) {
            this.view.showValue(number);
        }
    }

    @Override
    public void reportError(String details) {
        synchronized (this.view) {
            this.view.reportError(details);
        }
    }

    @Override
    public void connect(VirtualViewSocket client) {

    }

    @Override
    public void add(Integer number) {

    }

    @Override
    public void reset() {

    }
}
