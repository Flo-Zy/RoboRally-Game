package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProgCard extends Card {
    public void makeEffect(){}
}