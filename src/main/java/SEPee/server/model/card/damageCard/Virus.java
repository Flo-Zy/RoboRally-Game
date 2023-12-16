package SEPee.server.model.card.damageCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Virus extends DamageCard{
    public Virus() {
        super("Virus", " ");
    }
    @Override
    public String getImageUrl() {
        return " ";
    }
}
