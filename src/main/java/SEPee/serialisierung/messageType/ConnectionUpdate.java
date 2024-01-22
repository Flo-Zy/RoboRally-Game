package SEPee.serialisierung.messageType;

/**
 * class for the ConnectionUpdate message type
 */
public class ConnectionUpdate extends Message{
    private ConnectionUpdate.ConnectionUpdateBody messageBody;

    public ConnectionUpdate(int clientID, boolean isConnected, String action) {
        super("ConnectionUpdate");
        //this.messageType = "PlayerAdded";
        this.messageBody = new ConnectionUpdateBody(clientID, isConnected, action);
    }

    public ConnectionUpdate.ConnectionUpdateBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(ConnectionUpdate.ConnectionUpdateBody messageBody) {
        this.messageBody = messageBody;
    }

    public static class ConnectionUpdateBody {
        private int clientID;

        private boolean isConnected;
        private String action;

        /**
         * class for body of the ConnectionUpdate message type
         */
        public ConnectionUpdateBody(int clientID, boolean isConnected, String action) {
            this.clientID = clientID;
            this.isConnected = isConnected;
            this.action = action;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public boolean isConnected() {
            return isConnected;
        }

        public void setConnected(boolean connected) {
            isConnected = connected;
        }

        public String getAction() {
            return action;
        }

        public void setAction(String action) {
            this.action = action;
        }
    }
}
