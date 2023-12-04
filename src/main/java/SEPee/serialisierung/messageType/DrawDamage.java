package SEPee.serialisierung.messageType;

public class DrawDamage extends Message{
    private DrawDamage.DrawDamageBody messageBody;

    public DrawDamage(int clientID, String[] cards) {
        super("DrawDamage");
        this.messageBody = new DrawDamage.DrawDamageBody(clientID, cards);
    }

    public DrawDamage.DrawDamageBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(DrawDamage.DrawDamageBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class DrawDamageBody {
        private int clientID;
        private String[] cards;

        public DrawDamageBody(int clientID, String[] cards) {
            this.clientID = clientID;
            this.cards = cards;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public String[] getCards() {
            return cards;
        }

        public void setCards(String[] cards) {
            this.cards = cards;
        }
    }
}
