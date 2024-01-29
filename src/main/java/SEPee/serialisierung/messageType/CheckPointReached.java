package SEPee.serialisierung.messageType;

/**
 * class for the CheckPointReached message type
 * @author Maximilian
 */
public class CheckPointReached extends Message{
    private CheckPointReachedBody messageBody;

    public CheckPointReached(int clientID, int number){
        super("CheckPointReached");
        //this.messageType = "CheckPointReached";
        this.messageBody = new CheckPointReachedBody(clientID, number);
    }

    public CheckPointReachedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CheckPointReachedBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the CheckPointReached message type
     */
    public static class CheckPointReachedBody {
        private int clientID;
        private int number;

        public CheckPointReachedBody(int clientID, int number) {
            this.clientID = clientID;
            this.number = number;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public int getNumber() {
            return number;
        }

        public void setNumber(int number) {
            this.number = number;
        }
    }
}
