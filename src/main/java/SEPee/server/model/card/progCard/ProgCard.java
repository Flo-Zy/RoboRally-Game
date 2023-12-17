package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProgCard extends Card {
    public ProgCard(String name, String imageUrl) {
        super(name, imageUrl);
    }
    public void makeEffect(){}

}