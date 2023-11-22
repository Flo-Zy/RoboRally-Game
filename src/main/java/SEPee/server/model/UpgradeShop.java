package SEPee.server.model;

import SEPee.server.model.card.upgradeCard.UpgradeCard;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpgradeShop {
    private ArrayList<UpgradeCard> upgradeDeck;
    private UpgradeCard[] cardsToPurchase;

    public void shuffle(){}
}
