package SEPee.serialisierung.messageType;

/**
 * class for the Energy message type
 */
public class Energy extends Message{
    private EnergyBody messageBody;

    public Energy(int clientID, int count, String source){
        super("Energy");
        //this.messageType = "Energy";
        this.messageBody = new EnergyBody(clientID, count, source);
    }

    public EnergyBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(EnergyBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class EnergyBody {
        private int clientID;
        private int count;
        private String source;

        /**
         * class for body of the Energy message type
         */
        public EnergyBody(int clientID, int count, String source) {
            this.clientID = clientID;
            this.count = count;
            this.source = source;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }
    }
}
