package SEPee.server.model;

import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
public class Player {
    private String name;
    private int id;
    private boolean ready;
    private Robot robot;
    private int figure;
    private Card[] hand;
    private PlayerMat playerMat;
    private int checkpointTokens;
    static Map<Socket, Integer> socketIdMap;


    public Player(String name, int id, int figure){
        this.name=name;
        this.id=id;
        this.figure=figure;
        this.ready = false;
        this.socketIdMap = new HashMap<>(); // Initialize the HashMap

    }

    public void associateSocketWithId(Socket socket, int clientId) {
        socketIdMap.put(socket, clientId);
    }

    public static int getClientIdFromSocket(Socket socket) {
        return socketIdMap.getOrDefault(socket, 666); // Returns 666 if socket ID not found
    }

    public void draw(){}
    public void fillRegister(Card[] chosenCards){}
    public void discard(){}
    public void examineDamageCards(){}
    public void rebootRobot(){}
}
