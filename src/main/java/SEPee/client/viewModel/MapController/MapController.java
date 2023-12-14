package SEPee.client.viewModel.MapController;

import SEPee.server.model.Player;
import SEPee.server.model.card.Card;

import java.util.ArrayList;

public abstract class MapController {
    public void avatarAppear(Player player, int x, int y){}
    public void initializeDrawPile(int clientId, ArrayList<Card> clientHand){}
    public void initializeRegister(int clientId, ArrayList<Card> clientHand){}
    public void movementPlayed(int clientId, int newX, int newY){}

    public void playerTurn(int clientIdtoTurn, String rotation){}



    }
