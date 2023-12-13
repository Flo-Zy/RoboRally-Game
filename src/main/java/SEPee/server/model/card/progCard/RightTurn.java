package SEPee.server.model.card.progCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RightTurn extends ProgCard {
    public RightTurn() {
        super("RightTurn", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_04.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_04.png";
    }
}
