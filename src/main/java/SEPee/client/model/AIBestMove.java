package SEPee.client.model;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SelectedCard;
import SEPee.server.model.field.*;
import SEPee.server.model.field.Field;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AIBestMove {
    @Getter
    @Setter
    private static GameBoard gameBoard;
    @Getter
    @Setter
    private int xCheckpoint;
    @Getter
    @Setter
    private int yCheckpoint;
    @Getter
    @Setter
    private int numCheckpointToken;
    private int cardCounter = 0;
    private ArrayList<String> clientHand = new ArrayList<>();
    private ArrayList<String> register = new ArrayList<>();
    private int xRobot;
    private int yRobot;
    private String orientation;
    private int xFuture;
    private int yFuture;
    private String futureOrientation;
    private boolean done = false;
    private boolean turnFlag = false;

    public void setRegister(RobotAI robot,  ArrayList<String> hand) throws InterruptedException{
        this.clientHand = hand;
        xRobot = robot.getX();
        yRobot = robot.getY();
        orientation = robot.getOrientation();
        setCheckpoint();

        int filledRegisters = 0;
        boolean finished = false;
        while(!finished) {
            int oldRegister = filledRegisters;
            //checkMoveCheckpointBeforeConveyorBelt();
            System.out.println("X: "+ xRobot);
            System.out.println("Y: "+ yRobot);
            System.out.println("XFUTURE : "+ xFuture);
            System.out.println("YFUTURE : "+ yFuture);
            standingOnConveyorInOppositeDirection();
            System.out.println("X: "+ xRobot);
            System.out.println("Y: "+ yRobot);
            System.out.println("XFUTURE : "+ xFuture);
            System.out.println("YFUTURE : "+ yFuture);
            if(!turnFlag) {
                turnInCheckpointDirection();
                System.out.println("X: "+ xRobot);
                System.out.println("Y: "+ yRobot);
                System.out.println("XFUTURE : "+ xFuture);
                System.out.println("YFUTURE : "+ yFuture);
            }
            checkWall();
            System.out.println("X: "+ xRobot);
            System.out.println("Y: "+ yRobot);
            System.out.println("XFUTURE : "+ xFuture);
            System.out.println("YFUTURE : "+ yFuture);
            move();
            System.out.println("X: "+ xRobot);
            System.out.println("Y: "+ yRobot);
            System.out.println("XFUTURE : "+ xFuture);
            System.out.println("YFUTURE : "+ yFuture);
            turnFlag = false;
            filledRegisters = register.size();
            if(cardCounter == 5){
                finished = true;
            }else if(oldRegister == filledRegisters){
                finished = moveAndCheckAgain();
                System.out.println("X: "+ xRobot);
                System.out.println("Y: "+ yRobot);
                System.out.println("XFUTURE : "+ xFuture);
                System.out.println("YFUTURE : "+ yFuture);
            }
        }


        /*int j = 0;
        while(j < 5 - register.size()){
            if(register.isEmpty()) {
                for (String card : clientHand) {
                    if(!card.equals("Again")){
                        register.add(card);
                        clientHand.remove(card);
                        break;
                    }
                }
            }else{
                register.add(clientHand.get(0));
                clientHand.remove(0);
            }

        }*/

        System.out.println("HIER AI KARTEN: "+register);
        int i = 1;
        for(String card: register){
            SelectedCard selectedCard = new SelectedCard(card, i);
            String serializedSelectedCard = Serialisierer.serialize(selectedCard);
            ClientAI.getWriter().println(serializedSelectedCard);
            i++;
        }
        register.clear();
        cardCounter = 0;
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

    private static String checkRobotField(int robotX, int robotY) {
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

    private void turnInCheckpointDirection() throws InterruptedException {
        String direction;
        xFuture = xRobot;
        yFuture = yRobot;
        futureOrientation = orientation;

        if(checkRobotField(xRobot, yRobot).contains("ConveyorBelt 2")){
            checkBlueConveyorBelts(checkRobotField(xRobot, yRobot));
        }else if(checkRobotField(xRobot, yRobot).contains("ConveyorBelt 1")){
            checkGreenConveyorBelts(checkRobotField(xRobot, yRobot));
            xFuture = xRobot;
            yFuture = yRobot;
            futureOrientation = orientation;
            System.out.println("INCHECKPOINTDIRECTION");
            System.out.println("X: "+ xRobot);
            System.out.println("Y: "+ yRobot);
            System.out.println("XFUTURE : "+ xFuture);
            System.out.println("YFUTURE : "+ yFuture);
        }else if(checkRobotField(xRobot, yRobot).contains("Gear")){
            turnsWithGears();
            xFuture = xRobot;
            yFuture = yRobot;
            futureOrientation = orientation;
        }

        int xDifference = xCheckpoint - xFuture;
        int yDifference = yCheckpoint - yFuture;

        if (Math.abs(xDifference) >= Math.abs(yDifference)) {
            System.out.println("1.CHECKPOINT CHECK");
            direction = (xDifference > 0) ? "right" : "left";
            System.out.println("CHECKPOINT TURN "+orientation);
            System.out.println("2.CHECKPOINT DIRECTION: "+direction);
            if(!orientation.equals(direction)){
                checkPointDirectionSwitchCase(direction);
                System.out.println("INCHECKPOINTDIRECTION");
                System.out.println("X: "+ xRobot);
                System.out.println("Y: "+ yRobot);
                System.out.println("XFUTURE : "+ xFuture);
                System.out.println("YFUTURE : "+ yFuture);
                if(!done){
                    System.out.println("2.CHECKPOINT CHECK");
                    direction = (yDifference < 0) ? "top" : "bottom";
                    System.out.println("2.CHECKPOINT DIRECTION: "+direction);
                    if(!orientation.equals(direction)){
                        checkPointDirectionSwitchCase(direction);
                        System.out.println("INCHECKPOINTDIRECTION");
                        System.out.println("X: "+ xRobot);
                        System.out.println("Y: "+ yRobot);
                        System.out.println("XFUTURE : "+ xFuture);
                        System.out.println("YFUTURE : "+ yFuture);
                    }
                }
            }
        } else {
            System.out.println("1.CHECKPOINT CHECK");
            direction = (yDifference < 0) ? "top" : "bottom";
            System.out.println("CHECKPOINT TURN "+orientation);
            System.out.println("2.CHECKPOINT DIRECTION: "+direction);
            if(!orientation.equals(direction)){
                checkPointDirectionSwitchCase(direction);
                System.out.println("INCHECKPOINTDIRECTION");
                System.out.println("X: "+ xRobot);
                System.out.println("Y: "+ yRobot);
                System.out.println("XFUTURE : "+ xFuture);
                System.out.println("YFUTURE : "+ yFuture);
                if(!done){
                    System.out.println("2.CHECKPOINT CHECK");
                    direction = (xDifference > 0) ? "right" : "left";
                    System.out.println("2.CHECKPOINT DIRECTION: "+direction);
                    if(!orientation.equals(direction)){
                        checkPointDirectionSwitchCase(direction);
                        System.out.println("INCHECKPOINTDIRECTION");
                        System.out.println("X: "+ xRobot);
                        System.out.println("Y: "+ yRobot);
                        System.out.println("XFUTURE : "+ xFuture);
                        System.out.println("YFUTURE : "+ yFuture);
                    }
                }
            }
        }
        done = false;
    }

    private void move() throws InterruptedException{
        // für die Zukunft: Pits (beim drehen) und andere Roboter und PushPanel und Gears???
        int initialDistance = calculateManhattanDistance(xRobot, yRobot);
        xFuture = xRobot;
        yFuture = yRobot;
        futureOrientation = orientation;
        boolean move1 = false;
        boolean move2 = false;
        boolean move3 = false;
        boolean backUp = false;
        String bestCard = null;
        int bestDistance = initialDistance;
        int bestX = -9999;
        int bestY = -9999;
        String bestOrientation = "---";
        int distanceFuture;
        for(String card : clientHand){
            if(card.equals("MoveI") && !move1){
                System.out.println("CHECK FUTURE MOVE1");
                move1 = true;
                switch (orientation){
                    case "top":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [top"))) {
                            System.out.println("KEINE WALL");
                            yFuture--;
                            if (!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                String result = checkRobotField(xFuture, yFuture);
                                if (result.contains("ConveyorBelt 2")) {
                                    checkBlueConveyorBelts(result);
                                } else if (result.contains("ConveyorBelt 1")) {
                                    checkGreenConveyorBelts(result);
                                }
                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                if (distanceFuture < bestDistance) {
                                    bestDistance = distanceFuture;
                                    bestCard = "MoveI";
                                    bestX = xFuture;
                                    bestY = yFuture;
                                    bestOrientation = futureOrientation;
                                }
                            }
                        }
                        break;
                    case "right":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [right"))) {
                            System.out.println("KEINE WALL");
                            xFuture++;
                            if (!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                String result = checkRobotField(xFuture, yFuture);
                                if (result.contains("ConveyorBelt 2")) {
                                    checkBlueConveyorBelts(result);
                                } else if (result.contains("ConveyorBelt 1")) {
                                    checkGreenConveyorBelts(result);
                                }
                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                if (distanceFuture < bestDistance) {
                                    bestDistance = distanceFuture;
                                    bestCard = "MoveI";
                                    bestX = xFuture;
                                    bestY = yFuture;
                                    bestOrientation = futureOrientation;
                                }
                            }
                        }
                        break;
                    case "bottom":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [bottom"))) {
                            System.out.println("KEINE WALL");
                            yFuture++;
                            if (!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                String result = checkRobotField(xFuture, yFuture);
                                if (result.contains("ConveyorBelt 2")) {
                                    checkBlueConveyorBelts(result);
                                } else if (result.contains("ConveyorBelt 1")) {
                                    checkGreenConveyorBelts(result);
                                }
                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                if (distanceFuture < bestDistance) {
                                    bestDistance = distanceFuture;
                                    bestCard = "MoveI";
                                    bestX = xFuture;
                                    bestY = yFuture;
                                    bestOrientation = futureOrientation;
                                }
                            }
                        }
                        break;
                    case "left":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [left"))) {
                            System.out.println("KEINE WALL");
                            xFuture--;
                            if (!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                String result = checkRobotField(xFuture, yFuture);
                                if (result.contains("ConveyorBelt 2")) {
                                    checkBlueConveyorBelts(result);
                                } else if (result.contains("ConveyorBelt 1")) {
                                    checkGreenConveyorBelts(result);
                                }
                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                if (distanceFuture < bestDistance) {
                                    bestDistance = distanceFuture;
                                    bestCard = "MoveI";
                                    bestX = xFuture;
                                    bestY = yFuture;
                                    bestOrientation = futureOrientation;
                                }
                            }
                        }
                        break;
                }
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
            }else if(card.equals("MoveII") && !move2){
                move2 = true;
                System.out.println("CHECK FUTURE MOVE2");
                switch (orientation){
                    case "top":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [top"))) {
                            System.out.println("KEINE WALL");
                            yFuture--;
                            if (!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [top"))) {
                                    System.out.println("KEINE WALL");
                                    yFuture--;
                                    if (!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        String result = checkRobotField(xFuture, yFuture);
                                        if (result.contains("ConveyorBelt 2")) {
                                            checkBlueConveyorBelts(result);
                                        } else if (result.contains("ConveyorBelt 1")) {
                                            checkGreenConveyorBelts(result);
                                        }
                                        distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                        if (distanceFuture < bestDistance) {
                                            bestDistance = distanceFuture;
                                            bestCard = "MoveII";
                                            bestX = xFuture;
                                            bestY = yFuture;
                                            bestOrientation = futureOrientation;
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                    case "right":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [right"))) {
                            System.out.println("KEINE WALL");
                            xFuture++;
                            if (!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [right"))) {
                                    System.out.println("KEINE WALL");
                                    xFuture++;
                                    if (!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        String result = checkRobotField(xFuture, yFuture);
                                        if (result.contains("ConveyorBelt 2")) {
                                            checkBlueConveyorBelts(result);
                                        } else if (result.contains("ConveyorBelt 1")) {
                                            checkGreenConveyorBelts(result);
                                        }
                                        distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                        if (distanceFuture < bestDistance) {
                                            bestDistance = distanceFuture;
                                            bestCard = "MoveII";
                                            bestX = xFuture;
                                            bestY = yFuture;
                                            bestOrientation = futureOrientation;
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                    case "bottom":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [bottom"))) {
                            System.out.println("KEINE WALL");
                            yFuture++;
                            if (!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [bottom"))) {
                                    System.out.println("KEINE WALL");
                                    yFuture++;
                                    if (!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        String result = checkRobotField(xFuture, yFuture);
                                        if (result.contains("ConveyorBelt 2")) {
                                            checkBlueConveyorBelts(result);
                                        } else if (result.contains("ConveyorBelt 1")) {
                                            checkGreenConveyorBelts(result);
                                        }
                                        distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                        if (distanceFuture < bestDistance) {
                                            bestDistance = distanceFuture;
                                            bestCard = "MoveII";
                                            bestX = xFuture;
                                            bestY = yFuture;
                                            bestOrientation = futureOrientation;
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                    case "left":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [left"))) {
                            System.out.println("KEINE WALL");
                            xFuture--;
                            if (!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [left"))) {
                                    System.out.println("KEINE WALL");
                                    xFuture--;
                                    if (!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        String result = checkRobotField(xFuture, yFuture);
                                        if (result.contains("ConveyorBelt 2")) {
                                            checkBlueConveyorBelts(result);
                                        } else if (result.contains("ConveyorBelt 1")) {
                                            checkGreenConveyorBelts(result);
                                        }
                                        distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                        if (distanceFuture < bestDistance) {
                                            bestDistance = distanceFuture;
                                            bestCard = "MoveII";
                                            bestX = xFuture;
                                            bestY = yFuture;
                                            bestOrientation = futureOrientation;
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                }
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
            }else if(card.equals("MoveIII") && !move3){
                move3 = true;
                System.out.println("CHECK FUTURE MOVE3");
                switch (orientation){
                    case "top":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [top"))) {
                            yFuture--;
                            if (!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [top"))) {
                                    yFuture--;
                                    if (!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        if (!(checkRobotField(xFuture, yFuture).contains("Wall [top"))) {
                                            yFuture--;
                                            if (!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                                String result = checkRobotField(xFuture, yFuture);
                                                if (result.contains("ConveyorBelt 2")) {
                                                    checkBlueConveyorBelts(result);
                                                } else if (result.contains("ConveyorBelt 1")) {
                                                    checkGreenConveyorBelts(result);
                                                }
                                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                                if (distanceFuture < bestDistance) {
                                                    bestDistance = distanceFuture;
                                                    bestCard = "MoveIII";
                                                    bestX = xFuture;
                                                    bestY = yFuture;
                                                    bestOrientation = futureOrientation;
                                                }
                                            }
                                        } else {
                                            String result = checkRobotField(xFuture, yFuture);
                                            if (result.contains("ConveyorBelt 2")) {
                                                checkBlueConveyorBelts(result);
                                            } else if (result.contains("ConveyorBelt 1")) {
                                                checkGreenConveyorBelts(result);
                                            }
                                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                            if (distanceFuture < bestDistance) {
                                                bestDistance = distanceFuture;
                                                bestCard = "MoveIII";
                                                bestX = xFuture;
                                                bestY = yFuture;
                                                bestOrientation = futureOrientation;
                                            }
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveIII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                    case "right":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [right"))) {
                            xFuture++;
                            if (!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [right"))) {
                                    xFuture++;
                                    if (!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        if (!(checkRobotField(xFuture, yFuture).contains("Wall [right"))) {
                                            xFuture++;
                                            if (!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                                String result = checkRobotField(xFuture, yFuture);
                                                if (result.contains("ConveyorBelt 2")) {
                                                    checkBlueConveyorBelts(result);
                                                } else if (result.contains("ConveyorBelt 1")) {
                                                    checkGreenConveyorBelts(result);
                                                }
                                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                                if (distanceFuture < bestDistance) {
                                                    bestDistance = distanceFuture;
                                                    bestCard = "MoveIII";
                                                    bestX = xFuture;
                                                    bestY = yFuture;
                                                    bestOrientation = futureOrientation;
                                                }
                                            }
                                        } else {
                                            String result = checkRobotField(xFuture, yFuture);
                                            if (result.contains("ConveyorBelt 2")) {
                                                checkBlueConveyorBelts(result);
                                            } else if (result.contains("ConveyorBelt 1")) {
                                                checkGreenConveyorBelts(result);
                                            }
                                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                            if (distanceFuture < bestDistance) {
                                                bestDistance = distanceFuture;
                                                bestCard = "MoveIII";
                                                bestX = xFuture;
                                                bestY = yFuture;
                                                bestOrientation = futureOrientation;
                                            }
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveIII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                    case "bottom":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [bottom"))) {
                            yFuture++;
                            if (!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [bottom"))) {
                                    yFuture++;
                                    if (!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        if (!(checkRobotField(xFuture, yFuture).contains("Wall [bottom"))) {
                                            yFuture++;
                                            if (!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                                String result = checkRobotField(xFuture, yFuture);
                                                if (result.contains("ConveyorBelt 2")) {
                                                    checkBlueConveyorBelts(result);
                                                } else if (result.contains("ConveyorBelt 1")) {
                                                    checkGreenConveyorBelts(result);
                                                }
                                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                                if (distanceFuture < bestDistance) {
                                                    bestDistance = distanceFuture;
                                                    bestCard = "MoveIII";
                                                    bestX = xFuture;
                                                    bestY = yFuture;
                                                    bestOrientation = futureOrientation;
                                                }
                                            }
                                        } else {
                                            String result = checkRobotField(xFuture, yFuture);
                                            if (result.contains("ConveyorBelt 2")) {
                                                checkBlueConveyorBelts(result);
                                            } else if (result.contains("ConveyorBelt 1")) {
                                                checkGreenConveyorBelts(result);
                                            }
                                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                            if (distanceFuture < bestDistance) {
                                                bestDistance = distanceFuture;
                                                bestCard = "MoveIII";
                                                bestX = xFuture;
                                                bestY = yFuture;
                                                bestOrientation = futureOrientation;
                                            }
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveIII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                    case "left":
                        if(!(checkRobotField(xFuture, yFuture).contains("Wall [left"))) {
                            xFuture--;
                            if (!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                if (!(checkRobotField(xFuture, yFuture).contains("Wall [left"))) {
                                    xFuture--;
                                    if (!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                        if (!(checkRobotField(xFuture, yFuture).contains("Wall [left"))) {
                                            xFuture--;
                                            if (!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))) {
                                                String result = checkRobotField(xFuture, yFuture);
                                                if (result.contains("ConveyorBelt 2")) {
                                                    checkBlueConveyorBelts(result);
                                                } else if (result.contains("ConveyorBelt 1")) {
                                                    checkGreenConveyorBelts(result);
                                                }
                                                distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                                if (distanceFuture < bestDistance) {
                                                    bestDistance = distanceFuture;
                                                    bestCard = "MoveIII";
                                                    bestX = xFuture;
                                                    bestY = yFuture;
                                                    bestOrientation = futureOrientation;
                                                }
                                            }
                                        } else {
                                            String result = checkRobotField(xFuture, yFuture);
                                            if (result.contains("ConveyorBelt 2")) {
                                                checkBlueConveyorBelts(result);
                                            } else if (result.contains("ConveyorBelt 1")) {
                                                checkGreenConveyorBelts(result);
                                            }
                                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                            if (distanceFuture < bestDistance) {
                                                bestDistance = distanceFuture;
                                                bestCard = "MoveIII";
                                                bestX = xFuture;
                                                bestY = yFuture;
                                                bestOrientation = futureOrientation;
                                            }
                                        }
                                    }
                                } else {
                                    String result = checkRobotField(xFuture, yFuture);
                                    if (result.contains("ConveyorBelt 2")) {
                                        checkBlueConveyorBelts(result);
                                    } else if (result.contains("ConveyorBelt 1")) {
                                        checkGreenConveyorBelts(result);
                                    }
                                    distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                                    if (distanceFuture < bestDistance) {
                                        bestDistance = distanceFuture;
                                        bestCard = "MoveIII";
                                        bestX = xFuture;
                                        bestY = yFuture;
                                        bestOrientation = futureOrientation;
                                    }
                                }
                            }
                        }
                        break;
                }
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
            }else if(card.equals("BackUp") && !backUp) {
                backUp = true;
                switch (orientation){
                    case "top":
                        yFuture++;
                        if(!(yFuture > 9) && !(checkRobotField(xFuture, yFuture).contains("Pit"))){
                            String result = checkRobotField(xFuture, yFuture);
                            if(result.contains("ConveyorBelt 2")){
                                checkBlueConveyorBelts(result);
                            }else if(result.contains("ConveyorBelt 1")){
                                checkGreenConveyorBelts(result);
                            }
                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                            if(distanceFuture < bestDistance){
                                bestDistance = distanceFuture;
                                bestCard = "BackUp";
                                bestX = xFuture;
                                bestY = yFuture;
                                bestOrientation = futureOrientation;
                            }
                        }
                        break;
                    case "right":
                        xFuture--;
                        if(!(xFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))){
                            String result = checkRobotField(xFuture, yFuture);
                            if(result.contains("ConveyorBelt 2")){
                                checkBlueConveyorBelts(result);
                            }else if(result.contains("ConveyorBelt 1")){
                                checkGreenConveyorBelts(result);
                            }
                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                            if(distanceFuture < bestDistance){
                                bestDistance = distanceFuture;
                                bestCard = "BackUp";
                                bestX = xFuture;
                                bestY = yFuture;
                                bestOrientation = futureOrientation;
                            }
                        }
                        break;
                    case "bottom":
                        yFuture--;
                        if(!(yFuture < 0) && !(checkRobotField(xFuture, yFuture).contains("Pit"))){
                            String result = checkRobotField(xFuture, yFuture);
                            if(result.contains("ConveyorBelt 2")){
                                checkBlueConveyorBelts(result);
                            }else if(result.contains("ConveyorBelt 1")){
                                checkGreenConveyorBelts(result);
                            }
                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                            if(distanceFuture < bestDistance){
                                bestDistance = distanceFuture;
                                bestCard = "BackUp";
                                bestX = xFuture;
                                bestY = yFuture;
                                bestOrientation = futureOrientation;
                            }
                        }
                        break;
                    case "left":
                        xFuture++;
                        if(!(xFuture > 12) && !(checkRobotField(xFuture, yFuture).contains("Pit"))){
                            String result = checkRobotField(xFuture, yFuture);
                            if(result.contains("ConveyorBelt 2")){
                                checkBlueConveyorBelts(result);
                            }else if(result.contains("ConveyorBelt 1")){
                                checkGreenConveyorBelts(result);
                            }
                            distanceFuture = calculateManhattanDistance(xFuture, yFuture);
                            if(distanceFuture < bestDistance){
                                bestDistance = distanceFuture;
                                bestCard = "BackUp";
                                bestX = xFuture;
                                bestY = yFuture;
                                bestOrientation = futureOrientation;
                            }
                        }
                        break;
                }
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
            }
        }
        if(bestCard != null && cardCounter < 5){
            cardCounter++;
            register.add(bestCard);
            clientHand.remove(bestCard);
            orientation = bestOrientation;
            xRobot = bestX;
            yRobot = bestY;
            System.out.println("MOVE GELEGT");
            System.out.println(clientHand);
        }
    }

    public void checkWall(){
        switch (orientation){
            case "top":
                System.out.println("CHECK WALL TOP");
                if(checkRobotField(xRobot, yRobot).contains("Wall [top")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [right")){
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "left";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(checkRobotField(xRobot, yRobot).contains("Wall [left")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "right";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(!gameBoard.getBordName().equals("Death Trap")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "right";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else{
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "left";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "right":
                System.out.println("CHECK WALL RIGHT");
                if(checkRobotField(xRobot, yRobot).contains("Wall [right")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [bottom")){
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "top";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(checkRobotField(xRobot, yRobot).contains("Wall [top")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "bottom";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(!gameBoard.getBordName().equals("Death Trap")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "bottom";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else{
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "top";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "bottom":
                System.out.println("CHECK WALL BOTTOM");
                if(checkRobotField(xRobot, yRobot).contains("Wall [bottom")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [left")){
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "right";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(checkRobotField(xRobot, yRobot).contains("Wall [right")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "left";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(!gameBoard.getBordName().equals("Death Trap")){
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "right";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else{
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "left";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
            case "left":
                System.out.println("CHECK WALL LEFT");
                if(checkRobotField(xRobot, yRobot).contains("Wall [left")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [top")){
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "bottom";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(checkRobotField(xRobot, yRobot).contains("Wall [bottom")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "top";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else if(!gameBoard.getBordName().equals("Death Trap")){
                        for(String card : clientHand){
                            if(card.equals("TurnRight")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove("TurnRight");
                                    orientation = "top";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }else{
                        for(String card : clientHand){
                            if(card.equals("TurnLeft")){
                                if(cardCounter < 5) {
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove("TurnLeft");
                                    orientation = "bottom";
                                    System.out.println("CHECK WALL");
                                    break;
                                }
                            }
                        }
                    }
                }
                break;
        }
    }

    private void checkBlueConveyorBelts(String standingOnBlueConveyor) throws InterruptedException {
        if (standingOnBlueConveyor.contains("ConveyorBelt 2")) {
            if (standingOnBlueConveyor.contains("ConveyorBelt 2 [top")) {
                yFuture--;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [top")) {
                    if (secondBlue.contains("ConveyorBelt 2 [right")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                }

                checkConveyorBeltAgain(secondBlue);


            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [right")) {
                xFuture++;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [right")) {
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                }

                checkConveyorBeltAgain(secondBlue);

            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [bottom")) {

                yFuture++;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [bottom")) {
                    if (secondBlue.contains("ConveyorBelt 2 [right")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [left")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                }

                checkConveyorBeltAgain(secondBlue);

            } else if (standingOnBlueConveyor.contains("ConveyorBelt 2 [left")) {

                xFuture--;

                String secondBlue = checkRobotField(xFuture, yFuture);

                if (!secondBlue.contains("ConveyorBelt 2 [left")) {
                    if (secondBlue.contains("ConveyorBelt 2 [top")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                    if (secondBlue.contains("ConveyorBelt 2 [bottom")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                }

                checkConveyorBeltAgain(secondBlue);

            }
        }
    }

    private void checkConveyorBeltAgain(String standingOnBlueConveyorBelt) throws InterruptedException {

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


    private void checkGreenConveyorBelts(String standingOnGreenConveyor) {
        if (standingOnGreenConveyor.contains("ConveyorBelt 1")) {
            if (standingOnGreenConveyor.contains("ConveyorBelt 1 [top")) {

                yFuture--;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [top")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [right")) {

                xFuture++;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [right")) {
                    if (secondGreen.contains("ConveyorBelt 1 [top")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [bottom")) {

                yFuture++;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [bottom")) {
                    if (secondGreen.contains("ConveyorBelt 1 [right")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [left")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                }

            } else if (standingOnGreenConveyor.contains("ConveyorBelt 1 [left")) {

                xFuture--;

                String secondGreen = checkRobotField(xFuture, yFuture);

                if (!secondGreen.contains("ConveyorBelt 1 [left")) {
                    if (secondGreen.contains("ConveyorBelt 1 [top")) {
                        futureOrientation = getResultingOrientation("clockwise", orientation);
                    }
                    if (secondGreen.contains("ConveyorBelt 1 [bottom")) {
                        futureOrientation = getResultingOrientation("counterclockwise", orientation);
                    }
                }
            }
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

    private void checkPointDirectionSwitchCase(String direction){
        switch(futureOrientation){
            case "top":
                switch (direction) {
                    case "right":
                        if (!checkRobotField(xFuture, yFuture).contains("Wall [right")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnRight")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnRight");
                                        clientHand.remove(card);
                                        orientation = "right";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "left":
                        if (!checkRobotField(xFuture, yFuture).contains("Wall [left")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnLeft")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnLeft");
                                        clientHand.remove(card);
                                        orientation = "left";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "bottom":
                        if (!checkRobotField(xFuture, yFuture).contains("Wall [bottom")) {
                            boolean finished = false;
                            for (String card : clientHand) {
                                if (card.equals("UTurn")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("UTurn");
                                        clientHand.remove(card);
                                        finished = true;
                                        orientation = "bottom";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                            if(!checkRobotField(xFuture, yFuture).contains("ConveyorBelt")) {
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (String card : clientHand) {
                                        if (card.equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "bottom";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "bottom";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    }
                                }
                            }
                        }
                        break;
                }
                break;
            case "right":
                switch (direction) {
                    case "top":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [top")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnLeft")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnLeft");
                                        clientHand.remove(card);
                                        orientation = "top";
                                        done = true;
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "bottom":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnRight")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnRight");
                                        clientHand.remove(card);
                                        orientation = "bottom";
                                        done = true;
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "left":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [left")) {
                            boolean finished = false;
                            for (String card : clientHand) {
                                if (card.equals("UTurn")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("UTurn");
                                        clientHand.remove(card);
                                        finished = true;
                                        orientation = "left";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                            if(!checkRobotField(xFuture, yFuture).contains("ConveyorBelt")) {
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (String card : clientHand) {
                                        if (card.equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "left";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "left";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    }
                                }
                            }
                        }
                        break;
                }
                break;
            case "bottom":
                switch (direction) {
                    case "right":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [right")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnLeft")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnLeft");
                                        clientHand.remove(card);
                                        orientation = "right";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "left":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [left")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnRight")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnRight");
                                        clientHand.remove(card);
                                        orientation = "left";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "top":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [top")) {
                            boolean finished = false;
                            for (String card : clientHand) {
                                if (card.equals("UTurn")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("UTurn");
                                        clientHand.remove(card);
                                        finished = true;
                                        orientation = "top";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                            if(!checkRobotField(xFuture, yFuture).contains("ConveyorBelt")) {
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (String card : clientHand) {
                                        if (card.equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "top";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "top";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    }
                                }
                            }
                        }
                        break;
                }
                break;
            case "left":
                switch (direction) {
                    case "top":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [top")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnRight")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnRight");
                                        clientHand.remove(card);
                                        orientation = "top";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "bottom":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [bottom")) {
                            for (String card : clientHand) {
                                if (card.equals("TurnLeft")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("TurnLeft");
                                        clientHand.remove(card);
                                        orientation = "bottom";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                        }
                        break;
                    case "right":
                        if(!checkRobotField(xFuture, yFuture).contains("Wall [right")) {
                            boolean finished = false;
                            for (String card : clientHand) {
                                if (card.equals("UTurn")) {
                                    if (cardCounter < 5) {
                                        cardCounter++;
                                        register.add("UTurn");
                                        clientHand.remove(card);
                                        finished = true;
                                        orientation = "right";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                        break;
                                    }
                                }
                            }
                            if(!checkRobotField(xFuture, yFuture).contains("ConveyorBelt")) {
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (String card : clientHand) {
                                        if (card.equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "right";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "right";
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                        done = true;
                                    }
                                }
                            }
                        }
                        break;
                }
                break;
        }
    }

    private boolean moveAndCheckAgain() throws InterruptedException {
        xFuture = xRobot;
        yFuture = yRobot;
        boolean safe = true;
        boolean wall = false;
        for(String card : clientHand){
            if(card.equals("MoveI")){
                switch(orientation){
                    case "top":
                        if(checkRobotField(xFuture, yFuture).contains("Wall [top")){
                            wall = true;
                        }
                        yFuture--;
                        if(yFuture < 0){
                            safe = false;
                        }
                        break;
                    case "right":
                        if(checkRobotField(xFuture, yFuture).contains("Wall [right")){
                            wall = true;
                        }
                        xFuture++;
                        if(xFuture > 12){
                            safe = false;
                        }
                        break;
                    case "bottom":
                        if(checkRobotField(xFuture, yFuture).contains("Wall [bottom")){
                            wall = true;
                        }
                        yFuture++;
                        if(yFuture > 9){
                            safe = false;
                        }
                        break;
                    case "left":
                        if(checkRobotField(xFuture, yFuture).contains("Wall [left")){
                            wall = true;
                        }
                        xFuture--;
                        if(xFuture < 0){
                            safe = false;
                        }
                        break;
                }
                if(safe && !wall) {
                    cardCounter++;
                    xRobot = xFuture;
                    yRobot = yFuture;
                    register.add("MoveI");
                    clientHand.remove("MoveI");
                    return false;
                }else{
                    return true;
                }
            }
        }
        return true;
    }

    private void standingOnConveyorInOppositeDirection() throws InterruptedException {
        xFuture = xRobot;
        yFuture = yRobot;
        futureOrientation = orientation;

        int xDifference = xCheckpoint - xRobot;
        int yDifference = yCheckpoint - yRobot;
        String direction;

        if (Math.abs(xDifference) >= Math.abs(yDifference)) {
            direction = (xDifference > 0) ? "right" : "left";
        } else {
            direction = (yDifference < 0) ? "top" : "bottom";
        }
        switch(direction){
            case "top":
                if(checkRobotField(xRobot, yRobot).contains("ConveyorBelt 2 [bottom")){
                    switch (orientation){
                        case "top":
                            for(String card : clientHand){
                                if(card.equals("TurnRight")){
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                        case "right":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(xFuture+1 <= 12) {
                                        xFuture++;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                        case "bottom":
                            for(String card : clientHand){
                                if(card.equals("TurnLeft")){
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                        case "left":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(xFuture-1 >= 0) {
                                        xFuture--;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                    }
                }
                break;
            case "right":
                if(checkRobotField(xRobot, yRobot).contains("ConveyorBelt 2 [left")){
                    switch (orientation){
                        case "top":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(yFuture-1 >= 0) {
                                        yFuture--;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                        case "right":
                            for(String card : clientHand){
                                if(card.equals("TurnRight")){
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                        case "bottom":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(yFuture+1 <= 9) {
                                        yFuture++;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                        case "left":
                            for(String card : clientHand){
                                if(card.equals("TurnLeft")){
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                    }
                }
                break;
            case "bottom":
                if(checkRobotField(xRobot, yRobot).contains("ConveyorBelt 2 [bottom")){
                    switch (orientation){
                        case "top":
                            for(String card : clientHand){
                                if(card.equals("TurnLeft")){
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                        case "right":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(xFuture+1 <= 12) {
                                        xFuture++;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                        case "bottom":
                            for(String card : clientHand){
                                if(card.equals("TurnRight")){
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                        case "left":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(xFuture-1 >= 0) {
                                        xFuture--;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                    }
                }
                break;
            case "left":
                if(checkRobotField(xRobot, yRobot).contains("ConveyorBelt 2 [left")){
                    switch (orientation){
                        case "top":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(yFuture-1 >= 0) {
                                        yFuture--;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                        case "right":
                            for(String card : clientHand){
                                if(card.equals("TurnLeft")){
                                    cardCounter++;
                                    register.add("TurnLeft");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                        case "bottom":
                            for(String card1 : clientHand){
                                if(card1.equals("MoveI")){
                                    if(yFuture+1 <= 9) {
                                        yFuture++;
                                        cardCounter++;
                                        register.add("MoveI");
                                        clientHand.remove(card1);
                                        xRobot = xFuture;
                                        yRobot = yFuture;
                                    }
                                    break;
                                }
                            }
                            turnFlag = true;
                            break;
                        case "left":
                            for(String card : clientHand){
                                if(card.equals("TurnRight")){
                                    cardCounter++;
                                    register.add("TurnRight");
                                    clientHand.remove(card);
                                    checkBlueConveyorBelts(checkRobotField(xFuture, yFuture));
                                    orientation = futureOrientation;
                                    xRobot = xFuture;
                                    yRobot = yFuture;
                                    turnFlag = true;
                                    break;
                                }
                            }
                            break;
                    }
                }
                break;
        }
    }

    private void turnsWithGears(){
        //turnInCheckpointDirection needs different futureOrientation if you are standing on a gear
        if(checkRobotField(xRobot, yRobot).contains("Gear [clockwise")){
            futureOrientation = getResultingOrientation("clockwise", orientation);
        }else if(checkRobotField(xRobot, yRobot).contains("Gear [counterclockwise")){
            futureOrientation = getResultingOrientation("counterclockwise", orientation);
        }
    }

    private void checkMoveCheckpointBeforeConveyorBelt(){
        //if you are able to reach the checkpoint with one move make that move before ConveyorBelts activate
        xFuture = xRobot;
        yFuture = yRobot;
        switch(orientation){
            case "top":
                for(String card : clientHand){
                    if(card.equals("MoveI")){
                        yFuture--;
                        if(yFuture == yCheckpoint && xFuture == xCheckpoint){
                            cardCounter++;
                            register.add("MoveI");
                            clientHand.remove(card);
                            yRobot = yFuture;
                            break;
                        }
                    }
                }
                break;
            case "right":
                for(String card : clientHand){
                    if(card.equals("MoveI")){
                        xFuture++;
                        if(yFuture == yCheckpoint && xFuture == xCheckpoint){
                            cardCounter++;
                            register.add("MoveI");
                            clientHand.remove(card);
                            xRobot = xFuture;
                            break;
                        }
                    }
                }
                break;
            case "bottom":
                for(String card : clientHand){
                    if(card.equals("MoveI")){
                        yFuture++;
                        if(yFuture == yCheckpoint && xFuture == xCheckpoint){
                            cardCounter++;
                            register.add("MoveI");
                            clientHand.remove(card);
                            yRobot = yFuture;
                            break;
                        }
                    }
                }
                break;
            case "left":
                for(String card : clientHand){
                    if(card.equals("MoveI")){
                        xFuture--;
                        if(yFuture == yCheckpoint && xFuture == xCheckpoint){
                            cardCounter++;
                            register.add("MoveI");
                            clientHand.remove(card);
                            xRobot = xFuture;
                            break;
                        }
                    }
                }
                break;
        }
    }
}
