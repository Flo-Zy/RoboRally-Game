package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CheckPoint extends Field{
    private int counter;
    public CheckPoint(Position position){
        super("Checkpoint", position);
    }
}
