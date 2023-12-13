package SEPee.server.model.card.progCard;
import SEPee.server.model.ClientHandler;
import SEPee.server.model.Robot;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Move1 extends ProgCard {



    public Move1() {
        super();
    }

    public static void makeEffect(Robot robot){
        // orientation von robot
        String orientation = robot.getOrientation();
        int xCoordinate = robot.getX();
        int yCoordinate = robot.getY();

        switch (orientation){
            case "top":
                robot.setY (yCoordinate - 1);
                break;
            case "right":
                robot.setX (xCoordinate + 1);
                break;
            case "left":
                robot.setX (xCoordinate - 1 );
                break;
            case "bottom":
                robot.setY(yCoordinate + 1);
                break;
        }
    }
    @Override
    public String getImageUrl() {
        return "/boardElementsPNGs/Custom/ProgrammierKarten/Blau/Untitled-6_01.png";
    }
}
