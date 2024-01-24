package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

/**
 * class for the check point fields
 */
@Getter
@Setter
public class CheckPoint extends Field{
    public CheckPoint(String isOnBoard, int checkPointNumber){
        super("CheckPoint", isOnBoard, checkPointNumber);
    }
}
