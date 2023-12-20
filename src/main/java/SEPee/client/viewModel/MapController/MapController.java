package SEPee.client.viewModel.MapController;

import SEPee.server.model.Player;
import SEPee.server.model.Robot;
import SEPee.server.model.card.Card;

import java.util.ArrayList;

public abstract class MapController {
    public void setCounter1(int counter){};
    public void avatarAppear(Player player, int x, int y){}
    public void initializeDrawPile(int clientId, ArrayList<Card> clientHand){}
    public void initializeRegister(int clientId, ArrayList<Card> clientHand){}
    public void setRegisterVisibilityFalse(){}
    public void fillEmptyRegister(ArrayList<Card> nextCards){}
    public void movementPlayed(int clientId, int newX, int newY){}
    public void playerTurn(int clientIdtoTurn, String rotation){}

    public String checkRebootConditions (Robot robot) {
        int xCoordinate = robot.getX();
        int yCoordinate = robot.getY();
        String rebootTo = "continue";

        if ((yCoordinate < 0 && xCoordinate < 3) || (xCoordinate < 0) || (yCoordinate > 10 && xCoordinate < 3)) {
            // top left condition, left condition, bottom left condition, weil starting board links ist
            rebootTo = "startingPoint";
        } else if ((yCoordinate < 0 && xCoordinate >= 3) || (xCoordinate > 12) || (yCoordinate > 10 && xCoordinate >= 3)) {
            // top right, right, bottom right conditions
            rebootTo = "rebootField";
        }
        return rebootTo;
    }

    class Zahlen{
        public int hand;
        public int register;

        Zahlen(int hand, int register){
            this.hand = hand;
            this.register = register;
        }
    }
}
