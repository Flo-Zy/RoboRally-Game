package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartPoint extends Field{

    public StartPoint(Position position){
        super("Startpoint", position);
    }
}
