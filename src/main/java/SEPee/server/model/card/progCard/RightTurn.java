package SEPee.server.model.card.progCard;
import SEPee.server.model.Robot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RightTurn extends ProgCard {
    public RightTurn() {
        super();
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_04.png";
    }
}
