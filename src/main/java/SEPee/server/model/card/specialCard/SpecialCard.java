package SEPee.server.model.card.specialCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class SpecialCard {
    private String name;

    public void makeEffect(){}
}