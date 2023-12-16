package SEPee.server.model.card.damageCard;

import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class DamageCard extends Card {
    public DamageCard(String name, String imageUrl) {
        super(name, imageUrl);
    }
    public void makeEffect(){}

}
