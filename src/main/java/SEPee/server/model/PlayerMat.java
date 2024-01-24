package SEPee.server.model;

import SEPee.server.model.card.Card;

import java.util.ArrayList;
import lombok.Getter;
import lombok.Setter;

/**
 * the class representing a player's player mat
 */
public class PlayerMat{
    @Getter
    @Setter
    private String[] register;
    @Getter
    @Setter
    private ArrayList<Card> progDeck;
    @Getter
    @Setter
    private ArrayList<String> discardPile;
    @Getter
    @Setter
    private int numRegister = 0;
    @Getter
    @Setter
    private ArrayList<String> clientHand;
    @Getter
    @Setter
    private int tokenCount;
    @Getter
    @Setter
    private ArrayList<String> receivedDamageCards;

    public PlayerMat(ArrayList<Card> progDeck) {
        this.register = new String[5];
        this.progDeck = progDeck;
        this.discardPile = new ArrayList<>();
        this.clientHand = new ArrayList<>();
        this.tokenCount = 0;
        this.receivedDamageCards = new ArrayList<>();
    }

    public String getRegisterIndex(int index){
        return register[index];
    }

    public void setRegisterIndex(int index, String card){
        register[index] = card;
    }

    /**
     * fills an empty register with a random card
     * @param card the card to fill the register with
     */
    public void fillEmptyRegister(String card){
        for(int i = 0; i < 5; i++){
            if(register[i] == null){
                register[i] = card;
                break;
            }
        }
    }

    /**
     * clears the registers
     */
    public void clearRegister(){
        for(int i = 0; i < 5; i++){
            register[i] = null;
        }
    }

    /**
     * gets the register size
     * @return the register's size
     */
    public int registerSize(){
        int size = 0;
        for(int i = 0; i < 5; i++){
            if(register[i] != null){
                size++;
            }
        }
        return size;
    }

}