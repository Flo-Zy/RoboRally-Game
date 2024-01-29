package SEPee.serialisierung.messageType;

import java.util.ArrayList;

/**
 * class for the CardsYouGotNow message type
 * @author Maximilian
 */
public class CardsYouGotNow extends Message{
    private CardsYouGotNowBody messageBody;

    public CardsYouGotNow(ArrayList<String> cards) {
        super("CardsYouGotNow");
        //this.messageType = "CardsYouGotNow";
        this.messageBody = new CardsYouGotNowBody(cards);
    }

    public CardsYouGotNowBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CardsYouGotNowBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the CardsYouGotNow message type
     */
    public static class CardsYouGotNowBody {
        ArrayList<String> cards;

        public CardsYouGotNowBody(ArrayList<String> cards) {
            this.cards = cards;
        }

        public ArrayList<String> getCards() {
            return cards;
        }

        public void setCards(ArrayList<String> cards) {
            this.cards = cards;
        }

    }
}
