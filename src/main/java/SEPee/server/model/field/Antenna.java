package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;

@Getter
@Setter
public class Antenna extends Field{

    //private int speed;
    @SerializedName("orientations")
    private String[] orientations;
    //private int[] registers;
    //private int count;

    public Antenna(String isOnBoard, String[] orientations) {
        super("Antenna", isOnBoard);
        this.orientations = orientations;
    }
}
