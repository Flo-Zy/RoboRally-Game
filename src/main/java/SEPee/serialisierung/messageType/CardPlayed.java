package SEPee.serialisierung.messageType;

public class CardPlayed {
    private String messageType;
    private CardPlayedBody messageBody;

    public CardPlayed(int clientID, String card){
        this.messageType = "CardPlayed";
        this.messageBody = new CardPlayedBody(clientID, card);
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
