package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeftTurn extends ProgCard {
    public LeftTurn() {
        super("LeftTurn", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_07.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_07.png";
    }
}
