package SEPee.serialisierung.messageType;

public class NotYourCards {
    private String messageType;
    private NotYourCardsBody messageBody;

    public NotYourCards(int clientID, int cardsInHand){
        this.messageType = "NotYourCards";
        this.messageBody = new NotYourCardsBody(clientID, cardsInHand);
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
