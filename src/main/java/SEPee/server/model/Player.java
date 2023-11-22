package SEPee.server.model;

import SEPee.server.model.card.Card;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Player {
    private String name;
    private int id;
    private Robot robot;
    private Card[] hand;
    private PlayerMat playerMat;
    private int checkpointTokens;

    public void draw(){}
    public void fillRegister(Card[] chosenCards){}
    public void discard(){}
    public void examineDamageCards(){}
    public void rebootRobot(){}
}
