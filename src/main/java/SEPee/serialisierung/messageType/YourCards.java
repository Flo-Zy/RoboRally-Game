package SEPee.serialisierung.messageType;

public class YourCards extends Message{
    // private String messageType;
    private YourCardsBody messageBody;

    public YourCards(String[] cardsInHand){
        super("YourCards");
        // this.messageType = "YourCards";
        this.messageBody = new YourCardsBody(cardsInHand);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
     */

    public YourCardsBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(YourCardsBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class YourCardsBody {
        private String[] cardsInHand;

        public YourCardsBody(String[] cardsInHand){
            this.cardsInHand = cardsInHand;
        }

        public String[] getCardsInHand(){
            return cardsInHand;
        }

        public void setCardsInHand(String[] cardsInHand){
            this.cardsInHand = cardsInHand;
        }

    }
}
