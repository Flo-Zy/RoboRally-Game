package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Wurm extends DamageCard{
    public Wurm() {
        super("Wurm", " ");
    }
    @Override
    public String getImageUrl() {
        return " ";
    }
}
