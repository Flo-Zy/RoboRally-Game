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
    private UpgradeCard[] permanentSlot;
    private UpgradeCard[] temporarySlot;
    private ArrayList<Card> discardPile;

    public PlayerMat(ArrayList<Card> progDeck) {
        this.register = new Card[5];
        this.progDeck = progDeck;
        //this.permanentSlot = new UpgradeCard[3];
        //this.temporarySlot = new UpgradeCard[3];
        this.discardPile = new ArrayList<>();
    }

}