package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * class for fields containing a wall
 */
@Getter
@Setter
public class Wall extends Field{
    @Getter
    @SerializedName("orientations")
    private static String[] orientations;
    //private int[] registers;
    //private int count;
    public Wall(String isOnBoard, String[] orientations){
        super("Wall", isOnBoard, orientations);
        this.orientations = orientations;
    }

}
