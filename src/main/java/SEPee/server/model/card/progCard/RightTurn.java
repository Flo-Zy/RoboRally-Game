package SEPee.server.model.card.progCard;
import SEPee.server.model.Robot;
import SEPee.server.model.ServerLogger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RightTurn extends ProgCard {
    public RightTurn() {
        super("TurnRight", "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_04.png");
    }

    public static void makeEffect(Robot robot){

        String orientation = robot.getOrientation();

        switch (orientation){
            case "top":
                robot.setOrientation("right");
                break;

            case "bottom":
                robot.setOrientation("left");
                break;

            case "right":
                robot.setOrientation("bottom");
                break;

            case "left":
                robot.setOrientation("top");
                break;

            default:
                ServerLogger.writeToServerLog("something went wrong");
                break;
        }


    }



    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_04.png";
    }
}
