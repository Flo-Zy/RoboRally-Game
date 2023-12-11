package SEPee.server.model;

import java.util.ArrayList;
import java.util.List;

import SEPee.server.model.field.Field;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Game {
    private List<List<List<Field>>> gameBoard;
    private ArrayList<Player> playerList;
    private ArrayList<Player> priorityPlayerList;
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

    public Game(ArrayList<Player> playerList, List<List<List<Field>>> gameBoard){
        this.gameBoard = gameBoard;
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

    public Player nextPlayer(){
        if(currentPhase == 0) { // 0: Starting-Phase
            playerIndex++;
            currentPlayer = playerList.get(playerIndex);
            return currentPlayer;
        } else { // 1/2/3: Upgrade/Programming/Activation-Phase
            // select nextPlayer closest to antenna
            playerIndex = 0;
            checkPriorities(gameBoard);
            currentPlayer = priorityPlayerList.get(playerIndex);
            playerIndex++;
            return currentPlayer;
        }
    }

    public ArrayList<Player> checkPriorities(List<List<List<Field>>> gameBoard) {
        priorityPlayerList = new ArrayList<>(playerList.size());
        // determ position of every player's robot
        for (int i=0; i<playerList.size(); i++) {
            playerList.get(i).getRobot().getX();
        }
        return priorityPlayerList;
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