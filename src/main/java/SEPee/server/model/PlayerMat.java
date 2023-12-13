package SEPee.server.model;

import SEPee.server.model.card.Card;
import SEPee.server.model.card.upgradeCard.UpgradeCard;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class PlayerMat{

    private Card[] register;
    @Getter
    @Setter
    private ArrayList<Card> progDeck;
    private UpgradeCard[] upgradeSlot;
    private UpgradeCard[] temporarySlot;
    private ArrayList<Card> discardPile;

    public PlayerMat(Card[] register, ArrayList<Card> progDeck, UpgradeCard[] upgradeSlot, UpgradeCard[] temporarySlot, ArrayList<Card> discardPile) {
        this.register = register;
        this.progDeck = progDeck;
        this.upgradeSlot = upgradeSlot;
        this.temporarySlot = temporarySlot;
        this.discardPile = discardPile;
    }

}