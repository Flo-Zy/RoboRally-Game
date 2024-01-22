package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

/**
 * class for implementing the Virus card
 */
@Getter
@Setter
public class Virus extends DamageCard{
    public Virus() {
        super("Virus", "/boardElementsPNGs/DamageCards/Virus.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/DamageCards/Virus.png";
    }
}
