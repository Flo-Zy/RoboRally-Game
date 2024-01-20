package SEPee.client.model;

import SEPee.serialisierung.messageType.*;
import SEPee.server.model.Player;
import SEPee.server.model.Robot;
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

    private int xRobot;
    private int yRobot;
    private String orientation;
    private int xFuture;
    private int yFuture;
    private String futureOrientation;
    private int bestDistance;
    private ArrayList<String> bestRegister;
    private ArrayList<ArrayList<String>> combinations= new ArrayList<>();
    private String lastPlayedCard;
    private boolean reboot = false;
    private int currentRegisterNum = 0;
    private AIBestMove aiBestMove = new AIBestMove();
    private boolean checkpointReached;
    private int currentCheckpointToken;



    public void setRegister(RobotAI robot, ArrayList<String> hand) throws InterruptedException {
        System.out.println("ICH BIN HIER");
        this.clientHand = hand;
        xRobot = robot.getX();
        yRobot = robot.getY();
        xFuture = xRobot;
        yFuture = yRobot;
        orientation = robot.getOrientation();
        futureOrientation = orientation;
        checkpointReached = false;

        currentCheckpointToken = numCheckpointToken;
        setCheckpoint();
        bestDistance = 9999;

        combinations = allCombinations(clientHand, 5);
        calculateBestRegister();

        if(bestRegister.isEmpty()){
            int numDamage = 0;
            for(String card : clientHand){
                if(card.equals("Spam") || card.equals("Worm") || card.equals("TrojanHorse") || card.equals("Virus")){
                    numDamage++;
                }
            }

            if(numDamage >= 5){
                for(String card : clientHand){
                    if(bestRegister.size() < 5) {
                        if (card.equals("Spam") || card.equals("Worm") || card.equals("TrojanHorse") || card.equals("Virus")) {
                            bestRegister.add(card);
                        }
                    }
                }
            }
            System.out.println(clientHand);
            System.out.println("HIER AI KARTEN: " + bestRegister);
            int i = 1;
            for (String card : bestRegister) {
                SelectedCard selectedCard = new SelectedCard(card, i);
                String serializedSelectedCard = Serialisierer.serialize(selectedCard);
                ClientAI.getWriter().println(serializedSelectedCard);
                i++;
            }

            TimerStarted timerStarted = new TimerStarted();
            String serializedTimerStarted = Serialisierer.serialize(timerStarted);
            ClientAI.getWriter().println(serializedTimerStarted);

        }else {
            System.out.println(clientHand);
            System.out.println("HIER AI KARTEN: " + bestRegister);
            int i = 1;
            for (String card : bestRegister) {
                SelectedCard selectedCard = new SelectedCard(card, i);
                String serializedSelectedCard = Serialisierer.serialize(selectedCard);
                ClientAI.getWriter().println(serializedSelectedCard);
                i++;
            }

            TimerStarted timerStarted = new TimerStarted();
            String serializedTimerStarted = Serialisierer.serialize(timerStarted);
            ClientAI.getWriter().println(serializedTimerStarted);
        }
        bestRegister.clear();
        checkpointReached = false;

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
                switch(currentCheckpointToken){
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
                switch(currentCheckpointToken){
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
                switch(currentCheckpointToken){
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

    public void calculateBestRegister() throws InterruptedException {
        for(int i = 0; i < combinations.size(); i++){
            ArrayList<String> currentRegister = combinations.get(i);
            if(!currentRegister.get(0).equals("Again")) {
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
                int currentDistance;
                reboot = false;
                currentRegisterNum = 0;
                checkpointReached = false;
                currentCheckpointToken = numCheckpointToken;
                for (String card : currentRegister) {
                    currentRegisterNum++;
                    setCheckpoint();
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
                    }else{
                        fieldActivation();
                    }
                }

                if(checkpointReached){
                    currentDistance = 0;
                }else {
                    currentDistance = calculateManhattanDistance(xFuture, yFuture);
                }
                currentDistance = currentDistance + checkpointWall();
                if(!reboot && currentDistance < bestDistance && !currentRegister.contains("Spam")){
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
        switch(futureOrientation){
            case "top":
                if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                    yFuture--;
                }
                break;
            case "right":
                if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                    xFuture++;
                }
                break;
            case "bottom":
                if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                    yFuture++;
                }
                break;
            case "left":
                if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                    xFuture--;
                }
                break;
        }
    }

    private void moveII(){
        switch(futureOrientation){
            case "top":
                if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                    yFuture--;
                    if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                        yFuture--;
                    }
                }
                break;
            case "right":
                if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                    xFuture++;
                    if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                        xFuture++;
                    }
                }
                break;
            case "bottom":
                if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                    yFuture++;
                    if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                        yFuture++;
                    }
                }
                break;
            case "left":
                if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                    xFuture--;
                    if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                        xFuture--;
                    }
                }
                break;
        }
    }

    private void moveIII(){
        switch(futureOrientation){
            case "top":
                if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                    yFuture--;
                    if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                        yFuture--;
                        if(!isOnGameboard(xFuture, yFuture-1) || checkRobotField(xFuture, yFuture-1).contains("Pit")){
                            reboot = true;
                        }else if(!checkRobotField(xFuture, yFuture).contains("Wall [top")){
                            yFuture--;
                        }
                    }
                }
                break;
            case "right":
                if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                    xFuture++;
                    if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                        xFuture++;
                        if(!isOnGameboard(xFuture+1, yFuture) || checkRobotField(xFuture+1, yFuture).contains("Pit")){
                            reboot = true;
                        }else if(!checkRobotField(xFuture, yFuture).contains("Wall [right")){
                            xFuture++;
                        }
                    }
                }
                break;
            case "bottom":
                if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                    yFuture++;
                    if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                        yFuture++;
                        if(!isOnGameboard(xFuture, yFuture+1) || checkRobotField(xFuture, yFuture+1).contains("Pit")){
                            reboot = true;
                        }else if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                            yFuture++;
                        }
                    }
                }
                break;
            case "left":
                if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                    reboot = true;
                }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                    xFuture--;
                    if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                        reboot = true;
                    }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                        xFuture--;
                        if(!isOnGameboard(xFuture-1, yFuture) || checkRobotField(xFuture-1, yFuture).contains("Pit")){
                            reboot = true;
                        }else if(!checkRobotField(xFuture, yFuture).contains("Wall [left")){
                            xFuture--;
                        }
                    }
                }
                break;
        }
    }

    private void turnRight() {
        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
    }

    private void turnLeft(){
        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
    }

    private void uTurn(){
        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
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
        //System.out.println("Fields at position (" + robotX + ", " + robotY + "): " + fields);

        StringBuilder result = new StringBuilder();

        for (Field field : fields) {
            if (field instanceof ConveyorBelt) {
                //System.out.println("ConveyorBelt");
                String[] orientations = field.getOrientation();
                int speed = field.getSpeed();

                result.append("ConveyorBelt " + speed + " " + Arrays.toString(orientations) + ", ");

            } else if (field instanceof Laser) {
                //System.out.println("Laser");
                result.append("Laser, ");
            } else if (field instanceof Wall) {
                //System.out.println("Wall");
                String[] orientations = field.getOrientation();
                result.append("Wall " + Arrays.toString(orientations) + ", ");
            } else if (field instanceof Empty) {
                //System.out.println("Empty field");
                result.append("Empty, ");
            } else if (field instanceof StartPoint) {
                //System.out.println("Start point");
                result.append("StartPoint, ");
            } else if (field instanceof CheckPoint) {
                //System.out.println("Checkpoint");
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
                //System.out.println("Field nicht gefunden");
                result.append("UnknownField, ");
            }
        }
        // Remove the last comma and space
        if (result.length() > 0) {
            result.setLength(result.length() - 2);
        }
        //System.out.println(result);
        return result.toString();
    }

    private boolean isOnGameboard(int x, int y){
        if((x <= 12 && x >= 0) && (y <= 9 && y >= 0)){
            return true;
        }else{
            return false;
        }
    }

    private static String getResultingOrientation(String turningDirection, String currentOrientation) {
        if (turningDirection.equals("clockwise")) {
            switch (currentOrientation) {
                case "top":
                    return "right";
                case "bottom":
                    return "left";
                case "left":
                    return "top";
                case "right":
                    return "bottom";
            }
        } else {
            switch (currentOrientation) {
                case "top":
                    return "left";
                case "bottom":
                    return "right";
                case "left":
                    return "bottom";
                case "right":
                    return "top";
            }
        }
        //da sollte man nie hinkommen
        return "---";
    }

    public void fieldActivation() throws InterruptedException {
        checkBlueConveyorBelts();

        checkGreenConveyorBelts();

        checkPushPanels();

        checkGears();

        checkCheckpoint();
    }

    private void checkBlueConveyorBelts(){
        String standingOnBlueConveyor = checkRobotField(xFuture, yFuture);
        if (standingOnBlueConveyor.contains("ConveyorBelt 2")) {
            if (standingOnBlueConveyor.contains("ConveyorBelt 2 [top")) {
                yFuture--;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [top")) {
                    if (secondBlue.contains("ConveyorBelt 2 [right")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

                checkConveyorBeltAgain();


            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [right")) {
                xFuture++;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [right")) {
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

                checkConveyorBeltAgain();

            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [bottom")) {

                yFuture++;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [bottom")) {
                    if (secondBlue.contains("ConveyorBelt 2 [right")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                }

                checkConveyorBeltAgain();

            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [left")) {

                xFuture--;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [left")) {
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

                checkConveyorBeltAgain();

            }
        }
        if(!isOnGameboard(xFuture, yFuture) || checkRobotField(xFuture, yFuture).contains("Pit")){
            reboot = true;
        }
    }

    private void checkConveyorBeltAgain(){
        String standingOnBlueConveyorBelt = checkRobotField(xFuture, yFuture);
        if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2")) {
            if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [top")) {
                yFuture--;

                String stillOnBlue = checkRobotField(xFuture, yFuture);

                if (!stillOnBlue.contains("ConveyorBelt 2 [top")) {
                    if (stillOnBlue.contains("ConveyorBelt 2 [right")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [left")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

            } else if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [right")) {
                xFuture++;

                String stillOnBlue = checkRobotField(xFuture, yFuture);

                if (!stillOnBlue.contains("ConveyorBelt 2 [right")) {
                    if (stillOnBlue.contains("ConveyorBelt 2 [bottom")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [top")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

            } else if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [bottom")) {
                yFuture++;

                String stillOnBlue = checkRobotField(xFuture, yFuture);

                if (!stillOnBlue.contains("ConveyorBelt 2 [bottom")) {

                    if (stillOnBlue.contains("ConveyorBelt 2 [left")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [right")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }


            } else if (standingOnBlueConveyorBelt.contains("ConveyorBelt 2 [left")) {
                xFuture--;

                String stillOnBlue = checkRobotField(xFuture, yFuture);

                if (!stillOnBlue.contains("ConveyorBelt 2 [left")) {

                    if (stillOnBlue.contains("ConveyorBelt 2 [top")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (stillOnBlue.contains("ConveyorBelt 2 [bottom")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

            }
        }
    }

    private void checkGreenConveyorBelts() {
        String standingOnGreenConveyor = checkRobotField(xFuture, yFuture);
        if (standingOnGreenConveyor.contains("ConveyorBelt 1")) {
            if (standingOnGreenConveyor.contains("ConveyorBelt 1 [top")) {

                yFuture--;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [top")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [right")) {

                xFuture++;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [right")) {
                    if (secondGreen.contains("ConveyorBelt 1 [top")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [bottom")) {

                yFuture++;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [bottom")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [left")) {

                xFuture--;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [left")) {
                    if (secondGreen.contains("ConveyorBelt 1 [top")) {
                        futureOrientation = getResultingOrientation("clockwise", futureOrientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
                    }
                }
            }
        }
        if(!isOnGameboard(xFuture, yFuture) || checkRobotField(xFuture, yFuture).contains("Pit")){
            reboot = true;
        }
    }

    private void checkPushPanels() {
        String standingOnPushPanel = checkRobotField(xFuture, yFuture);
        boolean shouldActivate = false;
        int pushPanelRegister = 0;

        if (standingOnPushPanel.contains("[1, 3, 5]")) {
            pushPanelRegister = 1;

        } else if (standingOnPushPanel.contains("[2, 4]")) {
            pushPanelRegister = 2;
        }

        if (standingOnPushPanel.contains("PushPanel")) {

            if ((currentRegisterNum == 1 || currentRegisterNum == 3 || currentRegisterNum == 5) && (pushPanelRegister == 1)) {
                shouldActivate = true;
            } else if ((currentRegisterNum == 2 || currentRegisterNum == 4) && (pushPanelRegister == 2)) {
                shouldActivate = true;
            }

            if (shouldActivate) {
                if (standingOnPushPanel.contains("PushPanel [top")) {
                    yFuture--;
                } else if (standingOnPushPanel.contains("PushPanel [left")) {
                    xFuture--;
                } else if (standingOnPushPanel.contains("PushPanel [right")) {
                    xFuture++;
                } else if (standingOnPushPanel.contains("PushPanel [bottom")) {
                    yFuture++;
                }
            }
        }
        if(!isOnGameboard(xFuture, yFuture) || checkRobotField(xFuture, yFuture).contains("Pit")){
            reboot = true;
        }
    }

    private void checkGears(){
        if (checkRobotField(xFuture, yFuture).contains("Gear [clockwise")) {
            futureOrientation = getResultingOrientation("clockwise", futureOrientation);
        } else if (checkRobotField(xFuture, yFuture).contains("Gear [counterclockwise")) {
            futureOrientation = getResultingOrientation("counterclockwise", futureOrientation);
        }
    }

    private void checkCheckpoint(){
        String standingOnCheckPoint = checkRobotField(xFuture, yFuture);

        if (standingOnCheckPoint.contains("CheckPoint [1")) {
            if (currentCheckpointToken == 0) { //check whether no checkPoints were reached before
                currentCheckpointToken++;
                checkpointReached = true;
                setCheckpoint();
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [2")) {
            if (currentCheckpointToken == 1) { //check whether one checkPoint was reached before
                currentCheckpointToken++;
                checkpointReached = true;
                setCheckpoint();
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [3")) {
            if (currentCheckpointToken == 2) {
                currentCheckpointToken++;
                checkpointReached = true;
                setCheckpoint();
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [4")) {
            if (currentCheckpointToken == 3) {
                currentCheckpointToken++;
                checkpointReached = true;
                setCheckpoint();
            }
        } else if (standingOnCheckPoint.contains("CheckPoint [5")) {
            if (currentCheckpointToken == 4) {
                currentCheckpointToken++;
                checkpointReached = true;
                setCheckpoint();
            }
        }
    }

    private int checkpointWall(){
        if(gameBoard.getBordName().equals("Extra Crispy")){
            switch (currentCheckpointToken){
                case 0:
                    if(xFuture == 10 && yFuture == 1){
                        return 2;
                    }
                    if(xFuture == 10 && yFuture == 3 && !futureOrientation.equals("top")){
                        return 4;
                    }
                    if(xFuture == 8 && yFuture == 2){
                        return 5;
                    }
                    if(xFuture == 5 && yFuture == 2){
                        return 6;
                    }
                    break;
                case 1:
                    if(xFuture == 5 && yFuture == 8){
                        return 2;
                    }
                    if(xFuture == 5 && yFuture == 6 && !futureOrientation.equals("bottom")){
                        return 4;
                    }
                    if(xFuture == 7 && yFuture == 7){
                        return 5;
                    }
                    if(xFuture == 10 && yFuture == 7){
                        return 6;
                    }
                    break;
                case 2:
                    if(xFuture == 10 && yFuture == 8){
                        return 2;
                    }
                    if(xFuture == 10 && yFuture == 6 && !futureOrientation.equals("bottom")){
                        return 4;
                    }
                    if(xFuture == 8 && yFuture == 7){
                        return 5;
                    }
                    if(xFuture == 5 && yFuture == 7){
                        return 6;
                    }
                    break;
                case 3:
                    if(xFuture == 5 && yFuture == 1){
                        return 2;
                    }
                    if(xFuture == 5 && yFuture == 3 && !futureOrientation.equals("top")){
                        return 4;
                    }
                    if(xFuture == 7 && yFuture == 2){
                        return 5;
                    }
                    if(xFuture == 10 && yFuture == 2){
                        return 6;
                    }
                    break;
            }
        }else if(gameBoard.getBordName().equals("Death Trap")){
            if (currentCheckpointToken == 1) {
                if ((xFuture == 4 && yFuture == 5) || (xFuture == 4 && yFuture == 3)) {
                    return 2;
                }else if(xFuture == 4 && yFuture == 6 || xFuture == 3 && yFuture == 6 || xFuture == 5 && yFuture == 6){
                    return 2;
                }
            }
        }
        return 0;
    }
}
