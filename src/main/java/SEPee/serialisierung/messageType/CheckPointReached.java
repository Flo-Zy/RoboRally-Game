package SEPee.serialisierung.messageType;

public class CheckPointReached {
    private String messageType;
    private CheckPointReachedBody messageBody;

    public CheckPointReached(int clientID, int number){
        this.messageType = "CheckPointReached";
        this.messageBody = new CheckPointReachedBody(clientID, number);
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
