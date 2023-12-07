package SEPee.server.model.field;
import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Laser extends Field{
    //private int speed;
    @SerializedName("orientations")
    private String[] orientations;
    //private int[] registers;
    @SerializedName("count")
    private int count;
    public Laser(String isOnBoard, String[] orientations, int count){
        super("Laser", isOnBoard);
        this.orientations = orientations;
        this.count = count;
    }
}
