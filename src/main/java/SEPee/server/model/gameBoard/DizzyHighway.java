package SEPee.server.model.gameBoard;


import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import SEPee.server.model.field.*;

import java.util.ArrayList;
import java.util.Arrays;

public class DizzyHighway extends GameBoard{
    public DizzyHighway(){
        super("5B", "DizzyHighway", 13,10);

        //StartingField A
        addField(0,3, new StartPoint(new Position(0,3)));
        addField(0,4, new Antenna(new Position(0,4), new ArrayList<>(Arrays.asList(Direction.EAST))));
        addField(0,6, new StartPoint(new Position(0,6)));

        addField(1,1, new StartPoint(new Position(1,1)));
        addField(1,2, new Wall(new Position(1,2), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        ((Wall) getGameBoard().get(1).get(2).get(1)).setBlockedNorth(true);
        addField(1,4, new StartPoint(new Position(1,4)));
        addField(1,5, new StartPoint(new Position(1,5)));
        addField(1,7, new Wall(new Position(1,7), new ArrayList<>(Arrays.asList(Direction.SOUTH))));
        ((Wall) getGameBoard().get(1).get(7).get(1)).setBlockedSouth(true);
        addField(1,8, new StartPoint(new Position(1,8)));

        addField(2, 0, new GreenConveyorBelt(new Position(2,0),new ArrayList<>(Arrays.asList(Direction.EAST))));
        addField(2,4, new Wall(new Position(2,4), new ArrayList<>(Arrays.asList(Direction.EAST))));
        ((Wall) getGameBoard().get(2).get(4).get(1)).setBlockedEast(true);
        addField(2,5, new Wall(new Position(2,5), new ArrayList<>(Arrays.asList(Direction.EAST))));
        ((Wall) getGameBoard().get(2).get(5).get(1)).setBlockedEast(true);
        addField(2,9, new GreenConveyorBelt(new Position(2,9), new ArrayList<>(Arrays.asList(Direction.EAST))));

        //Map 5B (DizzyHighway)
        addField(3,7, new BlueConveyorBelt(new Position(3,7), new ArrayList<>(Arrays.asList(Direction.EAST))));
        addField(3,8, new BlueConveyorBelt(new Position(3,8), new ArrayList<>(Arrays.asList(Direction.EAST))));
        addField(3,9, new EnergySpace(new Position(3,9)));

        addField(4,0,new BlueConveyorBelt(new Position(4,0), new ArrayList<>(Arrays.asList(Direction.SOUTH))));
        addField(4,1,new BlueConveyorBelt(new Position(4,1), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.NORTH, Direction.EAST))));
        addField(4,2,new BlueConveyorBelt(new Position(4,2), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.NORTH))));
        addField(4,3,new BlueConveyorBelt(new Position(4,3), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.NORTH))));
        addField(4,4,new BlueConveyorBelt(new Position(4,4), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.NORTH))));
        addField(4,5,new BlueConveyorBelt(new Position(4,5), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.NORTH))));
        addField(4,6,new BlueConveyorBelt(new Position(4,6), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.NORTH))));
        addField(4,7,new BlueConveyorBelt(new Position(4,7), new ArrayList<>(Arrays.asList(Direction.SOUTH, Direction.WEST, Direction.NORTH))));
        addField(4,8,new BlueConveyorBelt(new Position(4,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST, Direction.NORTH))));

        addField(5,0,new BlueConveyorBelt(new Position(5,0), new ArrayList<>(Arrays.asList(Direction.SOUTH))));
        addField(5,1,new BlueConveyorBelt(new Position(5,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.NORTH, Direction.EAST))));
        addField(5,2, new EnergySpace(new Position(5,2)));
        addField(5,8,new BlueConveyorBelt(new Position(5,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST))));


        addField(6,1,new BlueConveyorBelt(new Position(6,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.EAST))));
        addField(6,3, new Wall(new Position(6,3), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        ((Wall) getGameBoard().get(6).get(3).get(1)).setBlockedNorth(true);
        addField(6,4,new Wall(new Position(6,4), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        ((Wall) getGameBoard().get(6).get(4).get(1)).setBlockedNorth(true);
        addField(6,4, new BoardLaser(new Position(6,4), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        ((BoardLaser) getGameBoard().get(6).get(4).get(2)).set1Laser();
        addField(6,6,new Wall(new Position(6,6), new ArrayList<>(Arrays.asList(Direction.WEST))));
        ((Wall) getGameBoard().get(6).get(6).get(1)).setBlockedWest(true);
        addField(6,8,new BlueConveyorBelt(new Position(6,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST))));




        addField(7,8,new BlueConveyorBelt(new Position(7,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST))));
        addField(7,6, new Wall(new Position(7,6), new ArrayList<>(Arrays.asList(Direction.EAST))));
        ((Wall) getGameBoard().get(7).get(6).get(1)).setBlockedEast(true);
        addField(7,6,new BoardLaser(new Position(7,6), new ArrayList<>(Arrays.asList(Direction.WEST))));
        ((BoardLaser) getGameBoard().get(7).get(6).get(2)).set1Laser();
        addField(7,5, new EnergySpace(new Position(7,5)));
        addField(7,3, new Reboot(new Position(7,3), new ArrayList<>(Arrays.asList(Direction.SOUTH))));
        addField(7,1, new BlueConveyorBelt(new Position(7,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.EAST))));

        addField(8,8, new BlueConveyorBelt(new Position(8,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST))));
        addField(8,4, new EnergySpace(new Position(8,4)));
        addField(8,3, new Wall(new Position(8,3), new ArrayList<>(Arrays.asList(Direction.WEST))));
        ((Wall) getGameBoard().get(8).get(3).get(1)).setBlockedWest(true);
        addField(8,3, new BoardLaser(new Position(8,3), new ArrayList<>(Arrays.asList(Direction.EAST))));
        ((BoardLaser) getGameBoard().get(8).get(3).get(2)).set1Laser();
        addField(8,1, new BlueConveyorBelt(new Position(8,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.EAST))));

        addField(9,8, new BlueConveyorBelt(new Position(9,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST))));
        addField(9,6, new Wall(new Position(9,6), new ArrayList<>(Arrays.asList(Direction.SOUTH))));
        ((Wall) getGameBoard().get(9).get(6).get(1)).setBlockedSouth(true);
        addField(9,5, new Wall(new Position(9,5), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        ((Wall) getGameBoard().get(9).get(5).get(1)).setBlockedNorth(true);
        addField(9,5, new BoardLaser(new Position(9,5), new ArrayList<>(Arrays.asList(Direction.SOUTH))));
        ((BoardLaser) getGameBoard().get(9).get(5).get(2)).set1Laser();
        addField(9,3,new Wall(new Position(9,3), new ArrayList<>(Arrays.asList(Direction.EAST))));
        ((Wall) getGameBoard().get(9).get(3).get(1)).setBlockedEast(true);
        addField(9,1, new BlueConveyorBelt(new Position(9,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.EAST))));

        addField(10,9, new BlueConveyorBelt(new Position(10,9), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        addField(10,8, new BlueConveyorBelt(new Position(10,8), new ArrayList<>(Arrays.asList(Direction.EAST, Direction.WEST, Direction.SOUTH))));
        addField(10,7, new EnergySpace(new Position(10, 7)));
        addField(10,1, new BlueConveyorBelt(new Position(10,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.EAST))));

        addField(11, 9, new BlueConveyorBelt(new Position(11,9), new ArrayList<>(Arrays.asList(Direction.NORTH))));
        addField(11, 8, new BlueConveyorBelt(new Position(11,8), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.WEST, Direction.SOUTH))));
        addField(11, 7, new BlueConveyorBelt(new Position(11,7), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH))));
        addField(11, 6, new BlueConveyorBelt(new Position(11,6), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH))));
        addField(11, 5, new BlueConveyorBelt(new Position(11,5), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH))));
        addField(11, 4, new BlueConveyorBelt(new Position(11,4), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH))));
        addField(11, 3, new BlueConveyorBelt(new Position(11,3), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.SOUTH))));
        addField(11, 2, new BlueConveyorBelt(new Position(11,2), new ArrayList<>(Arrays.asList(Direction.NORTH, Direction.EAST, Direction.SOUTH))));
        addField(11, 1, new BlueConveyorBelt(new Position(11,1), new ArrayList<>(Arrays.asList(Direction.WEST, Direction.EAST, Direction.SOUTH ))));

        addField(12,3, new Checkpoint(new Position(12,3)));
        addField(12,2, new BlueConveyorBelt(new Position(12,2), new ArrayList<>(Arrays.asList(Direction.WEST))));
        addField(12, 1, new BlueConveyorBelt(new Position(12,1), new ArrayList<>(Arrays.asList(Direction.WEST))));
        addField(12,0, new EnergySpace(new Position(12,0)));

        for(int i = 0; i < x; i++){
            for(int j = 0; j < y; j++){
                for(int k = 0; k < getGameBoard().get(i).get(j).size(); k++){
                    getGameBoard().get(i).get(j).get(k).setIsOnBoard(getBoardId());
                }
            }
        }

    }

}
