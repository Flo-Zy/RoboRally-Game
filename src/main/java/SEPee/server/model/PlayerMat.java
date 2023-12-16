package SEPee.server.model;

import SEPee.server.model.card.Card;
import SEPee.server.model.card.upgradeCard.UpgradeCard;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

public class PlayerMat{
    @Getter
    @Setter
    private ArrayList<String> register;
    @Getter
    @Setter
    private ArrayList<Card> progDeck;
    private UpgradeCard[] permanentSlot;
    private UpgradeCard[] temporarySlot;
    @Getter
    @Setter
    private ArrayList<String> discardPile;
    @Getter
    @Setter
    private int numRegister = 0;

    public PlayerMat(ArrayList<Card> progDeck) {
        this.register = new ArrayList<>(5);
        this.progDeck = progDeck;
        //this.permanentSlot = new UpgradeCard[3];
        //this.temporarySlot = new UpgradeCard[3];
        this.discardPile = new ArrayList<>();
    }

}