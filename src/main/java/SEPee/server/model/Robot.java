package SEPee.server.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Robot {
    private int x;
    private int y;
    private Orientation orientation;
    //private int energyReserve;

    public Robot(int x, int y) {
        this.x = x;
        this.y = y;
        // Other initializations if needed
        //orientation
    }

    public void fireLaser(Orientation target){
    }
}