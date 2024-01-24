package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

/**
 * class for the fields containign energy spaces
 */
@Getter
@Setter
public class EnergySpace extends Field{
    @SerializedName("count")
    private int count;

    public EnergySpace(String isOnBoard, int count){
        super("EnergySpace", isOnBoard);
        this.count = count;
    }
}
