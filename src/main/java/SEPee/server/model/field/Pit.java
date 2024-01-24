package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

/**
 * class for fields containing pits
 */
@Getter
@Setter
public class Pit extends Field{

    public Pit(String isOnBoard){
        super("Pit", isOnBoard);
    }
}
