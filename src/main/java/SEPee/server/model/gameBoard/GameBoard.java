package SEPee.server.model.gameBoard;

import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

/**
 * parent class for the different game maps
 * @author Franziska, Felix
 */
@Getter
@Setter
public class GameBoard {
    private String boardId;
    private String bordName;
    private List<List<List<Field>>> gameBoard; // Spalte(Zeile(FelderTypen()))
    private int checkpointAmount;

    //DizzyHighway
    private int energySpace39 = 1;
    private int energySpace52 = 1;
    private int energySpace75 = 1;
    private int energySpace84 = 1;
    private int energySpace107 = 1;

    //ExtraCrispy
    private int energySpace34 = 1;
    private int energySpace80 = 1;
    private int energySpace114 = 1;

    //LostBearings
    private int energySpace57 = 1;
    private int energySpace74 = 1;
    private int energySpace85 = 1;
    private int energySpace102 = 1;

    //DeathTrap
    private int energySpace23 = 1;
    private int energySpace37 = 1;
    private int energySpace42 = 1;
    private int energySpace46 = 1;
    private int energySpace62 = 1;
    private int energySpace76 = 1;

    GameBoard(String boardId, String bordName, int sumColumns, int checkpointAmount){
        this.boardId = boardId;
        this.bordName = bordName;
        this.gameBoard = new ArrayList<>(sumColumns);
        this.checkpointAmount = checkpointAmount;
    }

    /**
     * adds a row to the game board
     * @param row the row to add
     */
    public void addRow(List<List<Field>> row) {
        gameBoard.add(row);
    }

    /**
     * in all the child classes this return the x variable of the reboot field
     * @return the x variable of the reboot field
     */
    public int getRebootX(){
        return 0;
    }
    /**
     * in all the child classes this return the y variable of the reboot field
     * @return the y variable of the reboot field
     */
    public int getRebootY(){
        return 0;
    }

    /**
     * in all the child classes this return the direction in which the arrow on the reboot field points
     * @return the direction in which the arrow of the reboot field is pointing
     */
    public String getOrientationOfReboot(){ return ""; }

    /**
     * checks whether to reboot to your starting point or the reboot field or not at all
     * @param xCoordinate x coordinate of the robot
     * @param yCoordinate y coordinate of the robot
     * @return "continue" if you do not have to reboot "startingPoint" if you have to reboot on your starting point
     * "rebootField" if you have to reboot on the reboot field
     */
    public String checkRebootConditions(int xCoordinate, int yCoordinate) {
        String rebootTo = "continue";

        if ((yCoordinate < 0 && xCoordinate < 3) || (xCoordinate < 0) || (yCoordinate > 9 && xCoordinate < 3)) {
            // top left condition, left condition, bottom left condition, because starting board is on the left
            rebootTo = "startingPoint";
        } else if (yCoordinate < 0 || xCoordinate > 12 || yCoordinate > 9) {
            // top right, right, bottom right conditions
            rebootTo = "rebootField";
        }
        return rebootTo;
    }

    /**
     * is overwritten in child classes to return all field elements on a field
     * @param x x coordinate of the field
     * @param y y coordinate of the field
     * @return a list of all field elements on this field
     */
    public List<Field> getFieldsAt(int x, int y) {
        return null;
    }

}