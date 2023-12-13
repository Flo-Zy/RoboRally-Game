package SEPee.server.model.card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Card {
    private String name;
    private String imageUrl;

    public void makeEffect(){}

}