package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveII extends ProgCard {
    public MoveII() {
        super();
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_03.png";
    }
}
