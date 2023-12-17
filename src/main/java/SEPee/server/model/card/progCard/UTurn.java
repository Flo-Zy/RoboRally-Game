package SEPee.server.model.card.progCard;
import SEPee.server.model.Robot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UTurn extends ProgCard {
    public UTurn() {
        super("UTurn", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_05.png");
    }


    public static void makeEffect(Robot robot){

        String orientation = robot.getOrientation();

        switch (orientation){
            case "top":
                robot.setOrientation("bottom");
                break;

            case "bottom":
                robot.setOrientation("top");
                break;

            case "right":
                robot.setOrientation("left");
                break;

            case "left":
                robot.setOrientation("right");
                break;

            default:
                System.out.println("something wong wotation");
                break;
        }


    }

    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_05.png";
    }
}
