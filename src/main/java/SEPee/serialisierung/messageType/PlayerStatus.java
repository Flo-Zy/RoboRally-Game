package SEPee.serialisierung.messageType;

/**
 * class for body of the PlayerStatus message type
 */
public class PlayerStatus extends Message{
    private PlayerStatusBody messageBody;

    public PlayerStatus(int clientID, boolean ready) {
        super ("PlayerStatus");
        //this.messageType = "PlayerStatus";
        this.messageBody = new PlayerStatusBody(clientID, ready);
    }

    public PlayerStatusBody getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(PlayerStatusBody messageBody) {
        this.messageBody = messageBody;
    }

    /**
     * class for body of the PlayerStatus message type
     */
    public static class PlayerStatusBody {
        private int clientID;
        private boolean ready;

        public PlayerStatusBody(int clientID, boolean ready) {
            this.clientID = clientID;
            this.ready = ready;
        }

        public int getClientID() {
            return clientID;
        }

        public void setClientID(int clientID) {
            this.clientID = clientID;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
    }
}