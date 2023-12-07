package SEPee.server.model.field;

import com.google.gson.annotations.SerializedName;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class StartPoint extends Field{
    //private int speed;
    //private String[] orientations;
    //private int[] registers;
    //private int count;

    public StartPoint(String isOnBoard){
        super("StartPoint", isOnBoard);
    }
}
