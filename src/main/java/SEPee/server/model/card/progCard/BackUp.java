package SEPee.server.model.card.progCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BackUp extends ProgCard {
    public BackUp() {
        super("BackUp", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_08.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_08.png";
    }
}
