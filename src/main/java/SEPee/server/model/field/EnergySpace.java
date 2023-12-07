package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EnergySpace extends Field{
    //private int speed;
    //private String[] orientations;
    //private int[] registers;
    @SerializedName("count")
    private int count;

    public EnergySpace(String isOnBoard, int count){
        super("EnergySpace", isOnBoard);
        this.count = count;
    }
}
