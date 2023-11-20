package SEPee.server.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    private int coordinateX;
    private int coordinateY;

    public boolean isRobotOnField(){
        return false;
    }
}