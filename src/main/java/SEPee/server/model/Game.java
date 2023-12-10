package SEPee.server.model;

import SEPee.server.model.card.progCard.ProgCard;
import SEPee.server.model.card.specialCard.SpecialCard;

import java.util.ArrayList;
import java.util.List;

import SEPee.server.model.field.Field;
import SEPee.server.model.gameBoard.GameBoard;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private List<List<List<Field>>> gameboard;
    private ArrayList<Player> playerList;
    private int playerIndex;
    private int currentPhase;
    private Player currentPlayer;
    //private ArrayList<ProgCard> initialProgDeck;
    private int virus;
    private int spam;
    private int trojanHorse;
    private int wurm;
    //private DamageDecks damageDecks;
    //private UpgradeShop upgradeShop;
    //private ArrayList<SpecialCard> specialCardsDeck;

    public Game(ArrayList<Player> playerList, List<List<List<Field>>> gameboard){
        this.playerList = playerList;
        this.playerIndex = 0;
        this.currentPhase = 0;
        this.currentPlayer = playerList.get(playerIndex);
        this.spam = 38;
        this.virus = 18;
        this.trojanHorse = 12;
        this.wurm = 6;
    }

    public void nextCurrentPhase(){
        if(currentPhase == 3){
            currentPhase = 1;
        }else{
            currentPhase++;
        }
    }

    public void nextPlayer(){
        if(currentPhase == 0){
            playerIndex++;
            currentPlayer = playerList.get(playerIndex);
        }else{

        }

    }

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