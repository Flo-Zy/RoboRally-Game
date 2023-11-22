package SEPee.server.model.field;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wall {
    private boolean isBlockedNorth;
    private boolean isBlockedEast;
    private boolean isBlockedSouth;
    private boolean isBlockedWest;
}
