package SEPee.server.model.field;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Empty extends Field{

    public Empty(Position position){
        super("Empty", position);
    }
}
