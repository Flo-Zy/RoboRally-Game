package SEPee.serialisierung.messageType;

/**
 * class for the CardSelected message type
 */
public class CardSelected extends Message{
    private CardSelectedBody messageBody;

    public CardSelected(int clientID, int register, boolean filled){
        super("CardSelected");
        //this.messageType = "CardSelected";
        this.messageBody = new CardSelectedBody(clientID, register, filled);
    }

    public CardSelectedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CardSelectedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the CardSelected message type
     */
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
