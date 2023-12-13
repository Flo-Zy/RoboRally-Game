package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Move3 extends ProgCard {
    public Move3() {
        super("Move3", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_03.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_03.png";
    }
}
