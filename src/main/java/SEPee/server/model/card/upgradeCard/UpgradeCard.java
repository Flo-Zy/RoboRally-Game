package SEPee.server.model.card.upgradeCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class UpgradeCard {
    private String name;
    private int cost;
    private boolean temporary;

    public void makeEffect(){

    }
}
