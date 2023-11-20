package SEPee.server.model;

import SEPee.server.model.card.progCard.ProgCard;
import SEPee.server.model.card.specialCard.SpecialCard;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private GameBoard gameboard;
    private Player[] playerList;
    private Phase currentPhase;
    private ArrayList<ProgCard> initialProgDeck;
    private DamageDecks damageDecks;
    private UpgradeShop upgradeShop;
    private ArrayList<SpecialCard> specialCardsDeck;

    public void checkPriorities() {
    }

    public void activateElements() {
    }

    public void setGameBoard(int boardId) {
    }

    public void shuffle() {
    }

    /** TIMER:
     long start,end;
     double tim;
     start=System.currentTimeMillis();
     end=System.currentTimeMillis();
     tim=(end-start)/1000.0;
     */
}