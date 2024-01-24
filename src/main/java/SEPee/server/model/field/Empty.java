package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

/**
 * class for the fields containing no field elements
 */
@Getter
@Setter
public class Empty extends Field {

    public Empty(String isOnBoard){
        super("Empty", isOnBoard);
    }
}
