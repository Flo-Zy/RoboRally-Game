package SEPee.client.model;

import SEPee.serialisierung.Serialisierer;
import SEPee.serialisierung.messageType.SelectedCard;
import SEPee.server.model.card.Card;
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

    public void setRegister(RobotAI robot,  ArrayList<Card> hand){
        this.clientHand = hand;
        setCheckpoint();
        turnInCheckpointDirection(robot);
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

    private int calculateManhattanDistance(RobotAI robot){
        int x = robot.getX();
        int y = robot.getY();

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

    private static String checkRobotField(RobotAI robot) {
        int robotX = robot.getX();
        int robotY = robot.getY();

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

    private void turnInCheckpointDirection(RobotAI robot){
        String direction = "---";
        if(gameBoard.getBordName().equals("Death Trap")){
            if (xCheckpoint > robot.getX()) {
                direction = "left";
            } else if (xCheckpoint < robot.getX()) {
                direction = "right";
            } else if (yCheckpoint > robot.getY()) {
                direction = "bottom";
            } else if (yCheckpoint < robot.getY()) {
                direction = "top";
            }
        }else {
            if (xCheckpoint > robot.getX()) {
                direction = "right";
            } else if (xCheckpoint < robot.getX()) {
                direction = "left";
            } else if (yCheckpoint > robot.getY()) {
                direction = "bottom";
            } else if (yCheckpoint < robot.getY()) {
                direction = "top";
            }
        }
        if(!robot.getOrientation().equals(direction)){
            switch(robot.getOrientation()){
                case "top":
                    switch (direction) {
                        case "right":
                            if (!checkRobotField(robot).contains("Wall [right")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "left":
                            if (!checkRobotField(robot).contains("Wall [left")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "bottom":
                            if (!checkRobotField(robot).contains("Wall [bottom")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            break;
                                        }
                                    }
                                }
                                if (!finished) {
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
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case "right":
                    switch (direction) {
                        case "top":
                            if(!checkRobotField(robot).contains("Wall [top")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "bottom":
                            if(!checkRobotField(robot).contains("Wall [bottom")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "left":
                            if(!checkRobotField(robot).contains("Wall [left")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            break;
                                        }
                                    }
                                }
                                if (!finished) {
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
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case "bottom":
                    switch (direction) {
                        case "right":
                            if(!checkRobotField(robot).contains("Wall [right")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "left":
                            if(!checkRobotField(robot).contains("Wall [left")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "top":
                            if(!checkRobotField(robot).contains("Wall [top")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            break;
                                        }
                                    }
                                }
                                if (!finished) {
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
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                    }
                                }
                            }
                            break;
                    }
                    break;
                case "left":
                    switch (direction) {
                        case "top":
                            if(!checkRobotField(robot).contains("Wall [top")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnRight")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnRight");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "bottom":
                            if(!checkRobotField(robot).contains("Wall [bottom")) {
                                for (Card card : clientHand) {
                                    if (card.getName().equals("TurnLeft")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("TurnLeft");
                                            clientHand.remove(card);
                                            break;
                                        }
                                    }
                                }
                            }
                            break;
                        case "right":
                            if(!checkRobotField(robot).contains("Wall [right")) {
                                boolean finished = false;
                                for (Card card : clientHand) {
                                    if (card.getName().equals("UTurn")) {
                                        if (cardCounter < 5) {
                                            cardCounter++;
                                            register.add("UTurn");
                                            clientHand.remove(card);
                                            finished = true;
                                            break;
                                        }
                                    }
                                }
                                if (!finished) {
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
                                        register.add("TurnRight");
                                        register.add("TurnRight");
                                        clientHand.remove("TurnRight");
                                        clientHand.remove("TurnRight");
                                    } else if (turnLeft >= 2) {
                                        cardCounter = cardCounter + 2;
                                        register.add("TurnLeft");
                                        register.add("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                        clientHand.remove("TurnLeft");
                                    }
                                }
                            }
                            break;
                    }
                    break;
            }
        }
    }


}
