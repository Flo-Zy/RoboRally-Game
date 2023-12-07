package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gear extends Field{
    //
    public Gear(Position position){
        super("GearClockwise", position);
        //orientations = "clockwise";
    }
}
