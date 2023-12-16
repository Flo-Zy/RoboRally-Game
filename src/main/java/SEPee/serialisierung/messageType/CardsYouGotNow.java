package SEPee.serialisierung.messageType;

import java.util.ArrayList;

public class CardsYouGotNow extends Message{
    //private String messageType;
    private CardsYouGotNowBody messageBody;

    public CardsYouGotNow(ArrayList<String> cards) {
        super("CardsYouGotNow");
        //this.messageType = "CardsYouGotNow";
        this.messageBody = new CardsYouGotNowBody(cards);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public CardsYouGotNowBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CardsYouGotNowBody messageBody) {
        this.messageBody = messageBody;
    }

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
