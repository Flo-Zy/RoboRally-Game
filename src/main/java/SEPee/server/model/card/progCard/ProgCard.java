package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

/**
 * parent class for all programming cards
 */
@Getter
@Setter
public abstract class ProgCard extends Card {
    public ProgCard(String name, String imageUrl) {
        super(name, imageUrl);
    }
    public void makeEffect(){}

}