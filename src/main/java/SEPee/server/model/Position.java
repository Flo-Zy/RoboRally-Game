package SEPee.server.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Position {
    private int coordinateX;
    private int coordinateY;

    public Position(int x, int y){
        this.coordinateX = x;
        this.coordinateY = y;
    }
    public boolean isRobotOnField(){
        return false;
    }
}