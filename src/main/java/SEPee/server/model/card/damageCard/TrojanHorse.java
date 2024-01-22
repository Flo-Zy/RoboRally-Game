package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

/**
 * class for implementing the TrojanHorse card
 */
@Getter
@Setter
public class TrojanHorse extends DamageCard{
    public TrojanHorse() {
        super("TrojanHorse", "/boardElementsPNGs/DamageCards/TrojanHorse.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/DamageCards/TrojanHorse.png";
    }
}
