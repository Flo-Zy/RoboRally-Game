package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GearNotClockwise extends Field{
    //private String orientations;
    public GearNotClockwise(Position position){
        super("GearNotClockwise", position);
       // orientations = "counterClockwise";
    }
}
