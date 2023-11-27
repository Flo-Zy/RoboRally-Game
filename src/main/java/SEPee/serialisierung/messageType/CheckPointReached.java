package SEPee.serialisierung.messageType;

public class CheckPointReached {
    private String messageType;
    private CheckPointReachedBody messageBody;

    public CheckPointReached(int clientID, int number){
        this.messageType = "CheckPointReached";
        this.messageBody = new CheckPointReachedBody(clientID, number);
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public CheckPointReachedBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(CheckPointReachedBody messageBody) {
        this.messageBody = messageBody;
    }

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
