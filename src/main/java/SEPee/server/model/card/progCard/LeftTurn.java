package SEPee.server.model.card.progCard;
import SEPee.server.model.Robot;
import SEPee.server.model.ServerLogger;
import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LeftTurn extends ProgCard {
    public LeftTurn() {
        super("TurnLeft", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_07.png");
    }

    public static void makeEffect(Robot robot){

        String orientation = robot.getOrientation();

        switch (orientation){
            case "top":
                robot.setOrientation("left");
                break;

            case "bottom":
                robot.setOrientation("right");
                break;

            case "right":
                robot.setOrientation("top");
                break;

            case "left":
                robot.setOrientation("bottom");
                break;

            default:
                ServerLogger.writeToServerLog("Something went wrong");
                break;
        }
    }

    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_07.png";
    }
}
