package SEPee.server.model.card.damageCard;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DamageCard {
    private String name;
    public void makeEffect(){}
}
