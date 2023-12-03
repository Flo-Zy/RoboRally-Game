package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GearClockwise extends Field{
    //
    public GearClockwise(Position position){
        super("GearClockwise", position);
        //orientations = "clockwise";
    }
}
