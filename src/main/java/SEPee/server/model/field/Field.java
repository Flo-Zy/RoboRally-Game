package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Field {
    private String name;
    private Position position;
    private Direction direction;

    public void makeEffect(){

    }
}