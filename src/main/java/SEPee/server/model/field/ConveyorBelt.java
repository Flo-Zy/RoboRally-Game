package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class ConveyorBelt extends Field{
    @SerializedName("speed")
    private int speed;
    @Getter
    @SerializedName("orientations")
    private static String[] orientations;
    //private int[] registers;
    //private int count;
    public ConveyorBelt(String isOnBoard, int speed, String[] orientations) {
        super("ConveyorBelt", isOnBoard);
        this.speed = speed;
        this.orientations = orientations;
    }
}
