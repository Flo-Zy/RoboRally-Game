package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * class for the field containing the antenna
 */
@Getter
@Setter
public class Antenna extends Field{

    @SerializedName("orientations")
    private String[] orientations;

    public Antenna(String isOnBoard, String[] orientations) {
        super("Antenna", isOnBoard);
        this.orientations = orientations;
    }
}
