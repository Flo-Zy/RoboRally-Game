package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPoint extends Field{
    //private int speed;
    //private String[] orientations;
    //private int[] registers;
    //private int count;
    public CheckPoint(String isOnBoard, int checkPointNumber){
        super("CheckPoint", isOnBoard, checkPointNumber);
    }
}
