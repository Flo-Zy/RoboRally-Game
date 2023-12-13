package SEPee.server.model.card.progCard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UTurn extends ProgCard {
    public UTurn() {
        super("UTurn", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_05.png");
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_05.png";
    }
}
