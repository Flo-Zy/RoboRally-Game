package SEPee.serialisierung.messageType;

/**
 * class for body of the NotYourCards message type
 * @author Franziska
 */
public class NotYourCards extends Message{
    private NotYourCardsBody messageBody;

    public NotYourCards(int clientID, int cardsInHand){
        super("NotYourCards");
        this.messageBody = new NotYourCardsBody(clientID, cardsInHand);
    }

    public NotYourCardsBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(NotYourCardsBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the NotYourCards message type
     */
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
