package SEPee.server.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

import SEPee.server.model.field.Field;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;

/**
 * class to represent a started game
 * @author Hasan, Maximilian, Florian, Felix, Franziska
 */
@Getter
@Setter
public class Game implements Robot.RobotPositionChangeListener {
    private List<List<List<Field>>> gameBoard;
    @Getter
    private ArrayList<Player> playerList;
    private int playerIndex;
    private int currentPhase;
    private Player currentPlayer;
    private int virus;
    private int spam;
    private int trojanHorse;
    private int wurm;
    private GameBoard boardClass;

    public Game(ArrayList<Player> playerList, List<List<List<Field>>> gameBoard, GameBoard boardClass){
        this.gameBoard = gameBoard;
        this.playerList = playerList;
        this.playerIndex = 0;
        this.currentPhase = 0;
        this.currentPlayer = playerList.get(playerIndex);
        this.spam = 38;
        this.virus = 18;
        this.trojanHorse = 12;
        this.wurm = 6;
        this.boardClass = boardClass;
    }

    /**
     * sets the next phase of the game
     * @author Hasan, Felix
     */
    public void nextCurrentPhase(){
        if(currentPhase == 3) {


            double millisToSleep = playerList.size() * 3750 - 5; // every animation is 750 millis long, times 5 registers, times the player amount

            /*try {
                Thread.sleep((long) millisToSleep);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }*/

            currentPhase = 2;
        } else if (currentPhase == 0) { // Überspringen der Upgrade Phase
            currentPhase = 2;
        } else if (currentPhase == 2) { // Programming Phase
            currentPhase++;
            // jetzt in AktivierungsPhase

            ServerLogger.writeToServerLog("Wir sind in der Phase:" + currentPhase);
        }
    }

    /**
     * set the next player's turn
     * @author Maximilian, Hasan
     */
    public void setNextPlayersTurn(){
        if(currentPhase == 0) {
            playerIndex++;
            currentPlayer = playerList.get(playerIndex);
        } else {
            // select nextPlayer closest to antenna
            playerIndex = 0;
            checkPriorities();
            currentPlayer = playerList.get(playerIndex);
            playerIndex++;
        }
        if(Server.getDisconnectedPlayer().contains(currentPlayer)){
            setNextPlayersTurn();
        }
    }

    /**
     * check the priorities of the players
     * @author Maximilian
     */
    public void checkPriorities() {
        int antennaX;
        int antennaY;
        if(boardClass.getBordName().equals("Death Trap")) {
            antennaX = 12;
            antennaY = 5;
        }else{
            antennaX = 0;
            antennaY = 4;
        }

        TreeMap<Double, Player> sortedPlayers = new TreeMap<>();

        for (Player player : playerList) {
            int distanceOfRobotFromAntenna = calculateDistance(player.getRobot().getX(), player.getRobot().getY(), antennaX, antennaY);
            double angleFromAntenna = calculateAngle(player.getRobot().getX(), player.getRobot().getY(), antennaX, antennaY);

            double playerKey = distanceOfRobotFromAntenna + angleFromAntenna / 1000.0;

            sortedPlayers.put(playerKey, player);
        }

        playerList.clear();
        playerList.addAll(sortedPlayers.values());
    }

    /**
     * calculate the distance to the antenna
     * @param x1 robot x coordinate
     * @param y1 robot y coordinate
     * @param x2 antenna x coordinate
     * @param y2 antenna y coordinate
     * @return the distance
     * @author Maximilian
     */
    private int calculateDistance(int x1, int y1, int x2, int y2) {
        return (int) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * calculate the angle towards the antenna
     * @param x robot x coordinate
     * @param y robot y coordinate
     * @param centerX antenna x coordinate
     * @param centerY antenna y coordinate
     * @return the angle
     * @author Maximilian
     */
    private double calculateAngle(int x, int y, int centerX, int centerY) {
        double angle = Math.atan2(y - centerY, x - centerX);
        double degrees = Math.toDegrees(angle);
        degrees = (degrees + 360) % 360;
        return Math.toRadians(degrees);
    }

    /**
     * set the new position of the robot
     * @param robot the robot that moved
     * @author Felix
     */
    @Override
    public void onRobotPositionChange(Robot robot) {
        for (Player player : playerList) {
            if (player.getRobot() == robot) {
                player.setRobot(robot);
                break;
            }
        }
    }
}