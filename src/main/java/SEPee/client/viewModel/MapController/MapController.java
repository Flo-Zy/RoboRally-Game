package SEPee.client.viewModel.MapController;

import SEPee.server.model.Player;
import SEPee.server.model.card.Card;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class MapController {
    public void setCounter1(int counter){};
    public void avatarAppear(Player player, int x, int y){}
    public void initializeDrawPile(int clientId, ArrayList<Card> clientHand){}
    public void initializeRegister(int clientId, ArrayList<Card> clientHand){}
    public void movementPlayed(int clientId, int newX, int newY){}
    public void playerTurn(int clientIdtoTurn, String rotation){}
}
