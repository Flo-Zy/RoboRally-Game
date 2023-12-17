package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PushPanel extends Field {
    //private int speed;
    @SerializedName("orientations")
    private String[] orientations;
    @SerializedName("registers")
    private int[] registers;
    //private int count;
    public PushPanel(String isOnBoard, String[] orientations, int[] registers){
        super("PushPanel", isOnBoard);
        this.orientations = orientations;
        this.registers = registers;
    }
}