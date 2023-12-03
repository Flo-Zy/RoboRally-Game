package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class PushPanels extends Field{
    private int[] register;
    public PushPanels(Position position,int[] register, ArrayList<Direction> direction){
        super("PushPanels", position, direction);
        this.register = register;
    }
}
