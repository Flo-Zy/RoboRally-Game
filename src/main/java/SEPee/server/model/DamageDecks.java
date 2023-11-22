package SEPee.server.model;

import SEPee.server.model.card.damageCard.DamageCard;
import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DamageDecks {
    private ArrayList<DamageCard> virusDeck;
    private ArrayList<DamageCard> wurmDeck;
    private ArrayList<DamageCard> trojanHorseDeck;
    private ArrayList<DamageCard> spamDeck;

    public void shuffle(){}
}
