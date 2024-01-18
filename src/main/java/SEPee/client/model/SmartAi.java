package SEPee.client.model;

import SEPee.serialisierung.messageType.PlayerTurning;
import SEPee.server.model.Player;
import SEPee.server.model.Server;
import SEPee.server.model.card.progCard.LeftTurn;
import SEPee.server.model.card.progCard.RightTurn;
import SEPee.server.model.card.progCard.UTurn;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SelectedCard;
import SEPee.server.model.field.*;
import SEPee.server.model.field.Field;
import SEPee.server.model.gameBoard.GameBoard;

public class SmartAi {

    @Getter
    @Setter
    private GameBoard gameBoard;
    @Getter
    @Setter
    private int xCheckpoint;
    @Getter
    @Setter
    private int yCheckpoint;
    @Getter
    @Setter
    private int numCheckpointToken;
    private ArrayList<String> clientHand = new ArrayList<>();
    private ArrayList<String> register = new ArrayList<>();
    private int xRobot;
    private int yRobot;
    private String orientation;
    private int xFuture;
    private int yFuture;
    private String futureOrientation;
    private int bestDistance = 99999999;
    private ArrayList<String> bestRegister;
    private ArrayList<ArrayList<String>> combinations= new ArrayList<>();
    private String lastPlayedCard;
    boolean reboot = false;


    public void setRegister(RobotAI robot, ArrayList<String> hand){
        System.out.println("ICH BIN HIER");
        this.clientHand = hand;
        xRobot = robot.getX();
        yRobot = robot.getY();
        xFuture = xRobot;
        yFuture = yRobot;
        orientation = robot.getOrientation();
        futureOrientation = orientation;

        setCheckpoint();

        combinations = allCombinations(clientHand, 5);
        calculateBestRegister();

    }

    public ArrayList<ArrayList<String>> allCombinations(ArrayList<String> cards, int size){
        ArrayList<ArrayList<String>> result = new ArrayList<>();
        generateCombinationsHelper(cards, size, 0, new ArrayList<>(), result);
        return result;
    }

    private static void generateCombinationsHelper(ArrayList<String> cards, int size, int start,
                                                   ArrayList<String> current, ArrayList<ArrayList<String>> result) {
        if (size == 0) {
            // Generate permutations for the current combination
            generatePermutations(current, 0, result);
            return;
        }

        for (int i = start; i < cards.size(); i++) {
            current.add(cards.get(i));
            generateCombinationsHelper(cards, size - 1, i + 1, current, result);
            current.remove(current.size() - 1);
        }
    }

    private static void generatePermutations(ArrayList<String> current, int start, ArrayList<ArrayList<String>> result) {
        if (start == current.size() - 1) {
            // Add the current permutation to the result
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < current.size(); i++) {
            // Swap elements to generate permutations
            swap(current, start, i);
            generatePermutations(current, start + 1, result);
            swap(current, start, i);  // Backtrack to the original order
        }
    }

    private static void swap(ArrayList<String> list, int i, int j) {
        String temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }

    private int calculateManhattanDistance(int xCoordinate, int yCoordinate){

        int manhattanDistance = Math.abs(xCoordinate - xCheckpoint) + Math.abs(yCoordinate - yCheckpoint);

        return manhattanDistance;
    }

    private void setCheckpoint(){
        switch (gameBoard.getBordName()){
            case "Dizzy Highway":
                xCheckpoint = 12;
                yCheckpoint = 3;
                break;
            case "Extra Crispy":
                switch(numCheckpointToken){
                    case 0:
                        xCheckpoint = 10;
                        yCheckpoint = 2;
                        break;
                    case 1:
                        xCheckpoint = 5;
                        yCheckpoint = 7;
                        break;
                    case 2:
                        xCheckpoint = 10;
                        yCheckpoint = 7;
                        break;
                    case 3:
                        xCheckpoint = 5;
                        yCheckpoint = 2;
                        break;
                }
                break;
            case "Lost Bearings":
                switch(numCheckpointToken){
                    case 0:
                        xCheckpoint = 11;
                        yCheckpoint = 4;
                        break;
                    case 1:
                        xCheckpoint = 4;
                        yCheckpoint = 5;
                        break;
                    case 2:
                        xCheckpoint = 8;
                        yCheckpoint = 2;
                        break;
                    case 3:
                        xCheckpoint = 8;
                        yCheckpoint = 7;
                        break;
                }
                break;
            case "Death Trap":
                switch(numCheckpointToken){
                    case 0:
                        xCheckpoint = 1;
                        yCheckpoint = 7;
                        break;
                    case 1:
                        xCheckpoint = 4;
                        yCheckpoint = 4;
                        break;
                    case 2:
                        xCheckpoint = 7;
                        yCheckpoint = 8;
                        break;
                    case 3:
                        xCheckpoint = 8;
                        yCheckpoint = 2;
                        break;
                    case 4:
                        xCheckpoint = 0;
                        yCheckpoint = 1;
                        break;
                }
                break;
        }
    }

    public void calculateBestRegister(){
        for(int i = 0; i < combinations.size(); i++){
            ArrayList<String> currentRegister = combinations.get(i);
            if(!currentRegister.get(0).equals("Again")) {
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
                int currentDistance = 99999;
                reboot = false;
                for (String card : currentRegister) {
                    switch (card) {
                        case "Again":
                            again();
                            break;
                        case "BackUp":
                            lastPlayedCard = "BackUp";
                            backUp();
                            break;
                        case "MoveI":
                            lastPlayedCard = "MoveI";
                            moveI();
                            break;
                        case "MoveII":
                            lastPlayedCard = "MoveII";
                            moveII();
                            break;
                        case "MoveIII":
                            lastPlayedCard = "MoveIII";
                            moveIII();
                            break;
                        case "PowerUp":
                            lastPlayedCard = "PowerUp";
                            break;
                        case "TurnRight":
                            lastPlayedCard = "TurnRight";
                            turnRight();
                            break;
                        case "TurnLeft":
                            lastPlayedCard = "TurnLeft";
                            turnLeft();
                            break;
                        case "UTurn":
                            lastPlayedCard = "UTurn";
                            uTurn();
                            break;
                        case "Spam":
                            lastPlayedCard = "Spam";
                            break;
                        case "TrojanHorse":
                            lastPlayedCard = "TrojanHorse";
                            break;
                        case "Virus":
                            lastPlayedCard = "Virus";
                            break;
                        case "Worm":
                            lastPlayedCard = "Worm";
                            break;
                        default:
                            System.out.println("unknown card name");
                            break;

                    }
                    if(reboot){
                        break;
                    }
                }
                if(!reboot && currentDistance < bestDistance){
                    bestRegister = currentRegister;
                    bestDistance = currentDistance;
                }
            }
        }
    }

    private void again(){
        switch (lastPlayedCard) {
            case "Again":
                again();
                break;
            case "BackUp":
                backUp();
                break;
            case "MoveI":
                moveI();
                break;
            case "MoveII":
                moveII();
                break;
            case "MoveIII":
                moveIII();
                break;
            case "PowerUp":
                break;
            case "TurnRight":
                turnRight();
                break;
            case "TurnLeft":
                turnLeft();
                break;
            case "UTurn":
                uTurn();
                break;
            case "Spam":
                break;
            case "TrojanHorse":
                break;
            case "Virus":
                break;
            case "Worm":
                break;
            default:
                System.out.println("unknown card name");
                break;

        }
    }

    private void backUp(){
        switch(futureOrientation){
            case "top":
                if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                    yFuture++;
                }
                break;
            case "right":
                if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                    xFuture--;
                }
                break;
            case "bottom":
                if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                    yFuture--;
                }
                break;
            case "left":
                if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                    xFuture++;
                }
                break;
        }
    }

    private void moveI(){

    }

    private void moveII(){

    }

    private void moveIII(){

    }

    private void turnRight(){

    }

    private void turnLeft(){

    }

    private void uTurn(){

    }

    private String checkRobotField(int robotX, int robotY) {
        List<Field> fields = new ArrayList<>();

        if (gameBoard.getBordName().equals("Dizzy Highway")) {
            fields = gameBoard.getFieldsAt(robotX, robotY);
        } else if (gameBoard.getBordName().equals("Extra Crispy")) {
            fields = gameBoard.getFieldsAt(robotX, robotY);
        } else if (gameBoard.getBordName().equals("Death Trap")) {
            fields = gameBoard.getFieldsAt(robotX, robotY);
        } else if (gameBoard.getBordName().equals("Lost Bearings")) {
            fields = gameBoard.getFieldsAt(robotX, robotY);
        }

        //tester string
        System.out.println("Fields at position (" + robotX + ", " + robotY + "): " + fields);

        StringBuilder result = new StringBuilder();

        for (Field field : fields) {
            if (field instanceof ConveyorBelt) {
                System.out.println("ConveyorBelt");
                String[] orientations = field.getOrientation();
                int speed = field.getSpeed();

                result.append("ConveyorBelt " + speed + " " + Arrays.toString(orientations) + ", ");

            } else if (field instanceof Laser) {
                System.out.println("Laser");
                result.append("Laser, ");
            } else if (field instanceof Wall) {
                System.out.println("Wall");
                String[] orientations = field.getOrientation();
                result.append("Wall " + Arrays.toString(orientations) + ", ");
            } else if (field instanceof Empty) {
                System.out.println("Empty field");
                result.append("Empty, ");
            } else if (field instanceof StartPoint) {
                System.out.println("Start point");
                result.append("StartPoint, ");
            } else if (field instanceof CheckPoint) {
                System.out.println("Checkpoint");
                int checkPointNumber = field.getCheckPointNumber();
                result.append("CheckPoint [" + checkPointNumber + "], ");
            } else if (field instanceof EnergySpace) {
                result.append("EnergySpace, ");
            } else if (field instanceof Pit) {
                result.append("Pit, ");
            } else if (field instanceof PushPanel) {
                String[] orientations = field.getOrientation();
                int[] registers = field.getRegisters();
                result.append("PushPanel " + Arrays.toString(orientations) + " " + Arrays.toString(registers) + ", ");
            } else if (field instanceof Gear) {
                String[] orientation = field.getOrientation();
                result.append("Gear " + Arrays.toString(orientation) + ", ");
            } else {
                System.out.println("Field nicht gefunden");
                result.append("UnknownField, ");
            }
        }
        // Remove the last comma and space
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        System.out.println(result);
        return result.toString();
    }

    private boolean isOnGameboard(int x, int y){
        if((x <= 12 && x >= 0) && (y <= 9 && y >= 0)){
            return true;
        }else{
            return false;
        }
    }

}
