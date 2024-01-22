package SEPee.server.model.card.progCard;
import lombok.Getter;
import lombok.Setter;

/**
 * class for implementing the Again card
 */
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
