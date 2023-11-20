package SEPee.server.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Robot {
    private Position position;
    private Direction direction;
    private int energyReserve;

    public void fireLaser(Position target){

    }
}