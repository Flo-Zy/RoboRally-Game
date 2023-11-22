package SEPee.server.model.card.progCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class ProgCard {
    private String name;

    public void makeEffect(){}
}