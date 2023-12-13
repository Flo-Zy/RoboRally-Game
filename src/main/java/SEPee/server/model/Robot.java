package SEPee.server.model;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Robot {
    private int x;
    private int y;
    //private Orientation orientation;
    //private int energyReserve;
    private String orientation;

    /*
    public void setOrientation(String orientation);{
        orientation = this.orientation;
    }

     */

    public Robot(int x, int y, String orientation) {
        this.x = x;
        this.y = y;
        this.orientation = orientation;
        // Other initializations if needed
        //orientation
    }

    public void fireLaser(Orientation target){
    }
}