package SEPee.server.model.card.damageCard;

import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

/**
 * parent class for all damage cards
 */
@Getter
@Setter
public abstract class DamageCard extends Card {
    public DamageCard(String name, String imageUrl) {
        super(name, imageUrl);
    }
    public void makeEffect(){}

}
