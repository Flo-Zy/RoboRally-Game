package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class BoardLaser extends Field{
    private int counter;

    public BoardLaser(Position position, ArrayList<Direction> direction){
        super("BoardLaser", position, direction);
    }

    public void set1Laser(){
        counter = 1;
    }

    public void set2Laser(){
        counter = 2;
    }

    public void set3Laser(){
        counter = 3;
    }
}
