package SEPee.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.Movement;
import SEPee.server.model.card.Decks;
import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private List<List<List<Field>>> gameBoard;
    private ArrayList<Player> playerList;
    private ArrayList<Player> priorityPlayerList;
    private int playerIndex;
    private int currentPhase;
    private Player currentPlayer;
    //private ArrayList<ProgCard> initialProgDeck;
    private int virus;
    private int spam;
    private int trojanHorse;
    private int wurm;
    //private DamageDecks damageDecks;
    //private UpgradeShop upgradeShop;
    //private ArrayList<SpecialCard> specialCardsDeck;

    public Game(ArrayList<Player> playerList, List<List<List<Field>>> gameBoard){
        this.gameBoard = gameBoard;
        this.playerList = playerList;
        this.playerIndex = 0;
        this.currentPhase = 0;
        this.currentPlayer = playerList.get(playerIndex);
        this.spam = 38;
        this.virus = 18;
        this.trojanHorse = 12;
        this.wurm = 6;
    }

    public void nextCurrentPhase(){
        currentPhase++;

        if(currentPhase == 3) {
            currentPhase = 1;

        } else if (currentPhase == 1) { // Überspringen der Upgrade Phase
            currentPhase++;
        } else if (currentPhase == 2) { // Programming Phase: Mische für jeden player das priorityPlayerList.get(i).getPlayerMat().getProgDeck();
            for (Player player : priorityPlayerList) {
                player.getPlayerMat().setProgDeck(new Decks().getDeck());
            }
        }
    }

    public void setNextPlayersTurn(){ //nextPlayer ist keine getter methode!
        if(currentPhase == 0) { // 0: Starting-Phase
            playerIndex++;
            currentPlayer = playerList.get(playerIndex);
            //return currentPlayer;
        } else { // 1/2/3: Upgrade/Programming/Activation-Phase
            // select nextPlayer closest to antenna
            playerIndex = 0;
            // update der priorityPlayerList
            checkPriorities();
            currentPlayer = priorityPlayerList.get(playerIndex);
            playerIndex++;
        }
    }

    public void checkPriorities() {
        int antennaX = 0;
        int antennaY = 4;
        priorityPlayerList.clear();

        // TreeMap, die automatisch alles Player anhand Distanz und Winkel von der Antenne aus sortiert
        TreeMap<Double, Player> sortedPlayers = new TreeMap<>();

        // Bestimme Position von jedem Player Roboter + Berechne Distanz und Winkel zu der Antenne
        for (Player player : playerList) {
            int distanceOfRobotFromAntenna = calculateDistance(player.getRobot().getX(), player.getRobot().getY(), antennaX, antennaY);
            double angleFromAntenna = calculateAngle(player.getRobot().getX(), player.getRobot().getY(), antennaX, antennaY);

            // Schlüsselwert von Player für TreeMap erstellen aus Distanz und Winkel zu der Antenne
            double playerKey = distanceOfRobotFromAntenna + angleFromAntenna / 1000.0; // /1000 damit beim Sortieren der Winkel nicht zu viel Einfluss hat

            sortedPlayers.put(playerKey, player);
        }

        // Hinzufügen der Player von der TreeMap zur priorityPlayerList (sortiert nach Distanz und Winkel)
        priorityPlayerList.addAll(sortedPlayers.values());
    }

    private int calculateDistance(int x1, int y1, int x2, int y2) {
        // Berechnung Distanz
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }
    private double calculateAngle(int x, int y, int centerX, int centerY) {
        // Berechnung Winkel in Radian
        double angle = Math.atan2(y - centerY, x - centerX);
        // Winkel -> Grad und normalisieren zu [0, 360]
        double degrees = Math.toDegrees(angle);
        degrees = (degrees + 360) % 360;
        // Grad -> Radian
        return Math.toRadians(degrees);
    }

    public void activateElements() {
    }

    public void setGameBoard(int boardId) {
    }

    public void shuffle() {
    }

    /** TIMER:
     long start,end;
     double tim;
     start=System.currentTimeMillis();
     end=System.currentTimeMillis();
     tim=(end-start)/1000.0;
     */
}