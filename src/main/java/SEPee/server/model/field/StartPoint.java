package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

/**
 * class for fields containing a starting point
 */
@Getter
@Setter
public class StartPoint extends Field{

    public StartPoint(String isOnBoard){
        super("StartPoint", isOnBoard);
    }
}
