package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * class for fields that contain gears
 */
@Getter
@Setter
public class Gear extends Field{
    @SerializedName("orientations")
    private String[] orientations;
    public Gear(String isOnBoard, String[] orientations){
        super("Gear", isOnBoard, orientations);
        this.orientations = orientations;
    }
}
