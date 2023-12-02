package SEPee.server.model;

import SEPee.serialisierung.Deserialisierer;
import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.Message;
import SEPee.serialisierung.messageType.PlayerAdded;
import SEPee.serialisierung.messageType.PlayerValues;
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
                        break;
                    case "PlayerValues":
                        System.out.println("Player Values erhalten");
                        PlayerAdded playerAdded = new PlayerAdded(1, "Hasan",4);
                        String serializedPlayerAdded = Serialisierer.serialize(playerAdded);
                        writer.println(serializedPlayerAdded);
                        break;
                    case "SetStatus":
                        System.out.println("Set Status");
                        break;
                    case "MapSelected":
                        System.out.println("Map Selected");
                        break;
                    case "SendChat":
                        System.out.println("Send Chat");
                        break;
                    case "PlayCard":
                        System.out.println("Play Card");
                        break;
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