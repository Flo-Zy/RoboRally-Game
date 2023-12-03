package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Checkpoint extends Field{
    private int counter;
    public Checkpoint(Position position){
        super("Checkpoint", position);
    }
}
