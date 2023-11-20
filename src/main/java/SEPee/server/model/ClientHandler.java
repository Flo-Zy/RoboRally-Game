package SEPee.server.model;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class ClientHandler implements Runnable {
    private Socket clientSocket;
    private List<ClientHandler> clients;
    private PrintWriter writer;

    public ClientHandler(Socket clientSocket, List<ClientHandler> clients) {
        this.clientSocket = clientSocket;
        this.clients = clients;

        try {
            this.writer = new PrintWriter(clientSocket.getOutputStream(), true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try (
                BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))
        ) {
            String message;
            while ((message = reader.readLine()) != null) {
                broadcast(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void broadcast(String message) {
        for (ClientHandler client : clients) {
            client.writer.println(message);
        }
    }
}