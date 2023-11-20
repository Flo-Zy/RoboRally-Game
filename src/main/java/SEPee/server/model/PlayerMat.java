package SEPee.server.model;

import SEPee.server.model.card.Card;
import SEPee.server.model.card.upgradeCard.UpgradeCard;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PlayerMat{
    private Card[] register;
    private ArrayList<Card> progDeck;
    private UpgradeCard[] upgradeSlot;
    private UpgradeCard[] temporarySlot;
    private ArrayList<Card> discardPile;
}