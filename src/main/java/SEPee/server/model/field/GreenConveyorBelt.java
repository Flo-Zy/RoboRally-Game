package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class GreenConveyorBelt extends Field{

    private int speed;
    public GreenConveyorBelt(Position position, ArrayList<Direction> direction) {
        super("GreenConveyorBelt", position, direction);
        this.speed = 1;
    }
}
