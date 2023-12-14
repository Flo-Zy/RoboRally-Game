package SEPee.server.model.card.progCard;
import SEPee.server.model.Robot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MoveII extends ProgCard {
    public MoveII() {
        super();
    }

    public static void makeEffect(Robot robot){
        // orientation von robot
        String orientation = robot.getOrientation();
        int xCoordinate = robot.getX();
        int yCoordinate = robot.getY();

        switch (orientation){
            case "top":
                robot.setY (yCoordinate - 2);
                break;
            case "right":
                robot.setX (xCoordinate + 2);
                break;
            case "left":
                robot.setX (xCoordinate - 2);
                break;
            case "bottom":
                robot.setY(yCoordinate + 2);
                break;
        }
    }


    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_03.png";
    }
}
