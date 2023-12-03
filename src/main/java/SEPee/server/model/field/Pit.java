package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Pit extends Field{

    public Pit(Position position){
        super("Pit", position);
    }
}
