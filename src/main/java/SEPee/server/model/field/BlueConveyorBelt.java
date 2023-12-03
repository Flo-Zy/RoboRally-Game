package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BlueConveyorBelt extends Field{
    private int speed;
    public BlueConveyorBelt(Position position, ArrayList<Direction> direction){
        super("BlueConveyorBelt", position, direction);
        this.speed = 2;
    }
}
