package SEPee.server.model.field;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * class for fields that contain a board laser
 */
@Getter
@Setter
public class Laser extends Field{
    @SerializedName("orientations")
    private String[] orientations;
    @SerializedName("count")
    private int count;
    public Laser(String isOnBoard, String[] orientations, int count){
        super("Laser", isOnBoard);
        this.orientations = orientations;
        this.count = count;
    }
}
