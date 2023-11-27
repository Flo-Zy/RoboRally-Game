package SEPee.serialisierung.messageType;

public class CardSelected {
    private String messageType;
    private CardSelectedBody messageBody;

    public CardSelected(int clientID, int register, boolean filled){
        this.messageType = "CardSelected";
        this.messageBody = new CardSelectedBody(clientID, register, filled);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public CardSelectedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CardSelectedBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class CardSelectedBody {
        private int clientID;
        private int register;
        private boolean filled;

        public CardSelectedBody(int clientID, int register, boolean filled) {
            this.clientID = clientID;
            this.register = register;
            this.filled = filled;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public int getRegister() {
            return register;
        }

        public void setRegister(int register) {
            this.register = register;
        }

        public boolean isFilled() {
            return filled;
        }

        public void setFilled(boolean filled) {
            this.filled = filled;
        }
    }
}
