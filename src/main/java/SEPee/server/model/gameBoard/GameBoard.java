package SEPee.server.model.gameBoard;

import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.ArrayList;

@Getter
@Setter
public class GameBoard {
    private String boardId;
    private String bordName;
    private List<List<List<Field>>> gameBoard; // Spalte(Zeile(FelderTypen()))
    private int checkpointAmount;

    GameBoard(String boardId, String bordName, int sumColumns, int checkpointAmount){
        this.boardId = boardId;
        this.bordName = bordName;
        this.gameBoard = new ArrayList<>(sumColumns);
        this.checkpointAmount = checkpointAmount;
    }

    public void addRow(List<List<Field>> row) {
        gameBoard.add(row);
    }



    public int getRebootX(){
        return 0;
    }
    public int getRebootY(){
        return 0;
    }

    public String getOrientationOfReboot(){ return ""; }

    public String checkRebootConditions(int xCoordinate, int yCoordinate) {
        String rebootTo = "continue";

        if ((yCoordinate < 0 && xCoordinate < 3) || (xCoordinate < 0) || (yCoordinate > 9 && xCoordinate < 3)) {
            // top left condition, left condition, bottom left condition, weil starting board links ist
            rebootTo = "startingPoint";
        } else if (yCoordinate < 0 || xCoordinate > 12 || yCoordinate > 9) {
            // top right, right, bottom right conditions
            rebootTo = "rebootField";
        }
        return rebootTo;
    }

    public List<Field> getFieldsAt(int x, int y) {
        return null;
    }

}