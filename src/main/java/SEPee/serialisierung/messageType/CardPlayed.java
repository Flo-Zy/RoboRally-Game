package SEPee.serialisierung.messageType;

public class CardPlayed extends Message{
    //private String messageType;
    private CardPlayedBody messageBody;

    public CardPlayed(int clientID, String card){
        super("CardPlayed");
        //this.messageType = "CardPlayed";
        this.messageBody = new CardPlayedBody(clientID, card);
    }

    /*
    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

     */

    public CardPlayedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CardPlayedBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class CardPlayedBody{
        private int clientID;

        private String card;

        public CardPlayedBody(int clientID, String card){
            this.clientID = clientID;
            this.card = card;
        }

        public int getClientID(){
            return clientID;
        }

        public void setClientID(int clientID){
            this.clientID = clientID;
        }

        public String getCard() {
            return card;
        }

        public void setCard(String card) {
            this.card = card;
        }

    }
}
