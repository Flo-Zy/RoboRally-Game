package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Again extends ProgCard {
    public Again() {
        super("Again", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_09.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_09.png";
    }
}
