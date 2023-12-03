package SEPee.server.model;

import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String name;
    private int id;
    private boolean ready;
    private Robot robot;
    private int figure;
    private Card[] hand;
    private PlayerMat playerMat;
    private int checkpointTokens;

    public Player(String name, int id, int figure){
        this.name=name;
        this.id=id;
        this.figure=figure;
        this.ready = false;
    }

    public void draw(){}
    public void fillRegister(Card[] chosenCards){}
    public void discard(){}
    public void examineDamageCards(){}
    public void rebootRobot(){}
}
