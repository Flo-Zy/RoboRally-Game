package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

/**
 * fields containing push panels
 */
@Getter
@Setter
public class PushPanel extends Field {
    @SerializedName("orientations")
    private String[] orientations;

    public PushPanel(String isOnBoard, String[] orientations, int[] registers){
        super("PushPanel", isOnBoard, orientations, registers);
        this.orientations = orientations;
    }
}