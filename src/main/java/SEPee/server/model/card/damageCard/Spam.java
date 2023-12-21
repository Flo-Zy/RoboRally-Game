package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Spam extends DamageCard{
    public Spam() {
        super("Spam", "/boardElementsPNGs/DamageCards/Spam.png");
    }
    @Override
    public String getImageUrl() {
        return " ";
    }
}
