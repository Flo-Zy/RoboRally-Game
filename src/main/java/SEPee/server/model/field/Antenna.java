package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import javafx.geometry.Pos;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Antenna extends Field{

    public Antenna(Position position, ArrayList<Direction> direction){
        super("Antenna", position, direction);
    }
}
