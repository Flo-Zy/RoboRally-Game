package SEPee.serialisierung.messageType;

public class CardsYouGotNow extends Message{
    //private String messageType;
    private CardsYouGotNowBody messageBody;

    public CardsYouGotNow(String[] cards) {
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
        String[] cards;

        public CardsYouGotNowBody(String[] cards) {
            this.cards = cards;
        }

        public String[] getCards() {
            return cards;
        }

        public void setCards(String[] cards) {
            this.cards = cards;
        }

    }
}
