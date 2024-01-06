package SEPee.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.Movement;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.Decks;
import SEPee.server.model.card.upgradeCard.UpgradeCard;
import SEPee.server.model.field.Field;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game implements Robot.RobotPositionChangeListener {
    private List<List<List<Field>>> gameBoard;
    @Getter
    private ArrayList<Player> playerList;
    // private ArrayList<Player> priorityPlayerList;
    private int playerIndex;
    private int currentPhase;
    private Player currentPlayer;
    //private ArrayList<ProgCard> initialProgDeck;
    private int virus;
    private int spam;
    private int trojanHorse;
    private int wurm;
    private GameBoard boardClass;
    //private DamageDecks damageDecks;
    //private UpgradeShop upgradeShop;
    //private ArrayList<SpecialCard> specialCardsDeck;

    public Game(ArrayList<Player> playerList, List<List<List<Field>>> gameBoard, GameBoard boardClass){
        this.gameBoard = gameBoard;
        this.playerList = playerList;
        this.playerIndex = 0;
        this.currentPhase = 0;
        this.currentPlayer = playerList.get(playerIndex);
        //this.spam = 38;
        //Tester
        this.spam = 1;
        this.virus = 18;
        this.trojanHorse = 12;
        //this.wurm = 6;
        //Tester
        this.wurm = 1;
        this.boardClass = boardClass;

    }

    public void nextCurrentPhase(){
        if(currentPhase == 3) {
            currentPhase = 2;
        } else if (currentPhase == 0) { // Überspringen der Upgrade Phase
            currentPhase = 2;
        } else if (currentPhase == 2) { // Programming Phase
            currentPhase++;
            // jetzt in AktivierungsPhase




            System.out.println("Wir sind in der Phase:" + currentPhase);

            //set teleports or clients



        }
    }

    public void setNextPlayersTurn(){
        if(currentPhase == 0) { // 0: Starting-Phase
            playerIndex++;
            currentPlayer = playerList.get(playerIndex);
            //return currentPlayer;
        } else { // 1/2/3: Upgrade/Programming/Activation-Phase
            // select nextPlayer closest to antenna
            playerIndex = 0;
            // update der priorityPlayerList
            checkPriorities(); // update der playerList mit Prioritäten
            currentPlayer = playerList.get(playerIndex);
            playerIndex++;
        }
    }

    public void checkPriorities() {
        int antennaX = 0;
        int antennaY = 4;

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
        playerList.clear();
        playerList.addAll(sortedPlayers.values());
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

    @Override
    public void onRobotPositionChange(Robot robot) {
        for (Player player : playerList) {
            if (player.getRobot() == robot) {
                player.setRobot(robot);
                break; // Stop looping once the corresponding player is found and updated
            }
        }
    }
    /** TIMER:
     long start,end;
     double tim;
     start=System.currentTimeMillis();
     end=System.currentTimeMillis();
     tim=(end-start)/1000.0;
     */
}