package SEPee.server.model.field;
import SEPee.server.Direction;
import SEPee.server.Position;
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