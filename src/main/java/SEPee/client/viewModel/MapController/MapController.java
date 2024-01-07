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
    public void initializeRegisterAI(int clientId, ArrayList<Card> clientHand){}
    public void setRegisterVisibilityFalse(){}
    public void fillEmptyRegister(ArrayList<Card> nextCards){}
    public void movementPlayed(int clientId, int newX, int newY){}
    public void playerTurn(int clientIdtoTurn, String rotation){}
    public void setCheckPointImage(String imageUrl) {}
    class Zahlen{
        public int hand;
        public int register;

        Zahlen(int hand, int register){
            this.hand = hand;
            this.register = register;
        }
    }

}
