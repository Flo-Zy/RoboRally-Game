package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * parent class for all fields
 * with constructors for every single type of field
 */
@Getter
@Setter
public abstract class Field {
    @SerializedName("type")
    private String type;
    @SerializedName("isOnBoard")
    private String isOnBoard;
    @Getter
    private String [] orientation;
    @Getter
    private int speed;

    private int checkPointNumber;
    @Getter
    private int [] registers;

    public Field(String type, String isOnBoard) {
        this.type = type;
        this.isOnBoard = isOnBoard;
    }

    // field constructor for walls
    public Field(String type, String isOnBoard, String[] orientation) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientation = orientation;
    }

    // field constructor for checkpoints
    public Field(String type, String isOnBoard, int checkPointNumber){
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.checkPointNumber = checkPointNumber;
    }

    //field constructor for belts
    public Field(String type, String isOnBoard, int speed, String[] orientation) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientation = orientation;
        this.speed = speed;
    }

    //field constructor for PushPanels
    public Field(String type, String isOnBoard, String[] orientation, int[] registers) {
        this.type = type;
        this.isOnBoard = isOnBoard;
        this.orientation = orientation;
        this.registers = registers;
    }






}