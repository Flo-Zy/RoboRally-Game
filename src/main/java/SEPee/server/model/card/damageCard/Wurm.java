package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wurm extends DamageCard{
    public Wurm() {
        super("Wurm", "/boardElementsPNGs/DamageCards/Worm.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/DamageCards/Worm.png";
    }
}
