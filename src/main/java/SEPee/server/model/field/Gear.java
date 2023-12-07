package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Gear extends Field{
    //private int speed;
    @SerializedName("orientations")
    private String[] orientations;
    //private int[] registers;
    //private int count;
    public Gear(String isOnBoard, String[] orientations){
        super("Gear", isOnBoard);
        this.orientations = orientations;
    }
}
