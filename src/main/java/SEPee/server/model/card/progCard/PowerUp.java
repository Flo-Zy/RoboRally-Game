package SEPee.server.model.card.progCard;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PowerUp extends ProgCard {
    public PowerUp() {
        super("PowerUp", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_06.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_06.png";
    }
}
