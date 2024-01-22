package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

/**
 * class for implementing the Spam card
 */
@Getter
@Setter
public class Spam extends DamageCard{
    public Spam() {
        super("Spam", "boardElementsPNGs/DamageCards/Spam.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/DamageCards/Spam.png";
    }
}
