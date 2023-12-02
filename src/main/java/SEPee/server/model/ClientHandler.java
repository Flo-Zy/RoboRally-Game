package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.messageType.Message;
import com.google.gson.Gson;
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
            String serializedReceivedChat;
            while ((serializedReceivedChat = reader.readLine()) != null) {
                Message deserializedReceivedString = Deserialisierer.deserialize(serializedReceivedChat, Message.class);
                String input = deserializedReceivedString.getMessageType();
                //System.out.println("2");
                switch (input) {
                    //HelloServer wird oben behandelt beim Verbindungsaufbau
                    case "Alive":
                        System.out.println("Alive");
                        continue;
                    case "PlayerValues":
                        System.out.println("Player Values");
                        continue;
                    case "SetStatus":
                        System.out.println("Set Status");
                        continue;
                    case "MapSelected":
                        System.out.println("Map Selected");
                        continue;
                    case "SendChat":
                        System.out.println("Send Chat");
                        continue;
                    case "PlayCard":
                        System.out.println("Play Card");
                        continue;
                    default:
                        //Error-JSON an Client
                        System.out.println("Unknown command");
                        break;
                }
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