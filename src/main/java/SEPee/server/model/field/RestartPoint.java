package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestartPoint extends Field {
    //private int speed;
    //@SerializedName("orientations")
    //private String[] orientations;
    //@SerializedName("registers")
    //private int[] registers;
    //private int count;
    public RestartPoint(String isOnBoard){
        super("PushPanel", isOnBoard);
    }
}