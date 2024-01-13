package SEPee.client.model;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SelectedCard;
import SEPee.server.model.card.Card;
import SEPee.server.model.card.damageCard.Spam;
import SEPee.server.model.card.damageCard.TrojanHorse;
import SEPee.server.model.card.damageCard.Virus;
import SEPee.server.model.card.damageCard.Wurm;
import SEPee.server.model.card.progCard.*;
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
    private ArrayList<Card> clientHand = new ArrayList<>();
    private ArrayList<String> register = new ArrayList<>();
    private int xRobot;
    private int yRobot;
    private String orientation;

    public void setRegister(RobotAI robot,  ArrayList<Card> hand){
        this.clientHand = hand;
        xRobot = robot.getX();
        yRobot = robot.getY();
        orientation = robot.getOrientation();
        setCheckpoint();

        int filledRegisters = 0;
        boolean finished = false;
        while(!finished) {
            int oldRegister = filledRegisters;
            turnInCheckpointDirection();
            checkWall();
            move();
            filledRegisters = register.size();
            if(oldRegister == filledRegisters){
                finished = true;
            }
        }
        int j = 0;
        while(j < 5 - register.size()){
            if(register.isEmpty()) {
                for (Card card : clientHand) {
                    if(!card.equals("Again")){
                        register.add(card.getName());
                        clientHand.remove(card);
                        break;
                    }
                }
            }else{
                register.add(clientHand.get(0).getName());
                clientHand.remove(0);
            }

        }

        System.out.println("HIER AI KARTEN: "+register);
        int i = 1;
        for(String card: register){
            SelectedCard selectedCard = new SelectedCard(card, i);
            String serializedSelectedCard = Serialisierer.serialize(selectedCard);
            ClientAI.getWriter().println(serializedSelectedCard);
            i++;
        }
        register.clear();
    }

    private int calculateManhattanDistance(int xCoordinate, int yCoordinate){
        int x = xCoordinate;
        int y = yCoordinate;

        int manhattanDistance = Math.abs(x - xCheckpoint) + Math.abs(y - yCheckpoint);

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

    private void turnInCheckpointDirection(){
        String direction;
        int xDifference = xCheckpoint - xRobot;
        int yDifference = yCheckpoint - yRobot;

        if (Math.abs(xDifference) >= Math.abs(yDifference)) {
            direction = (xDifference > 0) ? "right" : "left";
        } else {
            direction = (yDifference < 0) ? "top" : "bottom";
        }
        if(!orientation.equals(direction)){
            switch(orientation){
                case "top":
                    switch (direction) {
                        case "right":
                            if (!checkRobotField(xRobot, yRobot).contains("Wall [right")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            orientation = "right";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "left":
                            if (!checkRobotField(yRobot, yRobot).contains("Wall [left")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            orientation = "left";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "bottom":
                            if (!checkRobotField(xRobot, yRobot).contains("Wall [bottom")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            orientation = "bottom";
                                            break;
                                        }
                                    }
                                }
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (Card card : clientHand) {
                                        if (card.getName().equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.getName().equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        RightTurn rightTurn = new RightTurn();
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove(rightTurn);
                                        clientHand.remove(rightTurn);
                                        orientation = "bottom";
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "bottom";
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case "right":
                    switch (direction) {
                        case "top":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [top")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            orientation = "top";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "bottom":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [bottom")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            orientation = "bottom";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "left":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [left")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            orientation = "left";
                                            break;
                                        }
                                    }
                                }
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (Card card : clientHand) {
                                        if (card.getName().equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.getName().equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        RightTurn rightTurn = new RightTurn();
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "left";
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        LeftTurn leftTurn = new LeftTurn();
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "left";
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case "bottom":
                    switch (direction) {
                        case "right":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [right")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            orientation = "right";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "left":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [left")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            orientation = "left";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "top":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [top")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            orientation = "top";
                                            break;
                                        }
                                    }
                                }
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (Card card : clientHand) {
                                        if (card.getName().equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.getName().equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        RightTurn turnRightCard = new RightTurn();
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "top";
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        LeftTurn turnLeftCard = new LeftTurn();
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "top";
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case "left":
                    switch (direction) {
                        case "top":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [top")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            orientation = "top";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "bottom":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [bottom")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            orientation = "bottom";
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "right":
                            if(!checkRobotField(xRobot, yRobot).contains("Wall [right")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            orientation = "right";
                                            break;
                                        }
                                    }
                                }
                                if (!finished && cardCounter < 4) {
                                    int turnRight = 0;
                                    int turnLeft = 0;
                                    for (Card card : clientHand) {
                                        if (card.getName().equals("TurnRight")) {
                                            turnRight++;
                                        } else if (card.getName().equals("TurnLeft")) {
                                            turnLeft++;
                                        }
                                    }
                                    if (turnRight >= 2) {
                                        cardCounter = cardCounter + 2;
                                        RightTurn turnRightCard = new RightTurn();
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                        orientation = "right";
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        LeftTurn turnLeftCard = new LeftTurn();
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        orientation = "right";
                                    }
                                }
                            }
                            break;
                    }
                    break;
            }
        }
    }

    private void move(){
        //conveyorBelts, Pits (beim gehen), nicht rauslaufen, Distanz besser
        // für die Zukunft: Pits (beim drehen) und andere Roboter und PushPanel und Gears???
        int initialDistance = calculateManhattanDistance(xRobot, yRobot);
        int xFuture = xRobot;
        int yFuture = yRobot;
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
        for(Card card : clientHand){
            if(card.getName().equals("MoveI") && !move1){
                move1 = true;
                switch (orientation){
                    case "top":
                        yFuture--;
                        if(!(yFuture < 0)){

                        }
                        break;
                    case "right":
                        xFuture++;
                        if(!(xFuture > 12)){

                        }
                        break;
                    case "bottom":
                        yFuture++;
                        if(!(yFuture > 9)){

                        }
                        break;
                    case "left":
                        xFuture--;
                        if(!(xFuture < 0)){

                        }
                        break;
                }
                xFuture = xRobot;
                yFuture = yRobot;
                futureOrientation = orientation;
            }else if(card.getName().equals("MoveII") && !move2){
                move2 = true;
            }else if(card.getName().equals("MoveIII") && !move3){
                move3 = true;
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
        }
    }

    public void checkWall(){
        switch (orientation){
            case "top":
                if(checkRobotField(xRobot, yRobot).contains("Wall [top")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [right")){
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                if(checkRobotField(xRobot, yRobot).contains("Wall [right")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [bottom")){
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                if(checkRobotField(xRobot, yRobot).contains("Wall [bottom")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [left")){
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                if(checkRobotField(xRobot, yRobot).contains("Wall [left")){
                    if(checkRobotField(xRobot, yRobot).contains("Wall [top")){
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnRight")){
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
                        for(Card card : clientHand){
                            if(card.getName().equals("TurnLeft")){
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

}
