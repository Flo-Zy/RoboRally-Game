package SEPee.client.viewModel.MapController;

import SEPee.server.model.Player;

import java.util.ArrayList;

public abstract class MapController {
    public void avatarAppear(Player player, int x, int y){}
    public void initializeDrawPile(int clientId, ArrayList<String> clientHand) {
    }
}
