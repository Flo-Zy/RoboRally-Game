package SEPee.server.model;

import SEPee.server.model.card.Card;
import SEPee.server.model.card.Decks;
import lombok.Getter;
import lombok.Setter;

import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * the class representing a player
 * @author Felix, Franziska, Maximilian, Hasan
 */
@Getter
@Setter
public class Player {
    private String name;
    private int id;
    private boolean ready;
    @Getter
    @Setter
    private Robot robot;
    private int figure;
    private Card[] hand;
    @Getter
    @Setter
    private PlayerMat playerMat;
    private int checkpointTokens;
    private static Map<Socket, Integer> socketIdMap = new ConcurrentHashMap<>();
    @Getter
    @Setter
    private boolean reboot = false;
    @Getter
    @Setter
    private int damageCounter = 0;
    private int energyCubes = 5;
    public Player(String name, int id, int figure){
        this.name=name;
        this.id=id;
        this.figure= figure;
        this.ready = false;
        this.playerMat = new PlayerMat(new Decks().getDeck());
        this.robot = new Robot(0,0, "left" );
    }

    /**
     * matches the socket to a player id
     * @param socket the socket to match
     * @param clientId the client id to match
     * @author Felix
     */
    public static void associateSocketWithId(Socket socket, int clientId) {
        socketIdMap.put(socket, clientId);
    }

    /**
     * tells you which client id belongs to a socket
     * @param socket the socket you want a client id for
     * @return client id that matches the socket
     * @author Felix
     */
    public static int getClientIdFromSocket(Socket socket) {
        return socketIdMap.getOrDefault(socket, 666); // Returns 666 if socket ID not found
    }
}
