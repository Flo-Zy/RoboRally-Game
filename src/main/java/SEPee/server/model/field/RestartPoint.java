package SEPee.server.model.field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RestartPoint extends Field {
    public RestartPoint(String isOnBoard){
        super("PushPanel", isOnBoard);
    }
}