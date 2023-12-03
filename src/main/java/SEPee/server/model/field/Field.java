package SEPee.server.model.field;
import SEPee.server.model.Direction;
import SEPee.server.model.Position;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public abstract class Field {
    private String type;
    private String isOnBoard;
    private Position position;
    private ArrayList<Direction> orientations;

    public Field(String name, Position position){
        this.type = name;
        this.position = position;
    }

    public Field(String name, Position position, ArrayList<Direction> direction){
        this.type = name;
        this.position = position;
        this.orientations = direction;
    }
    public void makeEffect(){

    }
}