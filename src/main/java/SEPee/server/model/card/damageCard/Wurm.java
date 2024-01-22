package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

/**
 * class for implementing the Worm card
 */
@Getter
@Setter
public class Wurm extends DamageCard{
    public Wurm() {
        super("Worm", "/boardElementsPNGs/DamageCards/Worm.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/DamageCards/Worm.png";
    }
}
