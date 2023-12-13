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

    private void setOrientationRight(){
        this.orientation = "right";
    }

    private void setOrientationBottom(){
        this.orientation = "bottom";
    }

    private void setOrientationLeft(){
        this.orientation = "left";
    }

    private void setOrientationTop(){
        this.orientation = "top";
    }

    public String getOrientation() {
        return orientation;
    }

    public Robot(int x, int y) {
        this.x = x;
        this.y = y;
        // Other initializations if needed
        //orientation
    }

    public void fireLaser(Orientation target){
    }
}