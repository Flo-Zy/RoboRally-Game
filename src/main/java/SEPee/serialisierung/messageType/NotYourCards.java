package SEPee.serialisierung.messageType;

public class NotYourCards extends Message{
    //private String messageType;
    private NotYourCardsBody messageBody;

    public NotYourCards(int clientID, int cardsInHand){
        super("NotYourCards");
        //this.messageType = "NotYourCards";
        this.messageBody = new NotYourCardsBody(clientID, cardsInHand);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public NotYourCardsBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(NotYourCardsBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class NotYourCardsBody{
        private int clientID;

        private int cardsInHand;

        public NotYourCardsBody(int clientID, int cardsInHand){
            this.clientID = clientID;
            this.cardsInHand = cardsInHand;
        }

        public int getClientID(){
            return clientID;
        }

        public void setClientID(int clientID){
            this.clientID = clientID;
        }

        public int getCardsInHand() {
            return cardsInHand;
        }

        public void setCardsInHand(int cardsInHand) {
            this.cardsInHand = cardsInHand;
        }

    }
}
